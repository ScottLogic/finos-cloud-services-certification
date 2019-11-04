
resource "aws_s3_bucket" "s3" {
  bucket = "terraform-test-config-rules"
  acl = "private"
  region = "eu-west-2"
}

resource "aws_s3_bucket_object" "rules_jar" {
  bucket = "${aws_s3_bucket.s3.id}"
  key = "rules.jar"
  source = var.JAR_NAME
}

