#!/bin/bash

# first argument is experiment id

[ $# -lt 1 ] && echo "Need database_bame and experiment_id as first and second arguments!" && exit 1


DATABASE=$1

echo "Experiment details"
echo "=================="
echo "SELECT id, sampling_rate, input_file, configurations FROM experiments ORDER BY id DESC LIMIT 1;" | sqlite3 $DATABASE
EXPERIMENT_ID=$(echo "SELECT id, sampling_rate, input_file, configurations FROM experiments ORDER BY id DESC LIMIT 1;" | sqlite3 $DATABASE | tail -n 1  |  awk '{print $1}')
echo ""
echo "Metrics"
echo "======="
echo "SELECT COUNT(*) as Executions, sampler as Sampler, AVG(mean_square_error) as \"Average MSE\", AVG(mean_average_error) as \"Average MAE\", AVG(deviation) as \"Average Deviation\" FROM metrics WHERE experiment_id=$EXPERIMENT_ID GROUP BY sampler;" | sqlite3 $DATABASE
