sudo systemctl stop mywebservice.service

cd /home/ec2-user
cd webservice
sudo mvn install -DskipTests=true
sudo chmod 777 ./target/assignment-1.0-SNAPSHOT.jar
cd ..
pwd


