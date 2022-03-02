cd ~
cd webservice
sudo mvn install -DskipTests=true
sudo chmod 777 ./target/assignment-1.0-SNAPSHOT.jar
cd ..
echo '#!/bin/bash 
java -jar home/ec2-user/webservice/target/assignment-1.0-SNAPSHOT.jar' > runjar.sh

sudo chmod 777 runjar.sh

sudo chmod 777 ../../etc/systemd/system
cd ../../etc/systemd/system


echo "[Unit]
Description= Web Service for CSYE6225
After=syslog.target

[Service]
User=ec2-user
ExecStart=/home/ec2-user/runjar.sh
SuccessExitStatus=143

[Install]
WantedBy=multi-user.target" > mywebservice.service

sudo systemctl enable mywebservice.service