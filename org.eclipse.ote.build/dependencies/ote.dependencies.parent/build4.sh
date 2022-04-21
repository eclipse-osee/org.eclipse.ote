#!/bin/sh
echo
CLIENT_SITE="http://ci.eclipse.org/osee/job/osee_dev/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.client.all.p2/target/repository/"
SERVER_SITE="http://ci.eclipse.org/osee/job/osee_dev/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.server.p2/target/repository/"
DEP_SITE="http://ci.eclipse.org/osee/job/osee_dev/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.dep.p2/target/repository/"

echo 'JAVA_HOME="c/Tools/jdk-11.0.7/jdk-11.0.7"'
echo "mvn clean verify -P-mirror-other-sites -V -B -Dosee-client-site=${CLIENT_SITE} -Dosee-server-site=${SERVER_SITE} -Dosee-dep-site=${DEP_SITE}"

JAVA_HOME="/c/Tools/jdk-11.0.7/jdk-11.0.7" 
mvn clean verify -P-mirror-other-sites -V -B -Dosee-client-site=${CLIENT_SITE} -Dosee-server-site=${SERVER_SITE} -Dosee-dep-site=${DEP_SITE}
