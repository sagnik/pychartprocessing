import sys
import json

import numpy as np
from skimage.io import imread
from munkres import Munkres

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
    scoreMatrix=scoreMatrix.astype(int).tolist() 
    m = Munkres()
    indexes = m.compute(scoreMatrix)
    total=0
    for row, column in indexes:
        value = scoreMatrix[row][column]
        total += value
    return total
   
          

def main():
    j=json.load(open(configs[0]))
    golds=j['goldcurves']
    predicteds=j['predictedcurves']
    print map(golds,predicteds)

if __name__ == "__main__":
	main()
