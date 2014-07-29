#!/bin/bash


HADOOP_INSTALLATION_PATH="/opt/hadoop"
HAMA_INSTALLATION_PATH="/opt/hama/"

HADOOP_BIN="$HADOOP_INSTALLATION_PATH/bin/hadoop"
HAMA_BIN="$HAMA_INSTALLATION_PATH/bin/hama"

HADOOP_JAR="$HADOOP_INSTALLATION_PATH/hadoop-examples-1.2.1.jar"
HAMA_JAR="$HAMA_INSTALLATION_PATH/hama-examples-0.6.4.jar"

DATASET_URL="http://snf-562459.vm.okeanos.grnet.gr/"

TIMEFILE="/tmp/times.csv"
LOGS="/tmp/jobs.log"
echo -n > $TIMEFILE
echo -n > $LOGS


TIME_CMD="/usr/bin/time -f %e\t%U\t%S -o $TIMEFILE --append"



hadoop fs -mkdir /data/
# Terasort benchmark
for MILLIONS in 10 20 30 40 50; do 
	curl $DATASET_URL/tera-${MILLIONS}M.txt.gz | gzip -d | hadoop fs -put - /data/tera-${MILLIONS}M.txt
	echo -ne "Terasort-${MILLIONS}\t" >>$TIMEFILE
	$TIME_CMD $HADOOP_BIN jar $HADOOP_JAR terasort /data/ /output/tera-${MILLIONS}M 1>>$LOGS 2>>$LOGS
	tail -n 1 $TIMEFILE
	$HADOOP_BIN fs -rmr /data/tera-${MILLIONS}M.txt /output/tera-${MILLIONS}M 1>>$LOGS 2>>$LOGS
done



# Pagerank benchmark
EDGES=50
for THOUSANDS in 50 60 70 80 90 100; do
 	curl $DATASET_URL/page-${THOUSANDS}k.txt.gz | gzip -d | hadoop fs -put - /data/page-${THOUSANDS}k.txt
	echo -ne "PageRank-${THOUSANDS}-${EDGES}\t" >> $TIMEFILE
	$TIME_CMD $HAMA_BIN jar $HAMA_JAR pagerank /data/page-${THOUSANDS}k.txt /output/pagerank-${THOUSANDS}-${EDGES} 1000 1>>$LOGS 2>>$LOGS
	tail -n 1 $TIMEFILE
	$HADOOP_BIN fs -rmr /data/page-${THOUSANDS}k.txt /output/pagerank-${THOUSANDS}-${EDGES} 1>>$LOGS 2>>$LOGS
done


# K means benchmark

# curl $ADULT_DATASET_URL | $HADOOP_BIN fs -put - /adult.dat
# curl $ADULT_DATASET_URL_SORT | $HADOOP_BIN fs -put - /adult_sort.dat
# 
# CLUSTERS=100
# for CLUSTERS in 40 50 60 70 80; do
# 	echo -ne "Kmeans-${CLUSTERS}\t" >> $TIMEFILE
# 	$TIME_CMD $HAMA_BIN jar $HAMA_JAR kmeans /adult_sort.dat /output  100 $CLUSTERS 1>>$LOGS 2>>$LOGS
# 	tail -n 1 $TIMEFILE
# done


echo "Benchmark results are ready and can be found in $TIMEFILE"

cat $TIMEFILE

exit 0
