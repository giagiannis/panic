#!/usr/bin/gnuplot

if (!exists("infile") || !exists("column")) \
        print "add -e 'infile=<>;column=<>;' to execution";\
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


firstrow=system("head -n 1 ".infile)
set xlabel word(firstrow,1)
set ylabel word(firstrow,column)
set title word(firstrow,column)." vs ".word(firstrow,1)
set grid


set style line 1 lc rgb "blue" pt 5 lw 3 ps 2

plot infile u 1:column w lp t col ls 1

system("epstopdf ".tmpfile." --outfile=".outfile." && rm ".tmpfile)

