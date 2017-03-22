#!/usr/bin/gnuplot
set xlabel "x1"
set ylabel "x2"
set zlabel "y"
splot "test.dat" u 1:2:3 t col

pause(-1)
