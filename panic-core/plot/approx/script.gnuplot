set title "Model approximation (SR=0.05,5G terasort, 2cores)"
set ylabel "Execution time (sec)"
set xlabel "Number of nodes"

set terminal png
set output "approx.png"


set grid

plot 	"output.dat" using 3:4 with lines title columnhead, \
	"output.dat" using 3:5 with lines title columnhead, \
	"output.dat" using 3:6 with lines title columnhead, \
	"output.dat" using 3:7 with lines title columnhead, \
	"output.dat" using 3:8 with lines title columnhead, \
	"output.dat" using 3:9 with lines title columnhead, \
	"output.dat" using 3:11 with lines title columnhead, \
	"output.dat" using 3:12 with lines title columnhead, \
	"output.dat" using 3:13 with lines title columnhead
