#!/bin/sh
echo
echo "mvn clean verify -P-mirror-other-sites -V -B -Dosee-client-site=http://ci.eclipse.org/osee/job/osee_dev_0.26.0/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.client.all.p2/target/repository/ -Dosee-server-site=http://ci.eclipse.org/osee/job/osee_dev_0.26.0/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.server.p2/target/repository/"
mvn clean verify -P-mirror-other-sites -V -B -Dosee-client-site=http://ci.eclipse.org/osee/job/osee_dev_0.26.0/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.client.all.p2/target/repository/ -Dosee-server-site=http://ci.eclipse.org/osee/job/osee_dev_0.26.0/lastSuccessfulBuild/artifact/org.eclipse.osee/plugins/org.eclipse.osee.server.p2/target/repository/
