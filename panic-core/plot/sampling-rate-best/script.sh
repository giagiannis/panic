#!/bin/bash

# script used to take as input a set of metric files containing the average error, etc of the trained models
# and depict them in the same plots vs the sampling rate

[ "$1" == "" ] && echo "I need METRICS_DIR as first argument" && exit 1 
METRICS_DIR=$1

FILE_PREFIX="metrics"
RATES=""
for i in $(ls $METRICS_DIR/metrics* | tr '-' '\t' | awk '{print $2}'); do
	RATES=$RATES"${i%.csv} "
done


LINES_PER_SAMPLER=11
MODELS=$(head -n 12 $METRICS_DIR/$FILE_PREFIX-0.05.csv | tail -n 9 | awk '{print $1}')




cleanup(){
	rm /tmp/data-*.csv;
}

plot_model(){
INPUT_FILE=$1
TITLE=$(basename $INPUT_FILE| tr '.' '\t' | tr '-' '\t' | awk '{print $2}')
gnuplot -p << EOF

set grid;
set xlabel "Sampling Rate"
set title 'Best models for each sampler'
set terminal png



set output 'output-$TITLE.png'
set ylabel "$TITLE"
plot '$INPUT_FILE' using 1:2 with lines title 'Adaptive Sampling', \
	'$INPUT_FILE' using 1:3 with lines title 'Uniform Sampling'
EOF

}



echo -e "SR\tAdaptive\tUniform\tRandom" > /tmp/sr-mse.csv
echo -e "SR\tAdaptive\tUniform\tRandom" > /tmp/sr-average.csv
echo -e "SR\tAdaptive\tUniform\tRandom" > /tmp/sr-deviation.csv
echo -e "SR\tAdaptive\tUniform\tRandom" > /tmp/sr-r.csv
for SR in $RATES; do
GREEDY_LINE=$(grep -n "Greed" $METRICS_DIR/$FILE_PREFIX-$SR.csv  | tr ':' '\t' | awk '{print $1}')
UNIFORM_LINE=$(grep -n "Uniform" $METRICS_DIR/$FILE_PREFIX-$SR.csv  | tr ':' '\t' | awk '{print $1}')
RANDOM_LINE=$(grep -n "RandomSampler" $METRICS_DIR/$FILE_PREFIX-$SR.csv  | tr ':' '\t' | awk '{print $1}')

MIN_GRE=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[GREEDY_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | awk '{print $2}' |grep -v "[a-zA-Z]"| sort -n | head -n 1 ) 

MIN_UNI=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[UNIFORM_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | awk '{print $2}' |grep -v "[a-zA-Z]"| sort -n | head -n 1 )

MIN_RAN=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[RANDOM_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | grep -v "[a-z]"| awk '{print $2}' | sort -n | head -n 1)

echo -e "$SR\t$MIN_GRE\t$MIN_UNI\t$MIN_RAN" >> /tmp/sr-mse.csv

MIN_GRE=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[GREEDY_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | awk '{print $3}' |grep -v "[a-zA-Z]"| sort -n | head -n 1 ) 

MIN_UNI=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[UNIFORM_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | awk '{print $3}' |grep -v "[a-zA-Z]"| sort -n | head -n 1 )

MIN_RAN=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[RANDOM_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | grep -v "[a-z]"| awk '{print $3}' | sort -n | head -n 1)

echo -e "$SR\t$MIN_GRE\t$MIN_UNI\t$MIN_RAN" >> /tmp/sr-average.csv


MIN_GRE=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[GREEDY_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | awk '{print $4}' |grep -v "[a-zA-Z]"| sort -n | head -n 1 ) 

MIN_UNI=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[UNIFORM_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | awk '{print $4}' |grep -v "[a-zA-Z]"| sort -n | head -n 1 )

MIN_RAN=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[RANDOM_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | grep -v "[a-z]"| awk '{print $4}' | sort -n | head -n 1)

echo -e "$SR\t$MIN_GRE\t$MIN_UNI\t$MIN_RAN" >> /tmp/sr-deviation.csv

MIN_GRE=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[GREEDY_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | awk '{print $5}' |grep -v "[a-zA-Z]"| sort -n  | tail -n 2 | head -n 1 ) 

MIN_UNI=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[UNIFORM_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | awk '{print $5}' |grep -v "[a-zA-Z]"| sort -n | tail -n 2 | head -n 1 )

MIN_RAN=$(cat $METRICS_DIR/$FILE_PREFIX-$SR.csv | head -n $[RANDOM_LINE+LINES_PER_SAMPLER] | tail -n $LINES_PER_SAMPLER | grep -v "[a-z]"| awk '{print $5}' | sort -n -r | head -n 1)

echo -e "$SR\t$MIN_GRE\t$MIN_UNI\t$MIN_RAN" >> /tmp/sr-r.csv
done


plot_model /tmp/sr-mse.csv
plot_model /tmp/sr-average.csv
plot_model /tmp/sr-deviation.csv
plot_model /tmp/sr-r.csv


OUTPUT_DIR=$2

if [ "$OUTPUT_DIR" != "" ]; then
	mkdir $OUTPUT_DIR
	mv *.png $OUTPUT_DIR;
fi
