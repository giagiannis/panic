#!/bin/bash

export DEBIAN_FRONTEND=noninteractive

apt-get -y update
apt-get -y install mysql-server

sed -i -e "s/bind-address/# bind-address/g" /etc/mysql/my.cnf
service mysql restart

mysql -u root << EOF
CREATE USER 'panic'@'localhost' IDENTIFIED BY 'panic';
GRANT ALL PRIVILEGES ON *.* TO 'panic'@'localhost' WITH GRANT OPTION;
CREATE USER 'panic'@'%' IDENTIFIED BY 'panic';
GRANT ALL PRIVILEGES ON *.* TO 'panic'@'%' WITH GRANT OPTION;
EOF
