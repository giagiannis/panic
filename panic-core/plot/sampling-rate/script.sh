#!/bin/bash

# how you create data to get some graphs
METRICS_PATH="../../results"




echo -e "SR\tAdaptive\tUniform\tRandom" > sampling-rate-average.dat; 
for i in 0.05 0.10 0.15 0.20 0.25 0.30 0.35 0.40 0.45 0.50; do 
	GREEDY=$(cat $METRICS_PATH/metrics-$i.csv  | grep MLPer | tail -n 1 | awk '{print $3}' ); 
	UNI=$(cat $METRICS_PATH/metrics-$i.csv  | grep MLPer | head -n 1 | awk '{print $3}'); 
	RA=$(cat $METRICS_PATH/metrics-$i.csv  | grep MLPer | head -n 2 | tail -n 1 | awk '{print $3}' );
	echo -e "$i\t$GREEDY\t$UNI\t$RA"; 
done >> sampling-rate-average.dat



echo -e "SR\tAdaptive\tUniform\tRandom" > sampling-rate-mean.dat; 
for i in 0.05 0.10 0.15 0.20 0.25 0.30 0.35 0.40 0.45 0.50; do 
	GREEDY=$(cat $METRICS_PATH/metrics-$i.csv  | grep MLPer | tail -n 1 | awk '{print $2}' ); 
	UNI=$(cat $METRICS_PATH/metrics-$i.csv  | grep MLPer | head -n 1 | awk '{print $2}'); 
	RA=$(cat $METRICS_PATH/metrics-$i.csv  | grep MLPer | head -n 2 | tail -n 1 | awk '{print $2}' );
	echo -e "$i\t$GREEDY\t$UNI\t$RA"; 
done >> sampling-rate-mean.dat
