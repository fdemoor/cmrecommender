import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
import matplotlib


################################################################################


X_LABEL = "Number of items in profile"
Y1_LABEL = "Number of users"
Y2_LABEL = "CDF"

q = 0.25
basedir = "../output/ml-1m_filtered/cosineCM_" + str(q) + "_kEval/"
filename = "dist.csv"


def parseDist(path):
  N = []
  BETAP = []
  with open(path, "r") as f:

    for line in f:
      data = line.rstrip("\n").split(",")
      N.append(int(data[0]))
      if len(data) > 2:
        BETAP.append((float(data[1]), float(data[2])))

  return N, BETAP


def plotN(N, ax1, ax2):

  npData = np.array(N)
  sns.distplot(npData, kde=False, bins=100, ax=ax1)
  sns.distplot(npData, hist=False, kde_kws=dict(cumulative=True), bins=100, ax=ax2)
  ax1.set_xlabel("Number of items in profile")
  ax1.set_ylabel("Number of users")
  ax2.set_ylabel("CDF")
  ax2.set_yticks(np.linspace(ax2.get_yticks()[0],ax2.get_yticks()[-1],len(ax1.get_yticks())))
  ax1.set_xlim([min(npData), max(npData)])
  
  
def plotTradeoff(BETAP, ax):
  
  BETAP.sort(key = lambda x: x[1])

  npBETAP = np.array(BETAP)
  X = np.arange(len(BETAP))
  
  ZEROS = np.zeros(len(X))
  ax.plot(X, ZEROS, color='black', linewidth=3)
  
  cmapBeta = matplotlib.cm.get_cmap('coolwarm')
  cmapP = matplotlib.cm.get_cmap('RdYlGn_r')
  
  ax.bar(X, npBETAP[:,0], color=cmapBeta(npBETAP[:,0]))
  ax.bar(X, -npBETAP[:,1], color=cmapP(npBETAP[:,1]))
  ax.set_ylim([-1, 1])
  ax.set_yticklabels([str(abs(x)) for x in ax.get_yticks()])
  ax.set_xlabel("Users")
  ax.set_ylabel("Bottom: Guassibility\n Top: Non-exact retrieve probability")
  ax.set_xticklabels([])


def main():

    sns.set(font_scale=2)
    sns.set_style("whitegrid")
    sns.set_palette(sns.color_palette("hls", 8))

    # Retrieve data from file
    N, BETAP = parseDist(basedir + filename)

    # Profile size distribution
    f1, ax1 = plt.subplots(1, 1, figsize=(10, 6))
    ax12 = ax1.twinx()
    plotN(N, ax1, ax12)
    sns.despine(ax=ax1, offset=10, trim=True)
    sns.despine(ax=ax12, offset=10, trim=True, left=True, right=False)
    f1.tight_layout(h_pad=3)
    f1.savefig(basedir + "/profileSizeDist.svg");

    # CM config distribution
    if BETAP != []:
      f2, ax2 = plt.subplots(1, 1, figsize=(10, 6))
      plotTradeoff(BETAP, ax2)
      sns.despine(ax=ax2, offset=10, trim=True)
      f2.tight_layout(h_pad=3)
      f2.savefig(basedir + "/tradeoffDist.svg");

main()
