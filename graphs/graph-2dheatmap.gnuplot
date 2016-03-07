#!/usr/bin/gnuplot
# this creates a 2-d plot comparing the same column between two different files


if (!exists("infile") || !exists("column")) \
        print "add -e 'infile=<>;colomn=<>' to execution"; \
        exit(0)


tmpseed=system("openssl rand -hex 5")
tmpfile='/tmp/'.tmpseed.".eps"
print("Temp file:\t".tmpfile)

if (!exists("outfile")) \
        print "You can set the outfile by adding: \"-e 'outfile=<outfile>'\"";\
        outfile=tmpseed.".pdf"
print("Output: ".outfile)



set terminal postscript eps enhanced color font 'Arial,24' size 5,3
set output tmpfile

#set palette grey
firstrow = system('head -1 '.infile)

set title word(firstrow,1)." vs ".word(firstrow,2)." vs ".word(firstrow,column)
set xlabel word(firstrow,1)
set ylabel word(firstrow,2)
set zlabel word(firstrow,column)
set grid
unset key
set autoscale x
plot infile u 1:2:column w image t col(column)

system("rm -f ".outfile)
system("epstopdf ".tmpfile." --outfile=".outfile." && rm ".tmpfile)
