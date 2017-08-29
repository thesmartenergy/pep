#!/bin/bash

cd pep-jersey-website
mvn clean package -P docs
rc=$?
if [[ $rc -ne 0 ]] ; then
  echo 'could not run maven'; exit $rc
fi
cd ..

ssh ci.emse.fr "cd /var/www/glassfish-apps/ 
rm pep-platform-old.war"

ssh ci.emse.fr "cd /var/www/glassfish-apps/ 
mv pep-platform.war pep-platform-old.war"

echo "send pep-platform.war on the server"
scp "pep-jersey-website/target/pep-platform.war" "root@ci.emse.fr:/var/www/glassfish-apps/pep-platform.war"

echo ""
echo "deploy pep-platform.war on the server"
ssh ci.emse.fr "/opt/glassfish4/glassfish/bin/asadmin login && /opt/glassfish4/glassfish/bin/asadmin deploy --force /var/www/glassfish-apps/pep-platform.war"

echo "ok"
echo
