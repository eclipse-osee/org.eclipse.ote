#!/bin/sh
export BASE_AREA=`pwd | sed 's/\/c\//\/c\:\//'`
echo "mvn clean verify -Dosee.base.p2=file://$BASE_AREA/../org.eclipse.ote.build/dependencies/ote.dependencies.p2/target/repository/" 
mvn clean verify -Dosee.base.p2=file://$BASE_AREA/../org.eclipse.ote.build/dependencies/ote.dependencies.p2/target/repository/ 