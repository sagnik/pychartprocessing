import matplotlib.pyplot as plt
from numpy.random import logistic
from numpy import arange
import numpy as np
from numpy import exp

def plotSingle(xs,ys,axis,putGrid=False):
    plt.plot(\
        xs,\
        ys,\
        marker='x',\
        color='blue',\
        markeredgecolor='blue',\
        label='test',\
        linestyle="dashed",\
        markerfacecolor='none'\
    )

    plt.legend(loc='upper left')
    plt.axis(axis)
    plt.grid(putGrid)
    plt.xlabel("numbers")
    plt.ylabel("function")
    plt.title("Automated plotting from matplotlib")
    svgFile="test.svg"
    plt.savefig(svgFile)

    print "plot saved at",svgFile                      

def main():
    base = arange(1.0, 10.0, 0.1)
    c=1
    b=2
    a=1
    fValues=10*(c/(1+a*exp(-b*base)))
    axis=[np.min(base),np.max(base),np.min(fValues),np.max(fValues)]
   
    print "\n--------------------------\n"
    print "plotting single functions"
    print "\n--------------------------\n"

    plotSingle(\
        xs=base,\
        ys=fValues,\
        axis=axis,\
        putGrid=False\
        )
    plt.clf()
    
if __name__ == "__main__":
    main()


