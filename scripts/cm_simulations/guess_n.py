import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import math
import random
import string
import sys

import sketch
import opti

MAX_N = 378
#U = 1125752
U = 1000


def runSimulation(n, u, q):
  """
  Run a simulation to compute an average error on the point-querry
  """
  w, d = getSize(n, u, q)
  eps = np.exp(1) / w
  delta = np.exp(-d)
  print "n =", n, "u =", u, "q =", q, "width =", w, "depth =", d
  
  error = 0
  total = 0
  
  for k in range(10):
      
    keys = [''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(10)) for _ in range(n)]
    values = np.random.rand(n) * 9 + 1
  
    s = sketch.Sketch(delta, eps, 10000, 1)
    
    for j in range(len(keys)):
      s.update(keys[j], values[j])
        
    y = n
    
    m = s.non_empty()
    if m == w:
      continue
    x = math.ceil(math.log(1 - m / float(w)) / math.log(1 - 1 / float(w)))
    
    print "Real n is", y, "Estimated is", x
          
    error += abs(x - y) / float(y)
    total += 1
      
  meanError = error / float(total)
      
  return meanError



### MAIN

random.seed("1683726262826")

#nList = np.arange(1, MAX_N + 1, MAX_N / 25)
nList = np.linspace(1, 6, 10)[1:]
qList = np.array([1.0 / 4.0, 1.0 / 3.0, 0.5, 1.0, 2.0, 3.0, 4.0], dtype=float)
indexList = np.arange(len(qList))

X, Y = np.meshgrid(nList, qList)
XX, YY = np.meshgrid(nList, indexList)

zfunc = np.vectorize(runSimulation)
N = np.array(U / nList, dtype=int)
#Z = zfunc(nList, U, Y, 5)
Z = zfunc(N, U, Y)

names = ['error']
run_plot.plot_init()

for i in range(len(names)):
  
  f, ax = run_plot.run_plot(XX, YY, Z[:][:][i], names[i])
    
  #ax.set_xlabel("Number of exported keys (Total number of keys = " + str(U) + ")")
  ax.set_xlabel("Total number of keys / Number of exported keys")
  ax.set_ylabel("Priority")
  ax.set_yticks(indexList)
  ax.set_yticklabels(["F" + str(q)[:4] for q in qList])
  xticklabels = ax.get_xticklabels()
  xticklabels[0].set_visible(False)
  
  run_plot.save_plot(f, names[i], '_guess_n')
