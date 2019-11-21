module "config_rule" {
  source = "./custom_rule_module"

  name = "${var.deployment_name}-dynamodb-secure-transport-check"
  additional_policies = ["arn:aws:iam::aws:policy/IAMReadOnlyAccess"]
  s3 = {
    bucket = aws_s3_bucket.s3.id
    key    = aws_s3_bucket_object.rules_jar.key
  }
  handler = "com.scottlogic.compliance.dynamodb.SecureTransportCheck"
  resource_types = ["AWS::IAM::User"]
}