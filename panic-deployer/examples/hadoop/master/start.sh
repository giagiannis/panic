#!/bin/bash

# Format namenode and start the hadoop cluster

hadoop namenode -format

start-dfs.sh

start-mapred.sh