import sys
import json

from skimage.io import imread

configs=["evalconfigs/1.json"]

def getScore(gold,predicted):
    goldImage=imread(gold)
    predictedImage=imread(predicted)
    print "gold",goldImage.size,"predicted",predictedImage.size
    return goldImage.size[0]

	
def map(golds,predicteds):
	scorematrix=[]
	for gIndex,gold in enumerate(golds):
	    for pIndex,predicted in enumerate(predicteds):
	        scorematrix[gIndex][pIndex]=getScore(gold,predicted)
	print scorematrix                   

def main():
	j=json.load(open(configs[0]))
    golds=j['goldcurves']
    predicteds=j['predictedcurves']
    map(golds,predicteds)

if __name__ == "__main__":
	main()
