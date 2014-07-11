#!/bin/bash

install_packages(){
export DEBIAN_FRONTEND=noninteractive
apt-get -y install ganglia-monitor 1>/dev/null 2>/dev/null
}


configure_gmond(){
# udp_send_channel remove mcat_join and add host
LOCALHOST=$(cat /etc/hosts | grep apache1 | awk '{print $1}')
sed -i -e "/^udp_send_channel/ a \  host=$LOCALHOST" /etc/ganglia/gmond.conf
sed -i -e "s/  mcast_join/# mcast_join/" /etc/ganglia/gmond.conf
sed -i -e "s/  bind/# bind/" /etc/ganglia/gmond.conf
sed -i -e "s/  bind/# bind/" /etc/ganglia/gmond.conf

sed -i -e '/^cluster {/{n;d}' /etc/ganglia/gmond.conf
sed -i -e "/^cluster {/ a \  name=\"apache\"" /etc/ganglia/gmond.conf

service ganglia-monitor restart
}


install_packages
configure_gmond

echo "Ganglia installed"