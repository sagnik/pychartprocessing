from __future__ import division

import sys
import json

import numpy as np
from skimage.io import imread,imshow
from munkres import Munkres
configs=["evalconfigs/1.json"]

def getScore(gold,predicted):
    goldImage=imread(gold)[:,:,:-1] #removing alpha
    predictedImage=imread(predicted)[:,:,:-1]

    cp1 = goldImage != 0
    curvePixelsGold=[(i,j) for i in range(cp1.shape[0]) for j in range(cp1.shape[1]) if cp1[i][j].any()]   
 
    cp2 = predictedImage != 0
    curvePixelsPredicted=[(i,j) for i in range(cp2.shape[0]) for j in range(cp2.shape[1]) if cp2[i][j].any()]   
 
    bothGoldandPredicted=len(set(curvePixelsGold).intersection(set(curvePixelsPredicted)))
    matched = len(curvePixelsGold)-bothGoldandPredicted
    #print gold,predicted,matched
    return matched 
	
def map(golds,predicteds):
    scoreMatrix=np.zeros((len(golds),len(predicteds)))
    for gIndex,gold in enumerate(golds):
        for pIndex,predicted in enumerate(predicteds):
	    scoreMatrix[gIndex][pIndex]=getScore(gold,predicted)
    scoreMatrix=scoreMatrix.astype(int).tolist() 
    m = Munkres()
    indices = m.compute(scoreMatrix)
    return [(golds[pair[0]],predicteds[pair[1]]) for pair in indices]

def score(gold,predicted):
    print "comparing",gold,"with",predicted
    goldImage=imread(gold)[:,:,:-1]
    predictedImage=imread(predicted)[:,:,:-1]
   
    if goldImage.shape != predictedImage.shape:
        raise IOError("the gold image and predicted image dimensions are not the same, exiting")
    else:
        cp1 = goldImage != 0
        curvePixelsGold=[(i,j) for i in range(cp1.shape[0]) for j in range(cp1.shape[1]) if cp1[i][j].any()]   
 
        cp2 = predictedImage != 0
        curvePixelsPredicted=[(i,j) for i in range(cp2.shape[0]) for j in range(cp2.shape[1]) if cp2[i][j].any()]   
 
        bothGoldandPredicted=len(set(curvePixelsGold).intersection(set(curvePixelsPredicted)))
        justGold=len(set(curvePixelsGold)-set(curvePixelsPredicted))
        justPredicted=len(set(curvePixelsPredicted)-set(curvePixelsGold))
        
        print bothGoldandPredicted,justGold,justPredicted           
        precision = bothGoldandPredicted/(bothGoldandPredicted+justPredicted)
        recall = bothGoldandPredicted / (bothGoldandPredicted+justGold)
        f1score = 2 * (precision * recall) / (precision + recall)
        return (precision,recall,f1score)  
         
          

def main():
    j=json.load(open(configs[0]))
    golds=j['goldcurves']
    predicteds=j['predictedcurves']
    print "trying to match gold and predicted images"
    maps=map(golds,predicteds)
    print "gold and predicted images matched" 
    scores=[score(gp[0],gp[1]) for gp in maps]
    print scores 

if __name__ == "__main__":
	main()
