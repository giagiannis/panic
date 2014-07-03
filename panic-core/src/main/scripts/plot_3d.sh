#!/bin/bash

[ -z "$COLUMNS_TO_PLOT" ] && echo -ne "Columns to plot:\t" && read COLUMNS_TO_PLOT
[ -z "$FILE_TO_PLOT" ] && echo -ne "File to plot:\t\t" && read FILE_TO_PLOT
[ -z "$INDEX_TO_PLOT" ] && export INDEX_TO_PLOT=0
echo -e "Index to plot is $INDEX_TO_PLOT"

ARGUMENT=""
for i in $COLUMNS_TO_PLOT; do
	ARGUMENT=$ARGUMENT"'$FILE_TO_PLOT' index 0 using 1:2:$i with points title columnhead,"	
done
ARGUMENT=${ARGUMENT%?}


echo "Plotting, press enter to terminate.."
gnuplot -perist -e "set grid; splot $ARGUMENT; pause -1;"