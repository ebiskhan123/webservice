
ls
sudo systemctl stop mywebservice.service
cd /home/ec2-user
sudo systemctl disable mywebservice.service
sudo systemctl daemon-reload
sudo rm -rf webservice
pwd
sudo mkdir webservice
ls
