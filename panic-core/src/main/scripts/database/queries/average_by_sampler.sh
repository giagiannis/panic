#!/bin/bash

# first argument is experiment id

[ $# -lt 1 ] && echo "Need experiment_id as first argument!" && exit 1

EXPERIMENT_ID=$1
[ -z "$DATABASE"] && DATABASE="foo.db"

echo "Experiment details"
echo "=================="
echo "SELECT id, sampling_rate, input_file, configurations FROM experiments WHERE id=$EXPERIMENT_ID;" | sqlite3 $DATABASE
echo ""
echo "Metrics"
echo "======="
echo "SELECT sampler, AVG(mean_square_error), AVG(mean_average_error), AVG(deviation) FROM metrics WHERE experiment_id=$EXPERIMENT_ID GROUP BY sampler;" | sqlite3 $DATABASE
