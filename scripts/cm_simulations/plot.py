import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
import math
import random
import string

cmaps = {       'error_inserted': 'coolwarm',
                'error_not_inserted': 'coolwarm',
                'error': 'coolwarm',
                'gamma': 'RdYlGn',
                'beta': 'coolwarm',
                'size': 'Wistia',
                'width': 'Greens',
                'depth': 'Blues',
                'fmeasure': 'RdYlGn',
                'proba_inserted': 'RdYlGn_r' }

thresholds = {  'error_inserted': True,
                'error_not_inserted': False,
                'error': False,
                'gamma': True,
                'beta': True,
                'size': False,
                'width': False,
                'depth': False,
                'fmeasure': True,
                'proba_inserted': True }
          
scales = {      'error_inserted': 500,
                'error_not_inserted': 400,
                'error': 1000,
                'gamma': 400,
                'beta': 1500,
                'size': 1,
                'width': 2,
                'depth': 20,
                'fmeasure': 400,
                'proba_inserted': 400 }
          
labels = {      'error_inserted': 'Error on inserted keys',
                'error_not_inserted': 'False positive frequency',
                'error': 'Error',
                'gamma': 'Deniability',
                'beta': 'Non-exact retrieve probability',
                'size': 'Size',
                'width': 'Width',
                'depth': 'Depth',
                'fmeasure': 'F-Measure',
                'proba_inserted': 'Guessability' }
                

def plot_init():
  sns.set(font_scale=2)
  sns.set_style("whitegrid")

def run_plot(X, Y, Z, name):
  
  f, ax = plt.subplots(1, 1, figsize=(10, 6))
  data = np.ma.masked_invalid(Z)
  if thresholds[name]:
    im = ax.scatter(X, Y, c=data, cmap=cmaps[name], s=data * scales[name] + 20, vmax=1, vmin = 0, edgecolor='black', linewidth='1')
  else:
    im = ax.scatter(X, Y, c=data, cmap=cmaps[name], s=data * scales[name] + 20, edgecolor='black', linewidth='1')
  cbar = plt.colorbar(im)
  cbar.set_label(labels[name])
  
  return f, ax
  
def save_plot(f, name):
  f.tight_layout(h_pad=3)
  f.savefig(name + '.svg')
