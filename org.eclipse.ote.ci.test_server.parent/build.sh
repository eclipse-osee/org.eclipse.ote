#!/bin/sh
echo
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
GIT_ROOT=$DIR/../..
cd $GIT_ROOT
pwd
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
echo
cd org.eclipse.ote/org.eclipse.ote.ci.test_server.parent
echo "org.eclipse.ote/org.eclipse.ote.ci.test_server.parent"
echo
echo "mvn clean verify  -Dote.simple.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.ote.simple.p2/target/repository -Dote.dependencies.p2=$1 -Dote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.ote.p2/target/repository"
echo
mvn clean verify  -Dote.simple.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.ote.simple.p2/target/repository -Dote.dependencies.p2=$1 -Dote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.ote.p2/target/repository
