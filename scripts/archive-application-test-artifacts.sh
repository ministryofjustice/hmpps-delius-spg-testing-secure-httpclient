#!/usr/bin/env bash

set -ex

now=$(date +"%m-%d-%Y")

aws s3 cp build/test-results s3://${test_results_bucket}/spgw/${now}/${project_name}/build-${CODEBUILD_BUILD_NUMBER}-unit-test-results  --recursive
aws s3 cp build/reports/tests s3://${test_results_bucket}/spgw/${now}/${project_name}/build-${CODEBUILD_BUILD_NUMBER}-unit-test-reports --recursive