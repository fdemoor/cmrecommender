import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt


################################################################################


RMSE_LABEL = "RMSE"
K_LABEL = "k (size of neighborhood)"
SIM_LABEL = "Similarity metric"

SIM_LABELS = {"cosine": "cos", "cosineCM": "cosCM"}

basedir = "../output/ml-100k"
filename = "rmse.csv"


################################################################################
#                     MARKER CLASS                                             #
################################################################################

class Markers:
  
  def __init__(self):
    self.markerList = ["s", "o", "d", "v", "<"]
    self.index = 0
  
  def nextMarker(self):
    m = self.markerList[self.index]
    self.index = (self.index + 1) % len(self.markerList)
    return m
    
markers = Markers()
    
################################################################################


def parseKFile(path):
  f = open(path, "r")
  allData = []
  for line in f:
    data = line.rstrip("\n").split(",")
    if (data[0] != "NaN"):
      allData.append([int(data[1]), float(data[0])])
  f.close()
  return allData
  
  
def plotK(ax, dirs):
  
  for directory in dirs:
    
    label = SIM_LABELS[directory.split("_")[0]]
    path = basedir + "/" + directory + "/" + filename
    data = parseKFile(path)
    npData = np.array(data)
    ax.plot(npData[:, 0], npData[:, 1], label=label, marker=markers.nextMarker())
    
  ax.set_xlabel(K_LABEL)
  ax.set_ylabel(RMSE_LABEL)


def main():

    sns.set(font_scale=2)
    sns.set_style("whitegrid")
    sns.set_palette(sns.color_palette("hls", 8))

    f1, ax1 = plt.subplots(1, 1, figsize=(10, 6))

    dirs = ["cosine_kEval"]
    plotK(ax1, dirs)
    
    plt.legend()

    sns.despine(offset=10, trim=True)
    f1.tight_layout(h_pad=3)
    f1.savefig(basedir + "/plotK.svg");

main()
