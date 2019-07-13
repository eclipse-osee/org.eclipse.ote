#!/bin/sh
echo
pwd
echo
echo "mvn clean verify -Dosee.base.p2=$1 -Dosee-build-stage=nothing -Dno-extern-repos"
echo
mvn clean verify -Dosee.base.p2=$1 -Dosee-build-stage=nothing -Dno-extern-repos
