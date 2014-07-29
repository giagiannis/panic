#!/bin/bash

# script used to automate the deployment procedure

run_scenario(){
DESCRIPTION=$(basename $1)

python -m deployer -d $DESCRIPTION -s state-files/${DESCRIPTION}-state -a launch && mail-alert "$(DESCRIPTION) deployed"

# waiting for launch

HOSTNAME=$(python -m deployer -l state-files/${DESCRIPTION}-state -a show | grep hostname | head -n 1 | tr "\'" "\t" | awk '{print $5}' )
HOST=$(ssh $HOSTNAME "hostname")

if [ "$HOST" == "master1" ]; then
	ssh $HOSTNAME < examples/hadoop/master/run_benchmarks.sh
	scp $HOSTNAME:/tmp/times.csv results/$DESCRIPTION 
	python -m deployer -l state-files/${DESCRIPTION}-state -a terminate && rm state-files/${DESCRIPTION}-state && mail-alert "$(DESCRIPTION) terminated"
fi
}

run_scenario examples/hadoop/deployments/case-7n-2c.json && run_scenario examples/hadoop/deployments/case-8n-1c.json && run_scenario examples/hadoop/deployments/case-9n-1c.json && run_scenario /hadoop/deployments/case-9n-4c.json > /tmpt/ggian_logs.txt&
run_scenario examples/hadoop/deployments/case-10n-1c.json && run_scenario examples/hadoop/deployments/case-10n-4c.json && run_scenario examples/hadoop/deployments/case-10n-2c.json > /tmpt/celar_logs.txt &

exit 0