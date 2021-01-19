locals {
  prefix = "spgw"

  http_client_dev_pipeline_name     = "delius-${local.prefix}-http-client-dev"
  http_client_release_pipeline_name = "delius-${local.prefix}-http-client-release"

  iam_role_arn     = data.terraform_remote_state.common.outputs.codebuild_info["iam_role_arn"]
  cache_bucket     = data.terraform_remote_state.common.outputs.codebuild_info["build_cache_bucket"]
  log_group_name   = data.terraform_remote_state.common.outputs.codebuild_info["log_group"]
  artefacts_bucket = data.terraform_remote_state.common.outputs.codebuild_info["artefacts_bucket"]
  tags             = data.terraform_remote_state.common.outputs.tags
}