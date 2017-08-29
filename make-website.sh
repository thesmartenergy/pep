#!/bin/bash

websitepath="pep-website/target/pep.war"

echo "run maven"
cd pep-website
mvn clean package -P docs
rc=$?
if [[ $rc -ne 0 ]] ; then
  echo 'could not run maven'; exit $rc
fi
cd ..

echo ""
echo "delete existing pep.war on the server"
ssh ci.emse.fr "cd /var/www/glassfish-apps/ 
rm pep-old.war"

ssh ci.emse.fr "cd /var/www/glassfish-apps/ 
mv pep.war pep-old.war"

echo "send pep.war on the server"
scp ${websitepath} "root@ci.emse.fr:/var/www/glassfish-apps/pep.war"

echo ""
echo "deploy pep.war on the server"
ssh ci.emse.fr "/opt/glassfish4/glassfish/bin/asadmin login && /opt/glassfish4/glassfish/bin/asadmin deploy --force /var/www/glassfish-apps/pep.war"

echo "ok"
echo
