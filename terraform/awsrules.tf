provider "aws" {
  region = "eu-west-2"
}

resource "aws_s3_bucket" "s3" {
  bucket = "terraform-test-config-rules"
  acl = "private"
  region = "eu-west-2"
}

resource "aws_s3_bucket_object" "rules_jar" {
  bucket = "${aws_s3_bucket.s3.id}"
  key = "rules.jar"
  source = "../target/aws-rules-1.0-SNAPSHOT.jar"
}

resource "aws_iam_role" "lambda_iam_test" {
  name = "lambda_iam_role_for_rules_test"

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

resource "aws_iam_role_policy_attachment" "role_iam_policy" {
  policy_arn = "arn:aws:iam::aws:policy/IAMReadOnlyAccess"
  role = "${aws_iam_role.lambda_iam_test.name}"
}

resource "aws_iam_role_policy_attachment" "role_lambda_policy" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
  role = "${aws_iam_role.lambda_iam_test.name}"
}

resource "aws_iam_role_policy_attachment" "role_config_policy" {
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSConfigRulesExecutionRole"
  role = "${aws_iam_role.lambda_iam_test.name}"
}

resource "aws_lambda_function" "lambda_test_1" {
  s3_bucket = "${aws_s3_bucket.s3.id}"
  s3_key = "${aws_s3_bucket_object.rules_jar.key}"
  function_name = "lambda_rules_test_1"
  handler = "com.scottlogic.test.SecureTransportTester::handle"

  runtime = "java8"

  role = "${aws_iam_role.lambda_iam_test.arn}"

  memory_size = 512
  timeout = 15
}

resource "aws_config_config_rule" "config_rule_1" {
  name = "config_rule_test_1"
  source {
    owner = "CUSTOM_LAMBDA"
    source_identifier = "${aws_lambda_function.lambda_test_1.arn}"
    source_detail {
      message_type = "ScheduledNotification"
      maximum_execution_frequency = "TwentyFour_Hours"
    }
  }
}
