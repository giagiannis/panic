#!/usr/bin/gnuplot
# this creates a 2-d plot comparing the same column between two different files


if (!exists("file1") || !exists("file2")) \
        print "add -e 'file1=<>;file2=<>' to execution"; \
        exit(0)

if (!exists("col_to_print")) \
	print "define col_to_print";\
	exit (0)

tmpseed=system("openssl rand -hex 5")
tmpfile='/tmp/'.tmpseed.".eps"
print("Temp file:\t".tmpfile)

if (!exists("outfile")) \
        print "You can set the outfile by adding: \"-e 'outfile=<outfile>'\"";\
        outfile=tmpseed.".pdf"

print("Output: ".outfile)



set terminal postscript eps enhanced color font 'Arial,24' size 5,3
set output tmpfile

colname1=system("basename ".file1)
colname2=system("basename ".file2)

#set title "MSE/# of leaves vs bucket size"
firstrow = system('head -1 '.file1)
set xlabel word(firstrow, 1)
set ylabel word(firstrow, col_to_print)
set title "File comparison"
set grid
plot file1 u 1:col_to_print w l t colname1, \
	file2 u 1:col_to_print w l t colname2


system("epstopdf ".tmpfile." --outfile=".outfile." && rm ".tmpfile)
