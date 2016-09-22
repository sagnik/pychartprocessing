#always should have functions that can operate on numpy arrays
from __future__ import division
from numpy import exp,square,log

def logisticGrowth1(x):
    c=1;b=1;a=1;
    return 1*(c/(1+a*exp(-b*x)))

def logisticGrowth2(x):
    c=1;b=2;a=1;
    return 1*(c/(1+a*exp(-b*x)))

def logisticGrowth3(x):
    c=1;b=3;a=1;
    return 1*(c/(1+a*exp(-b*x)))

def logisticGrowth4(x):
    c=1;b=4;a=1;
    return 1*(c/(1+a*exp(-b*x)))

def logisticGrowth5(x):
    c=1;b=5;a=1;
    return 1*(c/(1+a*exp(-b*x)))

def logisticGrowth6(x):
    c=1;b=6;a=1;
    return 1*(c/(1+a*exp(-b*x)))

def logisticGrowth7(x):
    c=1;b=7;a=1;
    return 1*(c/(1+a*exp(-b*x)))

def inverse(x):
    return 1/x

def loginverse(x):
    return 1/(log(x)+0.00001)

def squareinverse(x):
    return 1/square(x)

def cube(x):
    return pow(x,3)

def cubeinverse(x):
    return 1/cube(x)

functionNames={
    'logisticgrowth1': logisticGrowth1,
    'logisticgrowth2': logisticGrowth2,
    'logisticgrowth3': logisticGrowth3,
    'logisticgrowth4': logisticGrowth4,
    'logisticgrowth5': logisticGrowth5,
    'logisticgrowth6': logisticGrowth6,
    'logisticgrowth7': logisticGrowth7,
}
