#!/bin/bash

MAX_CONCURRENT_PROCESSES=7;
MODELS="RandomCommittee"
SAMPLERS="BiasedPCASampler,LatinHypercubeSampler,GreedyAdaptiveSampler"
INPUT_FILES="/home/giannis/Projects/panic/panic-data-generator/datasets-5dimensions/*1.txt"
SAMPLING_RATES="0.001 0.003 0.005 0.008 0.011 0.014 0.017 0.02"
CONFIGURATIONS="BiasedPCASampler:instances=10,samplesPerPhase=30|LatinHypercubeSampler:instances=10 BiasedPCASampler:instances=10,samplesPerPhase=35|LatinHypercubeSampler:instances=10 BiasedPCASampler:instances=10,samplesPerPhase=40|LatinHypercubeSampler:instances=10" 
LOG_DIR="/tmp/panic-experiments/"
DB_NAME="panic_$(date +"%Y%m%d%H%M%S")"
DB_USER="panic"
DB_PASSWORD="panic-password"
DB_HOST="192.168.10.27"
OTHER_PARAMS="--skip-predictions --db-user $DB_USER --db-pass $DB_PASSWORD --db-host $DB_HOST"

function wait_for_processes {
sleep 0.5
PROCESSES=$(ps -ef | grep java | grep panic | wc -l)
if [ "$MAX_CONCURRENT_PROCESSES" -le "$PROCESSES" ]; then
echo -e "Execution slots are full, waiting for a process to finish" && wait -n 1
fi
}


function init_db {
echo "CREATE SCHEMA $DB_NAME;" | mysql -u$DB_USER -p$DB_PASSWORD -h$DB_HOST 

}

# mkdir 
mkdir -p $LOG_DIR

# database init
init_db

# script execution
for INPUT in $INPUT_FILES; do  
echo "Working for $INPUT"
for SR in $SAMPLING_RATES; do 
for CONFIGURATION in $CONFIGURATIONS; do 
echo -e "\t$(basename ${INPUT}) ${SR} ${CONFIGURATION}: starting" && bash src/main/scripts/panic-core.sh -m $MODELS -i $INPUT -sr $SR -db $DB_NAME -st $SAMPLERS -c $CONFIGURATION $OTHER_PARAMS 2>&1 1>>$LOG_DIR/${DB_NAME}.log  && echo -e "\t$(basename ${INPUT}) ${SR} ${CONFIGURATION}: done" & 
wait_for_processes
done
done; 
echo "Done with $INPUT"
done
