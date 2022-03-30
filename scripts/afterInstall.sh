sudo systemctl stop mywebservice.service
#sudo systemctl disable mywebservice.service
sudo systemctl daemon-reload
cd /home/ec2-user
sudo chown ec2-user *
cd webservice
sudo mvn install -DskipTests=true
sudo chmod 777 ./target/assignment-1.0-SNAPSHOT.jar
cd ..
pwd

echo '#!/bin/bash

sleep 10
source /etc/profile.d/custom.sh

sudo java -DMYSQL_PASSWORD=${MYSQL_PASSWORD} -DS3_BUCKETNAME=${S3_BUCKETNAME} -DMYSQL_HOST=${MYSQL_HOST} -DMYSQL_USER=${MYSQL_USER} -DMYSQL_DATABASE=${MYSQL_DATABASE} -jar home/ec2-user/webservice/target/assignment-1.0-SNAPSHOT.jar' > runjar.sh

sudo chmod 777 runjar.sh

sudo systemctl daemon-reload

#sudo systemctl enable mywebservice.service
#sudo systemctl start mywebservice.service
#sudo systemctl reload mywebservice.service

