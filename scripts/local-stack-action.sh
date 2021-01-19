#!/usr/bin/env bash
set -e

$(pwd)/scripts/clone-engineering-platform-env-configs.sh


$(pwd)/scripts/terraform-local-builder.sh terraform-local-${1}.sh
