#!/bin/bash

# script used to create a subst of data files

DATA_FILE="../../results/terasort/results-0.05.csv"


LINES_OF_OUTPUT=304


cat $DATA_FILE | grep "objective" | head -n 1 >> output.dat
cat $DATA_FILE | head -n $LINES_OF_OUTPUT | tail -n $[$LINES_OF_OUTPUT/2-1]  | grep  "^2.0\s50.0\s" >> output.dat



