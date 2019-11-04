variable "jar_name" {
  default = "../target/${project.artifactId}-${project.version}.r${buildNumber}.jar"
}

variable "deployment_name" {
  type = "string"
  default = "dev"
}
