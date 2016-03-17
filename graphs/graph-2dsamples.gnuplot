#!/usr/bin/gnuplot
# this creates a 2-d plot comparing the same column between two different files


if (!exists("sample") || !exists("data") || !exists("cuts")) || !exists("errorlabels")\
        print "add -e 'data=<>;sample=<>;cuts=<>;errorlabels=<>' to execution"; \
        exit(0)


tmpseed=system("openssl rand -hex 5")
tmpfile='/tmp/'.tmpseed.".eps"
print("Temp file:\t".tmpfile)

if (!exists("outfile")) \
        print "You can set the outfile by adding: \"-e 'outfile=<outfile>'\"";\
        outfile=tmpseed.".pdf"

if(exists("comment")) \
	set title "Samples distribution (".comment.")"; \
else \
	set title "Samples distribution"
	
print("Output: ".outfile)



set terminal postscript eps enhanced color font 'Arial,24' size 5,3
set output tmpfile

#set palette grey
firstrow = system('head -1 '.data)

set xlabel "x1"
set ylabel "x2"
set grid
unset key
plot data w image t col, sample u 1:2 w p lc rgb "blue" ps 1 pt 5 t "Samples", \
cuts w p pt 5 lc rgb "black", errorlabels w labels font "Arial,10" textcolor rgb "white"
#cuts w vectors ls 5 nohead, errorlabels w labels font "Arial,10" textcolor rgb "white"

system("rm -f ".outfile)
system("epstopdf ".tmpfile." --outfile=".outfile." && rm ".tmpfile)
