#!/bin/sh

# DEBIAN_FRONTEND="noninteractive"

echo "Assignment 4"

sudo yum -y update

# curl -o- https://raw.githubusercontent.com/nvm-sh/nvm/v0.34.0/install.sh | bash

# . ~/.nvm/nvm.sh

# Install node version
# nvm install 14
sudo yum -y install java-1.8.0-openjdk

sudo yum -y install maven

echo java -version | awk '/version/{print $NF}'
sudo yum -y install mysql

# Get the repo RPM and install it.
#sudo wget http://dev.mysql.com/get/mysql57-community-release-el7-7.noarch.rpm
#sudo yum -y install ./mysql57-community-release-el7-7.noarch.rpm
#
## Install the server and start it
#sudo yum -y install mysql-community-server --nogpgcheck
#sudo systemctl start mysqld
#
## Get the temporary password
#temp_password=$(sudo grep 'temporary password' /var/log/mysqld.log | awk '{print $NF}')
#
#temppass=$(sudo grep 'temporary password' /var/log/mysqld.log | awk '{print $NF}')
#
#echo $temp_password

# sudo wget https://dev.mysql.com/get/mysql80-community-release-el7-3.noarch.rpm
# sudo rpm -Uvh mysql80-community-release-el7-3.noarch.rpm
# sudo yum install mysql-server
# sudo systemctl start mysqld

# sudo yum install mysql-server
# sudo systemctl status mysqld
# sudo grep 'temporary password' /var/log/mysqld.log 
# sudo yum install postgresql12 postgresql12-server

# sudo amazon-linux-extras | grep postgre Enter password: 

#echo "ALTER USER 'root'@'localhost' IDENTIFIED BY 'Mypassword@123'; flush privileges;" > reset_pass.sql
## echo "CREATE DATABASE cloudservicedb;" > create_db.sql
#
#mysql -u root --password="$temp_password" --connect-expired-password < reset_pass.sql
#mysql -u root --password="Mypassword@123" -e "CREATE DATABASE cloudservicedb;"
#
#export dbpwd=Mypassword@123
#echo 'Environment variable set for dbpwd as : '
#printenv dbpwd

#mysql -u root -p "Mypassword@123" -e "show databases;"

# mysql -u 'root' --password=$temp_password  -e 'alter user root@localhost identified by mypassword;'

# mysql -uroot -p
#mysql -u root -p "Mypassword@123" -e "show variables where Variable_name like '%host%';"
# echo 'l9U,35+sDBkz' | mysql -u root -p
# expect "Enter password:"
# echo 'l9U,35+sDBkz'


# mysql -u 'root' --password='l9U,35+sDBkz' -e 'SHOW DATABASES'


# mysql.server start
# mysql -u root -e "SET PASSWORD FOR root@'localhost' = PASSWORD('admin');"
