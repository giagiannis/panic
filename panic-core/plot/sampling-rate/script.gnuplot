set title "Average error vs Sampling Rate for MLP"
set ylabel "Average error (sec)"
set xlabel "Sampling Rate"



set grid

set terminal png
set output "average.png"
plot "sampling-rate-average.dat" using 1:2 with lines title columnhead, "sampling-rate-average.dat" using 1:3 with lines title columnhead, "sampling-rate-average.dat" using 1:4 with lines title columnhead



set title "Mean Square error vs Sampling Rate for MLP"
set ylabel "Mean Square error (sec^2)"
set terminal png
set output "mean.png"
plot "sampling-rate-mean.dat" using 1:2 with lines title columnhead, "sampling-rate-mean.dat" using 1:3 with lines title columnhead, "sampling-rate-mean.dat" using 1:4 with lines title columnhead
