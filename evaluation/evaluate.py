import sys
import json

import numpy as np
from skimage.io import imread

configs=["evalconfigs/1.json"]

def getScore(gold,predicted):
    goldImage=imread(gold)[:,:,:-1] #removing alpha
    predictedImage=imread(predicted)[:,:,:-1]
    return len(np.nonzero(goldImage-predictedImage)[0])

	
def map(golds,predicteds):
    scoreMatrix=np.zeros((len(golds),len(predicteds)))
    for gIndex,gold in enumerate(golds):
        for pIndex,predicted in enumerate(predicteds):
	    scoreMatrix[gIndex][pIndex]=getScore(gold,predicted)   
    return scoreMatrix
          

def main():
    j=json.load(open(configs[0]))
    golds=j['goldcurves']
    predicteds=j['predictedcurves']
    print map(golds,predicteds)

if __name__ == "__main__":
	main()
