#!/bin/bash

[ -z "$COLUMN_TO_PLOT" ] && echo -ne "Column to plot:\t" && read COLUMN_TO_PLOT
[ -z "$FILE_TO_PLOT" ] && echo -ne "File to plot:\t\t" && read FILE_TO_PLOT
[ -z "$INDICES_TO_PLOT" ] && echo -ne "Indices to plot:\t" && read INDICES_TO_PLOT
echo -e "Index to plot is $INDICES_TO_PLOT"

ARGUMENT=""
for i in $INDICES_TO_PLOT; do
#	ARGUMENT=$ARGUMENT"'$FILE_TO_PLOT' index $i using 1:2:$COLUMN_TO_PLOT with points title columnhead,"
        for j in $COLUMN_TO_PLOT; do 
            ARGUMENT=$ARGUMENT"'$FILE_TO_PLOT' index $i using 1:2:$j with points title columnhead,"    
        done
done
ARGUMENT=${ARGUMENT%?}


echo "Plotting, press enter to terminate.."
gnuplot -p -e "set grid; splot $ARGUMENT; pause -1;"
