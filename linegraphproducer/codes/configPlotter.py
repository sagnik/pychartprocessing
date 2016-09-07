from progconfigs.functions import functionNames
from progconfigs.markers import markerNames
from progconfigs.colors import colorNames

import matplotlib.pyplot as plt
import sys
from numpy import arange
import numpy as np
import os
import sys
import xml.etree.ElementTree as ET
from pprint import pprint

baseDataDir="../data/"    
baseConfigDir="../configs"

def plotSingle(xs,functionName,markerName,colorName,combinedPlotName,axis,putGrid=False):
    plt.plot(\
        xs,\
        functionNames[functionName](xs),\
        marker=markerNames[markerName],\
        color=colorNames[colorName],\
        label=functionName,\
        linestyle='dashed'
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


def plotMultiple(xs,plots,combinedPlotName,axis,putGrid=False):
    
    for plot in plots:
        plt.plot(\
            xs,\
            functionNames[plot['function']](xs),\
            marker=markerNames[plot['marker']],\
            color=colorNames[plot['color']],\
            label=plot['function']+"-"+plot['marker']+"-"+plot['color'],\
            linestyle="dashed"
            )
        print "plotting function",plot['function'],"with marker",plot['marker'],"and color",plot['color']

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
    plt.savefig(directory+"/"+svgFile)

    print "plot saved at",baseDataDir+combinedPlotName+".svg"                      


def parseConfig(configStr):
    root = ET.fromstring(configStr)
    plots=[]
    for plot in root.iter('plot'):
        thisPlot={}
        thisPlot['function']=functionNames.keys()[int(plot.find('function').text)]
        thisPlot['marker']=markerNames.keys()[int(plot.find('marker').text)]
        thisPlot['color']=colorNames.keys()[int(plot.find('color').text)]
        plots.append(thisPlot)
    return plots    

def main():
    configName=sys.argv[1]
    combinedPlotName=configName
    plots=parseConfig(open("../configs/"+configName+".config").read())
    base = arange(0.0, 10.0, 1.0)
    fValues=[functionNames[plot['function']](base) for plot in plots]
    #pprint(plots)
    #pprint(fValues)
    axis=[np.min(base),np.max(base),np.min(fValues),np.max(fValues)]      
    for plot in plots:
        plotSingle(\
                xs=base,\
                functionName=plot['function'],\
                markerName=plot['marker'],\
                colorName=plot['color'],\
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
        plots=plots,\
        combinedPlotName=combinedPlotName,\
        axis=axis,\
        putGrid=False\
        )
    plt.clf()
    
if __name__ == "__main__":
    main()


