#!/bin/sh
echo
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
GIT_ROOT=$DIR/../..
cd $GIT_ROOT
pwd
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
echo
cd org.eclipse.ote/org.eclipse.ote.simple.parent
echo "cd org.eclipse.ote/org.eclipse.ote.simple.parent"
echo
echo "mvn clean verify -Dote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.ote.p2/target/repository -Dote.dependencies.p2=$1"
echo
mvn clean verify -Dote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.ote.p2/target/repository -Dote.dependencies.p2=$1
