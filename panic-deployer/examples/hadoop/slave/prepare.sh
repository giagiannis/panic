#!/bin/bash

# This is the slave preparation script...


apt-get -y update

echo "Proof I was here @ $(date) -- slave" > /tmp/logs.txt

apt-get -y install openjdk-7-jre vim