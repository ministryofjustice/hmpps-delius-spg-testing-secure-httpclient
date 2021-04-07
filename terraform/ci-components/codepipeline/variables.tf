variable "tags" {
  type    = map(string)
  default = {}
}

variable "region" {
  description = "The AWS region."
}

variable "remote_state_bucket_name" {
  description = "Terraform remote state bucket name"
}

variable "dev_branch_name" {
  type = string
}

variable "release_branch_name" {
  type = string
}


