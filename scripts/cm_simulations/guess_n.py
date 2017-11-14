import numpy as np
import pandas as pd
import seaborn as sns
import matplotlib.pyplot as plt
import math
import random
import string
import sys
import os
import cPickle as pickle

import sketch
import opti
import results

MAX_N = 378
#U = 1125752
U = 1000

output = 'output_guess_n/'
os.system('mkdir -p ' + output)


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
Zs = [Z[:][:][i] for i in range(len(names))]

r = results.Results(output, names, XX, YY, Zs)
r.setX("Number of exported keys")
r.setY("Priority", indexList, ["F" + str(q)[:4] for q in qList])

with open(output + "data.p", "wb") as f:
  pickle.dump(r, f)
