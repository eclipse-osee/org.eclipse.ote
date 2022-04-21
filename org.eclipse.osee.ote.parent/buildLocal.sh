#!/bin/sh
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
JAVA_HOME="/c/Tools/jdk-11.0.7/jdk-11.0.7" 
echo "mvn clean verify -Dote.dependencies.p2=file://$BASE_AREA/../org.eclipse.ote.build/dependencies/ote.dependencies.p2/target/repository/ -Dosee-build-stage=nothing -Dno-extern-repos" 
mvn clean verify -Dote.dependencies.p2=file://$BASE_AREA/../org.eclipse.ote.build/dependencies/ote.dependencies.p2/target/repository/ -Dosee-build-stage=nothing -Dno-extern-repos
