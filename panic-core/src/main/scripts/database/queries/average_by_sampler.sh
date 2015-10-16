#!/bin/bash

# first argument is experiment id

[ $# -lt 2 ] && echo "Need database_bame and experiment_id as first and second arguments!" && exit 1


DATABASE=$1
EXPERIMENT_ID=$2

echo "Experiment details"
echo "=================="
echo "SELECT id, sampling_rate, input_file, configurations FROM experiments WHERE id=$EXPERIMENT_ID;" | sqlite3 $DATABASE
echo ""
echo "Metrics"
echo "======="
echo "SELECT sampler, AVG(mean_square_error), AVG(mean_average_error), AVG(deviation) FROM metrics WHERE experiment_id=$EXPERIMENT_ID GROUP BY sampler;" | sqlite3 $DATABASE
