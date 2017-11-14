import cPickle as pickle
import os
import sys

import plot

if len(sys.argv) < 2:
  print "Missing argument"
  print "Usage: python plot_results.py result_data_file.p"
  exit(1)

filename = sys.argv[1]
print "Got data file", filename

with open(filename, 'rb') as f:
  r = pickle.load(f)
  
os.system('mkdir -p ' + r.output)

plot.plot_init()

for i in range(len(r.names)):
  
  f, ax = plot.run_plot(r.X, r.Y, r.Zs[i], r.names[i])
 
  ax.set_xlabel(r.xlabel)
  ax.set_ylabel(r.ylabel)
  if r.xticks != []:
    ax.set_xticks(r.xticks)
  if r.yticks != []:
    ax.set_yticks(r.yticks)
  if r.xticklabels != []:
    ax.set_xticklabels(r.xticklabels, rotation=40, ha='right')
  if r.yticklabels != []:
    ax.set_yticklabels(r.yticklabels)

  #xticklabels = ax.get_xticklabels()
  #xticklabels[0].set_visible(False)

  plot.save_plot(f, r.output + r.names[i])
