#!/bin/bash

# This scripts downloads and configures Hadoop.

JAVA_HOME="/usr/lib/jvm/java-7-openjdk-amd64/"
HADOOP_URL="http://apache.cc.uoc.gr/hadoop/common/stable1/hadoop-1.2.1-bin.tar.gz"
HADOOP_INSTALLATION_PATH="/opt/hadoop"

download_hadoop(){
wget $HADOOP_URL -O /tmp/hadoop.tar.gz
tar xfz /tmp/hadoop.tar.gz -C /tmp/
mv /tmp/hadoop-1.2.1/ $HADOOP_INSTALLATION_PATH
echo "export PATH=\$PATH:/opt/hadoop/bin/" >> /etc/profile
rm /tmp/hadoop.tar.gz
}

conf_xml(){
sed -i -e "/<configuration>/a \ \t<property>\n\t\t<name>$1</name>\n\t\t<value>$2</value>\n\t</property>" $3
}

configure_hadoop(){
# set JAVA_HOME
sed  -i 's|# export JAVA_HOME.*|export JAVA_HOME='"$JAVA_HOME"'|' $HADOOP_INSTALLATION_PATH/conf/hadoop-env.sh

# set masters, slaves
echo "master1" > $HADOOP_INSTALLATION_PATH/conf/masters
echo -n > $HADOOP_INSTALLATION_PATH/conf/slaves
SLAVES=$(cat /etc/hosts | grep slave | wc -l)
for i in $(seq 1 $SLAVES); do 
  echo slave$i >> $HADOOP_INSTALLATION_PATH/conf/slaves
done
# configure hdfs
conf_xml dfs.replication 1 $HADOOP_INSTALLATION_PATH/conf/hdfs-site.xml
conf_xml dfs.name.dir /opt/hdfsname/ $HADOOP_INSTALLATION_PATH/conf/hdfs-site.xml
conf_xml dfs.data.dir /opt/hdfsdata/ $HADOOP_INSTALLATION_PATH/conf/hdfs-site.xml
conf_xml fs.default.name hdfs://master1:9000 $HADOOP_INSTALLATION_PATH/conf/hdfs-site.xml

# configure mapred
conf_xml mapred.job.tracker master1:9001 $HADOOP_INSTALLATION_PATH/conf/mapred-site.xml
}


download_hadoop
configure_hadoop