import matplotlib.pyplot as plt
import sys

def main():
    plt.plot([1,2,3,4])
    plt.ylabel('some numbers')
    sv=""
    if sys.argv[1].endswith(".svg"):
        sv=("../data/"+sys.argv[1])
    else:
        sv=("../data/"+sys.argv[1]+".svg") 
    plt.savefig(sv)

if __name__ == "__main__":
    main()
 
