#!/usr/bin/env bash
set -e


source ${HMPPS_BUILD_WORK_DIR}/ci_env_configs/dev.properties
cd ${HMPPS_BUILD_WORK_DIR}/ci-components/codepipeline


env | sort


if [ -d .terraform ]; then
  rm -rf .terraform
fi
sleep 1
terragrunt destroy


set +e
