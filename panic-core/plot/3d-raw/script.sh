#!/bin/bash

[ "$1" == "" ] && echo "I need METRICS_DIR as first argument" && exit 1 
INPUT_FILE=$1



DIMENSION_1=$(cat $INPUT_FILE | awk '{print $1}' | sort -n |uniq | grep --invert-match "[a-z]")
DIMENSION_2=$(cat $INPUT_FILE | awk '{print $2}' | sort -n |uniq | grep --invert-match "[a-z]")
DIMENSION_3=$(cat $INPUT_FILE | awk '{print $3}' | sort -n |uniq | grep --invert-match "[a-z]")



initialize(){
for j in $DIMENSION_2; do
echo "Nodes\tSize\tTime"> /tmp/data-3d-${j}.csv
done
}

plot_function(){

DATA_FILE=$1
CORES=$(basename $DATA_FILE | tr '-' '\t' | tr '.' '\t'| awk '{print $3}')
gnuplot -p << EOF

set grid

set title 'Raw performance (cores=$CORES)'
set xlabel 'Nodes'
set ylabel 'Dataset size'
set zlabel 'Time (sec)'

set terminal png
set output 'output-${CORES}.png'
splot '$DATA_FILE' using 1:2:3 with lines title '$(basename $INPUT_FILE)'

EOF
}

cleanup(){
for j in $DIMENSION_2; do
rm /tmp/data-3d-${j}.csv
done
}



initialize


for i in $DIMENSION_1;do
for j in $DIMENSION_2; do
for k in $DIMENSION_3;do
	echo -e "${i}\t${k}\t$(cat $INPUT_FILE | grep "${i}\s${j}\s${k}" | awk '{print $4}')" >> /tmp/data-3d-${j}.csv
done
done
for j in $DIMENSION_2; do
	echo >> /tmp/data-3d-${j}.csv
done
done


for j in $DIMENSION_2; do
plot_function /tmp/data-3d-${j}.csv
done





OUTPUT_FOLDER=$2

if [ "$OUTPUT_FOLDER" != "" ]; then
	mkdir $OUTPUT_FOLDER
	mv *.png $OUTPUT_FOLDER;
fi
