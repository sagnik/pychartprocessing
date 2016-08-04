from numpy import arange, sin, pi
import matplotlib.pyplot as plt
import sys

def main():
    marker1={'id':'x','color': 'darkmagenta'}
    marker2={'id': '+', 'color': 'springgreen'}
    t = arange(0.0, 1.0, 0.01)
    plt.plot(t,sin(2*pi*t),marker=marker1['id'],color=marker1['color'])
    plt.plot(t,sin(-2*pi*t),marker=marker2['id'],color=marker2['color'])
    plt.xlabel('some numbers')
    plt.grid(True)
    plt.ylabel('1 Hz')
    plt.title('A sine wave or two')
    sv=""
    if sys.argv[1].endswith(".svg"):
        sv=("../data/"+sys.argv[1])
    else:
        sv=("../data/"+sys.argv[1]+".svg")
    plt.savefig(sv)

if __name__ == "__main__":
    main()


