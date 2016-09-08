#always should have functions that can operate on numpy arrays
from __future__ import division
from numpy import square,power,log

def cube(x):
    return power(x,3)

def squareinverse(x):
    return 1/power(x,2)

def inverse(x):
    return 1/x

def loginverse(x):
    return 1/log(x)

def cubeinverse(x):
    return 1/cube(x)

functionNames={
    'square': square,
    'cube': cube,
    'inverse': inverse,
    'squareinverse':squareinverse,
    'log': log,
    'loginverse': loginverse,
    'cubeinverse': cubeinverse
}
