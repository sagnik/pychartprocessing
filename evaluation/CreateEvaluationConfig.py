import json
import sys
import os

def isNumber(x):
    try:
        #print x
        int(x)
        return True
    except ValueError:
        return False
   
def gold(x):
    return 'Curve' in x and x.endswith('png') and isNumber(x.split('Curve-')[1].split('.png')[0])

def predicted(x):
    return 'Curve' in x and x.endswith('png') and not isNumber(x.split('Curve-')[1].split('.png')[0])
    
def main():
    atomicSVGDir=sys.argv[1]
    evalJson="evalconfigs/"+os.path.split(atomicSVGDir)[1]+".json"
    fs=os.listdir(atomicSVGDir)
    evalConfig={
    "orgfile":atomicSVGDir+".svg",
    "goldcurves":[os.path.join(atomicSVGDir,x) for x in fs if gold(x)],
    "predictedcurves":[os.path.join(atomicSVGDir,x) for x in fs if predicted(x)]
    }
    with open(evalJson,"wb") as f:
        f.write(json.dumps(evalConfig, indent=4, sort_keys=True)) 
    print "produced evaluation config",evalJson
        
  
      

if __name__ == "__main__":
    main()
