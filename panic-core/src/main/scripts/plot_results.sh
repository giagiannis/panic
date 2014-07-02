#!/bin/bash

[ -z "$COLUMNS" ] && echo -ne "Columns to plot:\t" && read COLUMNS
[ -z "$FILE" ] && echo -ne "File to plot:\t\t" && read FILE

ARGUMENT=""
for i in $COLUMNS; do
	ARGUMENT=$ARGUMENT"'$FILE' using 1:2:$i with points title columnhead,"	
done
ARGUMENT=${ARGUMENT%?}


gnuplot -perist -e "set grid; splot $ARGUMENT; pause 0;"