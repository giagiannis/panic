#!/bin/bash

DATABASE=$1
DATASET=$2
CONFIG=$3
CONF_PARSE="";
if [ $(echo $CONFIG | grep tree.length| wc -l) -gt 0 ]; then
	CONF_PARSE="SUBSTRING(configurations, LOCATE('tree.coefficient', configurations)+17,3) as 'tree.coefficient'"
fi
echo "SELECT $CONF_PARSE, sampling_rate as sampling, experiment_id as exp, TRUNCATE(AVG(mean_square_error),5) AS MSE, TRUNCATE(AVG(mean_average_error),5) AS MAE, TRUNCATE(AVG(deviation),5) AS deviation FROM metrics, experiments WHERE experiment_id in (SELECT id FROM experiments WHERE input_file LIKE('%/$DATASET%') AND configurations LIKE('%$CONFIG%')) AND metrics.experiment_id=experiments.id GROUP BY experiment_id, sampler;" | mysql -uroot -pgiannis $DATABASE 
