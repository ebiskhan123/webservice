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



sudo systemctl disable mywebservice.service
sudo systemd-resolve --flush-caches
sudo systemctl daemon-reload
sudo systemctl kill mywebservice.service
sudo systemctl --now enable mywebservice.service

sudo systemctl status mywebservice.service
sudo journalctl -u mywebservice.service -f

#sudo systemctl reboot


#sudo systemctl stop mywebservice.service
#sudo systemctl disable mywebservice.service
#sudo systemctl daemon-reload
#cd /home/ec2-user
#cd webservice
#sudo mvn install -DskipTests=true
#sudo chmod 777 ./target/assignment-1.0-SNAPSHOT.jar
#cd ..
#pwd
#
#echo '#!/bin/bash
#
#sleep 10
#source /etc/profile.d/custom.sh
#
#sudo java -DMYSQL_PASSWORD=${MYSQL_PASSWORD} -DS3_BUCKETNAME=${S3_BUCKETNAME} -DMYSQL_HOST=${MYSQL_HOST} -DMYSQL_USER=${MYSQL_USER} -DMYSQL_DATABASE=${MYSQL_DATABASE} -jar home/ec2-user/webservice/target/assignment-1.0-SNAPSHOT.jar' > runjar.sh
#
#sudo chmod 777 runjar.sh
#sudo chmod 777 ../../etc/systemd/system
#cd ../../etc/systemd/system
#
#
#echo "[Unit]
#Description= Web Service for CSYE6225
#After=syslog.target
#
#[Service]
#User=ec2-user
#ExecStart=/home/ec2-user/runjar.sh
#SuccessExitStatus=143
#Restart=always
#RestartSec=10
#StandardOutput=syslog
#StandardError=syslog
#
#[Install]
#WantedBy=multi-user.target
#WantedBy=cloud-init.target" > mywebservice.service
#
#sudo systemctl daemon-reload
#
#sudo systemctl enable mywebservice.service
#
#sudo systemctl reload mywebservice.service
