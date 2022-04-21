#!/bin/sh
echo
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
echo 'JAVA_HOME="/c/Tools/jdk-11.0.7/jdk-11.0.7"'
JAVA_HOME="/c/Tools/jdk-11.0.7/jdk-11.0.7"
echo "mvn clean verify -Dosee-build-stage=nothing -Dote.dependencies.p2=file://$BASE_AREA/../org.eclipse.ote.build/dependencies/ote.dependencies.p2/target/repository/ -Dosee.ote.p2=file://$BASE_AREA/../org.eclipse.osee.ote.p2/target/repository -Dorg.eclipse.ote.p2=file://$BASE_AREA/../org.eclipse.ote.p2/target/repository -Dno-extern-repos"
mvn clean verify -Dosee-build-stage=nothing -Dote.dependencies.p2=file://$BASE_AREA/../org.eclipse.ote.build/dependencies/ote.dependencies.p2/target/repository/ -Dosee.ote.p2=file://$BASE_AREA/../org.eclipse.osee.ote.p2/target/repository -Dorg.eclipse.ote.p2=file://$BASE_AREA/../org.eclipse.ote.p2/target/repository -Dno-extern-repos
