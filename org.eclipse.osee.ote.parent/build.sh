#!/bin/sh
echo
pwd
echo
echo "mvn clean verify -Dote.dependencies.p2=$1 -Dosee-build-stage=nothing -Dno-extern-repos"
echo
JAVA_HOME=/c/Tools/jdk-11.0.7/jdk-11.0.7
mvn clean verify -Dote.dependencies.p2=$1 -Dosee-build-stage=nothing -Dno-extern-repos
