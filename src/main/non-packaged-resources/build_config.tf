variable "jar_name" {
  default = "../target/${project.artifactId}-${project.version}.r${buildNumber}.jar"
}

variable "deployment_name" {
  type = "string"
  description = "A name prefix used to make resources unique to this deployment."
}
