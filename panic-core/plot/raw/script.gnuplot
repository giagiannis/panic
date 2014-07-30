set title "Raw performance"
set ylabel "Execution time (sec)"
set xlabel "Number of nodes"

set terminal png
set output "raw.png"


set grid

plot "raw_1_100.dat" using 1:4 with lines title columnhead, "raw_2_100.dat" using 1:4 with lines title columnhead, "raw_4_100.dat" using 1:4 with lines title columnhead
