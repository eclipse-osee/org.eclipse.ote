#!/bin/sh
echo
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
echo "mvn clean verify -Dosee-build-stage=nothing -Dosee.base.p2=$1 -Dosee.ote.p2=file://$BASE_AREA/../org.eclipse.osee.ote.p2/target/repository -Dorg.eclipse.ote.p2=file://$BASE_AREA/../org.eclipse.ote.p2/target/repository -Dno-extern-repos"
mvn clean verify -Dosee-build-stage=nothing -Dosee.base.p2=$1 -Dosee.ote.p2=file://$BASE_AREA/../org.eclipse.osee.ote.p2/target/repository -Dorg.eclipse.ote.p2=file://$BASE_AREA/../org.eclipse.ote.p2/target/repository -Dno-extern-repos
