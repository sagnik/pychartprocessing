import sys
import json
import os
import numpy as np

def main():
    jsons=os.listdir(sys.argv[1])
    precisions=[]
    recalls=[]
    f1scores=[]
    for j in jsons:
        jc=json.load(open(os.path.join(sys.argv[1],j)))
        for item in jc:
            precisions.append(item['precision'])
            recalls.append(item['recall'])
            f1scores.append(item['f1score'])
     
    print "average precision",np.average(precisions)        
    print "average recall",np.average(recalls)        
    print "average f1 score",np.average(f1scores)        
 
if __name__ == "__main__":
    main()       


