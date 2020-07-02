#!/bin/sh
echo
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
GIT_ROOT=$DIR/../..
cd $GIT_ROOT
pwd
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
echo
cd org.eclipse.ote/org.eclipse.ote.parent
echo "cd org.eclipse.ote/org.eclipse.ote.parent"
echo
echo "mvn clean verify -Dote.dependencies.p2=$1 -Dosee.ote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.osee.ote.p2/target/repository -Dosee-build-stage=nothing -Dno-extern-repos"
echo
mvn clean verify -Dote.dependencies.p2=$1 -Dosee.ote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.osee.ote.p2/target/repository -Dosee-build-stage=nothing -Dno-extern-repos
