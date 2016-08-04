from functions import functionNames
from markers import markerNames
from colors import colorNames

import matplotlib.pyplot as plt
import sys
from numpy import arange
    
def main():
    fIndex=0
    base = arange(0.0, 10.0, 1.0)
    plt.plot(\
     base,\
     functionNames[functionNames.keys()[fIndex]](base),\
     marker=markerNames[markerNames.keys()[fIndex]],\
     color=colorNames[colorNames.keys()[fIndex]]\
    )
    #plt.plot(t,sin(-2*pi*t),marker=marker2['id'],color=marker2['color'])
    plt.xlabel('numbers')
    plt.grid(False)
    plt.ylabel(functionNames.keys()[fIndex])
    plt.title('Automated plotting from Python')

    print "plotting function",functionNames.keys()[fIndex],"with marker",markerNames.keys()[fIndex],"and color",colorNames.keys()[fIndex]
    sv=""
    if sys.argv[1].endswith(".svg"):
        sv=("../data/"+sys.argv[1])
    else:
        sv=("../data/"+sys.argv[1]+".svg")
    plt.savefig(sv)

if __name__ == "__main__":
    main()


