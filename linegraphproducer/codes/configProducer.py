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
        </plot>
        <plot>
            <function>{3}</function>
            <marker>{4}</marker>
            <color>{5}</color>
        </plot>
    </config>"""
    for i in range(10):
        with open("../configs/{0}.config".format(i),"w") as f:
            f.write(twoFunctions.format(i,i,i,i+1,i+1,i+1))
            
    

if __name__ == "__main__":
    main() 

