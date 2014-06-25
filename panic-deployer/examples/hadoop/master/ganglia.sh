#!/bin/bash

install_packages(){
export DEBIAN_FRONTEND=noninteractive
apt-get -y install ganglia-monitor ganglia-webfrontend 1>/dev/null 2>/dev/null
}

setup_web(){
ln -s /etc/ganglia-webfrontend/apache.conf /etc/apache2/sites-available/ganglia
a2ensite ganglia

service apache2 restart
}

configure_gmetad(){
# data_source
sed -i -e 's/^data_source.*/data_source "Hadoop cluster" 10 master1:8649/g' /etc/ganglia/gmetad.conf
# gridname
sed -i -e 's/^# gridname "MyGrid"/gridname "Hadoop Cluster"/g' /etc/ganglia/gmetad.conf

service gmetad restart
}

configure_gmond(){
# udp_send_channel remove mcat_join and add host
MASTER_IP=$(cat /etc/hosts | grep master | awk '{print $1}')
sed -i -e "/^udp_send_channel/ a \  host=$MASTER_IP" /etc/ganglia/gmond.conf
sed -i -e "s/  mcast_join/# mcast_join/" /etc/ganglia/gmond.conf
sed -i -e "s/  bind/# bind/" /etc/ganglia/gmond.conf
sed -i -e "s/  bind/# bind/" /etc/ganglia/gmond.conf

sed -i -e '/^cluster {/{n;d}' /etc/ganglia/gmond.conf
sed -i -e "/^cluster {/ a \  name=\"Hadoop cluster\"" /etc/ganglia/gmond.conf

service ganglia-monitor restart
}

install_packages
configure_gmond
configure_gmetad
setup_web

echo "Ganglia installed"