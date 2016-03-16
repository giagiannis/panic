#!/usr/bin/gnuplot


if (!exists("actual") && !exists("predicted")) \
	print "Add: -e 'actual=<>;predicted=<>;sample=<>;' to draw";\
        exit(0)

#set terminal canvas rounded size 1024,768 enhanced
#set output "canvas.html"

#print("Output: ".outfile)

#tmpfile='/tmp/'.system("openssl rand -hex 5").".eps"
#print("Temp file:\t".tmpfile)


#set terminal postscript eps enhanced color font 'Arial,24' size 5,3
#set output tmpfile


#set title "MSE/# of leaves vs bucket size"
firstrow = system('grep --invert-match "\#" '.actual.' | head -1 ')
set xlabel word(firstrow, 1)
set ylabel word(firstrow, 2)
set zlabel word(firstrow, 3)


set grid
if(exists("sample")) \
splot actual w p t col pt 8, \
	predicted  w p t "Predicted performance" pt 9, \
	sample w p t "Samples" pt 7; \
else \
splot actual w p t col pt 8, \
	predicted  w p t "Predicted performance" pt 9;

pause -1

#system("epstopdf ".tmpfile." --outfile=".outfile." && rm ".tmpfile)
