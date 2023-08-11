#!/bin/sh
echo
pwd
echo
echo "mvn clean verify -Dote.dependencies.p2=$1 -Dosee-build-stage=nothing -Dno-extern-repos"
echo
mvn clean verify -Dote.dependencies.p2=$1 -Dosee-build-stage=nothing -Dno-extern-repos
