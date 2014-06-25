#!/bin/bash

HADOOP_INSTALLATION_PATH="/opt/hadoop"

# Format namenode and start the hadoop cluster

$HADOOP_INSTALLATION_PATH/bin/hadoop namenode -format

$HADOOP_INSTALLATION_PATH/bin/start-dfs.sh

$HADOOP_INSTALLATION_PATH/bin/start-mapred.sh

echo "hadoop started"