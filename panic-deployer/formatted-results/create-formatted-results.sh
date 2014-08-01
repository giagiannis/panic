#!/bin/bash

# script used to create a single csv file out ot a set of files
# created during the deployments


DEPLOYMENT_FILES_PATH="/home/giannis/Project/panic/panic-deployer/results/pagerank/"
#DEPLOYMENT_FILES_PATH="/home/giannis/Project/panic/panic-deployer/results/terasort/"

DIMENSION_1="2 3 4 5 6 7 8 9 10"
DIMENSION_2="1 2 4"
DIMENSION_3="50 60 70 80 90 100"
#DIMENSION_3="10 20 30 40 50"

DIMENSION_1_LABEL="nodes"
DIMENSION_2_LABEL="cores"
DIMENSION_3_LABEL="size"
PERFORMANCE_LABEL="time"

echo -e "${DIMENSION_1_LABEL}\t${DIMENSION_2_LABEL}\t${DIMENSION_3_LABEL}\t${PERFORMANCE_LABEL}"
for i in $DIMENSION_1; do
	for j in $DIMENSION_2; do
		for k in $DIMENSION_3; do
			VALUE=$(cat $DEPLOYMENT_FILES_PATH/case-${i}n-${j}c.json-state | grep "^[a-zA-Z]*-${k}" | awk '{print $2}')
			echo -e "${i}\t${j}\t${k}\t${VALUE}"
		done
	done
done
