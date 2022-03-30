sudo yum install ruby -y
sudo yum install wget -y

cache="/opt/codedeploy-agent/bin/codedeploy-agent"
$cache stop
sudo yum erase codedeploy-agent -y

cd /home/ec2-user
wget https://aws-codedeploy-$CURRENTREGION.s3.$CURRENTREGION.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
sudo service codedeploy-agent status
