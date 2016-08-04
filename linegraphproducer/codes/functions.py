#always should have functions that can operate on numpy arrays

from numpy import square,power,sin,cos

def cube(x):
    return power(x,3)

functionNames={
'square': square,
'cube': cube,
}
