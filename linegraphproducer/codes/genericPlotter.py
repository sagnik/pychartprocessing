from functions import functionNames
from markers import markerNames
from colors import colorNames

import matplotlib.pyplot as plt
import sys
from numpy import arange
import numpy as np
import os

baseDataDir="../data/"    
def plotSingle(xs,functionName,markerName,colorName,combinedPlotName,axis,putGrid=False):
    plt.plot(\
        xs,\
        functionNames[functionName](xs),\
        marker=markerNames[markerName],\
        color=colorNames[colorName],\
        label=functionName,\
        )
    plt.legend(loc='upper left')
    plt.axis(axis)
    print "plotting function",functionName,"with marker",markerName,"and color",colorName
    plt.grid(putGrid)
    plt.xlabel('numbers')
    plt.ylabel(functionName)
    plt.title('Automated plotting from matplotlib: '+functionName)
    directory=baseDataDir+combinedPlotName
    if not os.path.exists(directory):
        os.makedirs(directory)
    svgFile=directory+"/"+combinedPlotName+"-"+functionName+"-"+markerName+"-"+colorName+".svg"    
    plt.savefig(svgFile)
    print "plot saved at",directory+"/"+svgFile


def plotMultiple(xs,thisFunctionNames,thisMarkerNames,thisColorNames,combinedPlotName,axis,putGrid=False):
    
    for functionName in thisFunctionNames:
        for markerName in thisMarkerNames:
            for colorName in thisColorNames:
                plt.plot(\
                    xs,\
                    functionNames[functionName](xs),\
                    marker=markerNames[markerName],\
                    color=colorNames[colorName],\
                    label=functionName+"-"+markerName+"-"+colorName 
                )
                print "plotting function",functionName,"with marker",markerName,"and color",colorName

    plt.legend(loc='upper left')
    plt.axis(axis)
    plt.grid(putGrid)
    plt.xlabel('numbers')
    plt.ylabel("functions")
    plt.title('Automated plotting from matplotlib: '+"different functions")

    directory=baseDataDir+combinedPlotName
    if not os.path.exists(directory):
        os.makedirs(directory)

    svgFile=combinedPlotName+".svg"
    metFile=combinedPlotName+".met"    
    plt.savefig(directory+"/"+svgFile)

    with open(directory+"/"+metFile,"w") as f:
        for colorName in thisColorNames:
            f.write(colorName+"\n")

    print "plot saved at",baseDataDir+combinedPlotName+".svg"                      

def main():
    combinedPlotName="test"

    indices=[0,1]
    base = arange(0.0, 10.0, 1.0)

    thisFunctionNames=[functionNames.keys()[index] for index in indices]
    thisColorNames=[colorNames.keys()[index] for index in indices]
    thisMarkerNames=[markerNames.keys()[index] for index in indices]

    fValues=[functionNames[functionName](base) for functionName in thisFunctionNames]
    axis=[np.min(base),np.max(base),np.min(fValues),np.max(fValues)]
    
    for functionName in thisFunctionNames:
        for markerName in thisMarkerNames:
            for colorName in thisColorNames:
                plotSingle(\
                xs=base,\
                functionName=functionName,\
                markerName=markerName,\
                colorName=colorName,\
                combinedPlotName=combinedPlotName,\
                axis=axis,\
                putGrid=False\
                )
                plt.clf()    
    
    print "\n--------------------------\n"
    print "plotting multiple functions"
    print "\n--------------------------\n"

    plotMultiple(\
        xs=base,\
        thisFunctionNames=thisFunctionNames,\
        thisMarkerNames=thisMarkerNames,\
        thisColorNames=thisColorNames,\
        combinedPlotName=combinedPlotName,\
        axis=axis,\
        putGrid=False\
        )
    plt.clf()

if __name__ == "__main__":
    main()


