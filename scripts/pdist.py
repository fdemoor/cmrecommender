import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt


################################################################################


X_LABEL = "Number of items in profile"
Y_LABEL = "Number of users"

basedir = "../output/ml-100k/stats/"
filename = "pdist.csv"


def parseProfileDistFile(path):
  f = open(path, "r")
  data = []
  for line in f:
    data.append(int(line.rstrip("\n")))
  f.close()
  return data
  
  
def plotPDist(ax):
  
  data = parseProfileDistFile(basedir + filename)
  npData = np.array(data)
  sns.distplot(npData, kde=False, bins=100, ax=ax)
  ax.set_xlabel(X_LABEL)
  ax.set_ylabel(Y_LABEL)


def main():

    sns.set(font_scale=2)
    sns.set_style("whitegrid")
    sns.set_palette(sns.color_palette("hls", 8))

    f1, ax1 = plt.subplots(1, 1, figsize=(10, 6))

    plotPDist(ax1)

    sns.despine(offset=10, trim=True)
    f1.tight_layout(h_pad=3)
    f1.savefig(basedir + "/plotPDist.svg");

main()
