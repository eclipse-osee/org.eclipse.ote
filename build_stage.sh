#!/bin/bash

SHELL_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

source ${SHELL_DIR}/../ote.common/export_dev_build.sh

cd ${SHELL_DIR}/org.eclipse.ote.parent
set -x
time mvn ${DEV_BUILD} 
