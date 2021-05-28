#!/bin/sh
echo
CLIENT_SITE="file:./../../../../org.eclipse.osee/plugins/org.eclipse.osee.client.all.p2/target/repository"
SERVER_SITE="file:./../../../../org.eclipse.osee/plugins/org.eclipse.osee.server.p2/target/repository/"
DEP_SITE="file:./../../../../org.eclipse.osee/plugins/org.eclipse.osee.dep.p2/target/repository/"

echo "mvn clean verify -P-mirror-other-sites -V -B -Dosee-client-site=${CLIENT_SITE} -Dosee-server-site=${SERVER_SITE} -Dosee-dep-site=${DEP_SITE}"

mvn clean verify -P-mirror-other-sites -V -B -Dosee-client-site=${CLIENT_SITE} -Dosee-server-site=${SERVER_SITE} -Dosee-dep-site=${DEP_SITE}
