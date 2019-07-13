#!/bin/sh
#get the folder of the script
export DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
GIT_ROOT=$DIR/../..
cd $GIT_ROOT
pwd
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
echo found $BASE_AREA
echo
cd -
echo
echo "mvn clean verify -Dote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.ote.p2/target/repository -Dosee.ote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.osee.ote.p2/target/repository -Dote.runtime.p2=$1"
echo
mvn clean verify -Dote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.ote.p2/target/repository -Dosee.ote.p2=file://$BASE_AREA/org.eclipse.ote/org.eclipse.osee.ote.p2/target/repository -Dote.runtime.p2=$1
