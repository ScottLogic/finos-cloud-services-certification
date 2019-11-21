resource "aws_iam_role" "lambda_role" {
  name = "${var.name}-lambda-iam-role-for-rules"

  path = "/service-role/"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Effect": "Allow",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      }
    }
  ]
}
EOF
}

resource "aws_iam_role_policy_attachment" "role_additional_policy" {
  for_each = toset(var.additional_policies)
  policy_arn = each.key
  role = aws_iam_role.lambda_role.name
}

resource "aws_iam_role_policy_attachment" "role_lambda_policy" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
  role = aws_iam_role.lambda_role.name
}

resource "aws_iam_role_policy_attachment" "role_config_policy" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSConfigRulesExecutionRole"
  role = aws_iam_role.lambda_role.name
}

resource "aws_lambda_function" "lambda_function" {
  s3_bucket = var.s3.bucket
  s3_key = var.s3.key
  function_name = "${var.name}-lambda-function"
  handler = var.handler

  runtime = "java8"

  role = aws_iam_role.lambda_role.arn

  memory_size = 512
  timeout = 15
}

resource "aws_lambda_permission" "lambda_permission" {
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.lambda_function.arn
  principal     = "config.amazonaws.com"
  statement_id  = "AllowExecutionFromConfig"
}

resource "aws_config_config_rule" "config_rule" {
  name = "${var.name}-config_rule"
  source {
    owner = "CUSTOM_LAMBDA"
    source_identifier = aws_lambda_function.lambda_function.arn
    source_detail {
      message_type = "ScheduledNotification"
      maximum_execution_frequency = "TwentyFour_Hours"
    }
    source_detail {
      message_type = "ConfigurationItemChangeNotification"
    }
  }
  scope {
    compliance_resource_types = var.resource_types
  }
}
