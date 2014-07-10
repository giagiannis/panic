#!/bin/bash

export DEBIAN_FRONTEND=noninteractive
apt-get -y update
apt-get -y install apache2 mysql-client libapache2-mod-php5 php5-mysql git

echo -e "<?php\nphpinfo();\n?>" > /var/www/index.php

service apache2 restart