#!/bin/bash

export TOMCAT_INSTALL_DIR="/var/lib/tomcat7"
export GIT_URL="https://github.com/giagiannis/cloud-benchmark-apps"

install_packages(){
export DEBIAN_FRONTEND=noninteractive
apt-get -y update 1>>/tmp/apt.log 2>>/tmp/apt.log
apt-get -y install maven git vim bash-completion openjdk-7-jdk tomcat7 tomcat7-admin tomcat7-common tomcat7-examples tomcat7-docs tomcat7-user 1>>/tmp/apt.log 2>>/tmp/apt.log
}

configure(){
MEMORY_MB=$(free -m | grep -i mem | awk '{print $2}')
sed -i "s|-Xmx128m|-Xmx${MEMORY_MB}m|g" /etc/default/tomcat7

service tomcat7 restart

git clone $GIT_URL /tmp/webapps/
mvn -f /tmp/webapps/webapp-java/pom.xml package
mv /tmp/webapps/webapp-java/target/webapp-java-1.0.war $TOMCAT_INSTALL_DIR/webapps/webapp-java.war
rm -rf /tmp/webapps/
}

install_packages
configure
