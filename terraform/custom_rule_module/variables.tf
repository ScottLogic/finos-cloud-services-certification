variable "name" {
  description = "A short name for this rule"
  type        = "string"
}

variable "s3" {
  description = "S3 configuration for the lambda jar"
  type        = "map"

  default = {
    bucket = "none"
    key    = "none"
  }
}

variable "handler" {
  description = "Fully qualified name describing the class or method to use as the Lambda handler"
  type        = "string"
  default     = "com.scottlogic.compliance.dynamodb.SecureTransportCheck"
}

variable "resource_types" {
  description = "A list of resource types which should trigger the config rule"
  type        = "list"
  default     = ["AWS::IAM::User"]
}

variable additional_policies {
  description = "A list of policy ARNs to attach to the role used by the lambda"
  type        = "list"
  default     = []
}