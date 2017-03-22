#!/usr/bin/python
from random import random
import os
POINTS=50


# data points
for i in range(0,POINTS): 
    x1=random()
    x2=random()
    print "%.5f\t%.5f\t%.5f"%(x1,x2,2*x1)


for i in range(0,POINTS): 
    x1=1+random()
    x2=random()
    print "%.5f\t%.5f\t%.5f"%(x1,x2,2-2*(x1-1))



# line points
#h1,h2,h3=random(),random(),random()
#h1=2.0
#h2=0.0
#h3=-2.0
#
#
#if h2 != 0.0:
#    for i in range(0,POINTS):
#        x1 = random()
#        x2 = -(h1/h2)*x1 - (h3/h2)
#        for i in range(0,10):
#            print "%.5f\t%.5f\t%.5f"%(x1,x2,2*random())
#    os.write(2, "%.5f x_1 + %.5f x_2 + %.5f = 0\n" % (h1,h2,h3))
#if h1 != 0.0:
#    for i in range(0,POINTS):
#        x2 = random()
#        x1 = -(h2/h1)*x1 - (h3/h1)
#        for i in range(0,10):
#            print "%.5f\t%.5f\t%.5f"%(x1,x2,2*random())
#    os.write(2, "%.5f x_1 + %.5f x_2 + %.5f = 0\n" % (h1,h2,h3))
