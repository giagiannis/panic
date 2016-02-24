#!/usr/bin/gnuplot
# this creates a 2-d plot with two different axis
# the data must be structured as follows:
# 1st col: the x axis
# 2nd col: the y axis
# 3rd col: the y2 axis

if (!exists("filename")) \
	system("echo \"add -e 'filename=<filename>' to execution\""); \
	exit(0)

set terminal postscript eps enhanced color font 'Arial,24' size 10,6
set output filename.".eps"

set xlabel "x"
set ylabel "y"
set y2label "y2"
set y2tics auto


firstrow = system('head -1 '.filename)
set xlabel word(firstrow, 1)
set ylabel word(firstrow, 2)
set y2label word(firstrow, 3)

set autoscale y
set autoscale y2

plot filename u 1:2 w l axes x1y1 t col, \
	filename u 1:3 w l axes x2y2 t col


system("epstopdf ".filename.".eps")
system("rm ".filename.".eps")
