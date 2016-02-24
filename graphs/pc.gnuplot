#!/usr/bin/gnuplot
# pc.gnuplot creates a loadings plot based on the values of Principal Components


if (!exists("filename")) \
	system("echo \"add -e 'filename=<filename>' to execution\""); \
	exit(0)

if (!exists("outfile")) \
	print "You can set the outfile by adding: \"-e 'outfile=<outfile>'\"";\
	outfile=filename.".pdf"

print("Output: ".outfile)

tmpfile='/tmp/'.system("openssl rand -hex 5").".eps"
print("Temp file:\t".tmpfile)

set terminal postscript eps enhanced color font 'Arial,24' size 5,5
set output tmpfile

set xlabel "PC1"
set ylabel "PC2"


set grid
set title "Loadings plot"
set xrange [-1:1]
set yrange [-1:1]
unset key

set style line 1 lc rgb "black" pt 5 lw 3 ps 2

set object 1 circle at 0,0 size 1 fc rgb "navy"
plot filename using 2:3 with points ls 1,\
	filename using 2:3:1 with labels offset character 1, character 1, \
	filename using -1:-1:2:3 with vectors ls 1

system("epstopdf ".tmpfile." --outfile=".outfile." && rm ".tmpfile)
