#!/bin/bash


HADOOP_INSTALLATION_PATH="/opt/hadoop"
HAMA_INSTALLATION_PATH="/opt/hama/"

HADOOP_BIN="$HADOOP_INSTALLATION_PATH/bin/hadoop"
HAMA_BIN="$HAMA_INSTALLATION_PATH/bin/hama"

HADOOP_JAR="$HADOOP_INSTALLATION_PATH/hadoop-examples-1.2.1.jar"
HAMA_JAR="$HAMA_INSTALLATION_PATH/hama-examples-0.6.4.jar"

ADULT_DATASET_URL="http://snf-562459.vm.okeanos.grnet.gr/adult.dat"
ADULT_DATASET_URL_SORT="http://snf-562459.vm.okeanos.grnet.gr/adult_sort.dat"

TIMEFILE="/tmp/times.csv"
LOGS="/tmp/jobs.log"
echo -n > $TIMEFILE
echo -n > $LOGS


TIME_CMD="/usr/bin/time -f %e\t%U\t%S -o $TIMEFILE --append"

# # Terasort benchmark
# for MILLIONS in 10 20 30 40 50; do 
# 	echo -ne "Teragen-${MILLIONS}\t" >>$TIMEFILE
# 	$TIME_CMD $HADOOP_BIN jar $HADOOP_JAR teragen $[MILLIONS*10**6] /data/tera-${MILLIONS}M 1>>$LOGS 2>>$LOGS
# 	tail -n 1 $TIMEFILE
# 	echo -ne "Terasort-${MILLIONS}\t" >>$TIMEFILE
# 	$TIME_CMD $HADOOP_BIN jar $HADOOP_JAR terasort /data/tera-${MILLIONS}M /output/tera-${MILLIONS}M 1>>$LOGS 2>>$LOGS
# 	tail -n 1 $TIMEFILE
# 	$HADOOP_BIN fs -rmr /data/tera-${MILLIONS}M /output/tera-${MILLIONS}M 1>>$LOGS 2>>$LOGS
# done
# 
# 
# # Pagerank benchmark
# EDGES=50
# for THOUSANDS in 50 60 70 80 90 100; do
# 	echo -ne "Fastgen-${THOUSANDS}-${EDGES}\t" >> $TIMEFILE
# 	$TIME_CMD $HAMA_BIN jar $HAMA_JAR gen fastgen $[THOUSANDS*1000] $EDGES /data/pagerank-${THOUSANDS}-${EDGES} 1 1>>$LOGS 2>>$LOGS
# 	tail -n 1 $TIMEFILE
# 	echo -ne "PageRank-${THOUSANDS}-${EDGES}\t" >> $TIMEFILE
# 	$TIME_CMD $HAMA_BIN jar $HAMA_JAR pagerank /data/pagerank-${THOUSANDS}-${EDGES} /output/pagerank-${THOUSANDS}-${EDGES} 1000 1>>$LOGS 2>>$LOGS
# 	tail -n 1 $TIMEFILE
# 	$HADOOP_BIN fs -rmr /data/pagerank-${THOUSANDS}-${EDGES} /output/pagerank-${THOUSANDS}-${EDGES} 1>>$LOGS 2>>$LOGS
# done


# K means benchmark

# curl $ADULT_DATASET_URL | $HADOOP_BIN fs -put - /adult.dat
curl $ADULT_DATASET_URL_SORT | $HADOOP_BIN fs -put - /adult_sort.dat

CLUSTERS=100
for CLUSTERS in 40 50 60 70 80; do
	echo -ne "Kmeans-${CLUSTERS}\t" >> $TIMEFILE
	$TIME_CMD $HAMA_BIN jar $HAMA_JAR kmeans /adult_sort.dat /output  100 $CLUSTERS 1>>$LOGS 2>>$LOGS
	tail -n 1 $TIMEFILE
done
exit 0
