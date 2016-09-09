import sys
import random

def main():
    twoFunctions=\
"""<?xml version="1.0"?>
   <!-- generated automatically, do not edit -->
    <config>
        <plot>
            <function>{0}</function>
            <marker>{1}</marker>
            <color>{2}</color>
            <linestyle>{3}</linestyle>
        </plot>
        <plot>
            <function>{4}</function>
            <marker>{5}</marker>
            <color>{6}</color>
            <linestyle>{7}</linestyle>
        </plot>
    </config>"""
    for i in range(100):
        with open("../configs/{0}.config".format(i),"w") as f:
            functions=range(7)
            random.shuffle(functions)
            markers=range(7)
            random.shuffle(markers)
            colors=range(140)
            random.shuffle(colors)
            f.write(\
             twoFunctions.format(\
              functions[0],\
              markers[0],\
              colors[0],\
              1,\
              functions[1],\
              markers[1],\
              colors[1],\
              1\
             )\
            ) 
            #only using dashed lines for now because they are more challenging
            
    

if __name__ == "__main__":
    main() 

