import sys

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
    for i in range(10):
        with open("../configs/{0}.config".format(i),"w") as f:
            f.write(twoFunctions.format(i,i,i,1,i+1,i+1,i+1,1)) 
            #only using dashed lines for now because they are more challenging
            
    

if __name__ == "__main__":
    main() 

