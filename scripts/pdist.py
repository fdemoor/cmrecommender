import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt


################################################################################


X_LABEL = "Number of items in profile"
Y1_LABEL = "Number of users"
Y2_LABEL = "CDF"

basedir = "../output/ml-100k/stats/"
filename = "pdist.csv"


def parseProfileDistFile(path):
  f = open(path, "r")
  data = []
  for line in f:
    data.append(int(line.rstrip("\n")))
  f.close()
  return data
  
  
def plotPDist(ax1, ax2):
  
  data = parseProfileDistFile(basedir + filename)
  npData = np.array(data)
  sns.distplot(npData, kde=False, bins=100, ax=ax1)
  sns.distplot(npData, hist=False, kde_kws=dict(cumulative=True), bins=100, ax=ax2)
  ax1.set_xlabel(X_LABEL)
  ax1.set_ylabel(Y1_LABEL)
  ax2.set_ylabel(Y2_LABEL)
  ax2.set_yticks(np.linspace(ax2.get_yticks()[0],ax2.get_yticks()[-1],len(ax1.get_yticks())))


def main():

    sns.set(font_scale=2)
    sns.set_style("whitegrid")
    sns.set_palette(sns.color_palette("hls", 8))

    f1, ax1 = plt.subplots(1, 1, figsize=(10, 6))
    ax2 = ax1.twinx()

    plotPDist(ax1, ax2)

    sns.despine(ax=ax1, offset=10, trim=True)
    sns.despine(ax=ax2, offset=10, trim=True, left=True, right=False)
    f1.tight_layout(h_pad=3)
    f1.savefig(basedir + "/plotPDist.svg");

main()
