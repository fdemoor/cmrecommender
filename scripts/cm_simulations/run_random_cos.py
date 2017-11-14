import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
import math
import random
import string
import os
import cPickle as pickle

import sketch
import opti
import results

MAX_N = 378
#U = 1125752
U = 1000

output = 'output_random_cos/'
os.system('mkdir -p ' + output)


def genRandom(n):
  """
  Returns n random key-value pairs
  
  Keys are random sequence of 10 letters
  Values are random floats in [1, 10]
  """
  keys = [''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(10)) for _ in range(n)]
  values = np.random.rand(n) * 9 + 1
  return keys, values
  

def cosine(a, b):
  ab = np.inner(a, b)
  aa = np.inner(a, a)
  bb = np.inner(b, b)
  if aa == 0.0 or bb == 0.0:
    return float('NaN')
  return ab / math.sqrt(aa * bb)
  
  
def cosineCM(s1, s2, w, d):
  minCos = sys.maxsize
  for j in range(d):
    ab = 0
    aa = 0
    bb = 0
    for i in range(w):
      x = s1.at(i, j)
      y = s2.at(i, j)
      ab += x * y
      aa += x * x
      bb += y * y
    if aa != 0.0 and bb != 0.0:
      s = ab / math.sqrt(aa * bb)
      if s < minCos:
        minCos = s
  if minCos == sys.maxsize:
    return float('NaN')
  return minCos


def runSimulation(n, u, q):
  """
  Run a simulation to compute an average error on the point-querry
  """
  w, d = opti.getSize(n, u, q)
  eps = np.exp(1) / w
  delta = np.exp(-d)
  print "n =", n, "u =", u, "q =", q, "width =", w, "depth =", d
  
  error = 0
  total = 0
  
  for k in range(5):
  
    for i in range(2, 10):
      
      keys = [''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(10)) for _ in range(n)]
      values1 = np.random.rand(n) * 9 + 1
      values1 = np.where(np.random.rand(n) < i / 10.0, 0, values1)
      values2 = np.random.rand(n) * 9 + 1
      values2 = np.where(np.random.rand(n) < i / 10.0, 0, values2)
    
      s1 = sketch.Sketch(delta, eps, 10000, 1)
      s2 = sketch.Sketch(delta, eps, 10000, 1)
      
      for j in range(len(keys)):
        if values1[j] > 0:
          s1.update(keys[j], values1[j])
        if values2[j] > 0:
          s2.update(keys[j], values2[j])
          
      y = cosine(values1, values2)
      x = cosineCM(s1, s2, w, d)
            
      error += abs(x - y)
      total += 1
      
  meanError = error / float(total)
  gamma = opti.gammaDeniability(w, d, n, u)
  beta = opti.pointErrorProba(w, d, n)
  size = w * d
  fmeasure = opti.Fmeasure(w, d, n, u, q)
      
  return (meanError, gamma, beta, size, w, d, fmeasure)



### MAIN

random.seed("1683726262826")

#nList = np.arange(1, MAX_N + 1, MAX_N / 25)
nList = np.linspace(1, 6, 25)[1:]
qList = np.array([1.0 / 4.0, 1.0 / 3.0, 0.5, 1.0, 2.0, 3.0, 4.0], dtype=float)
indexList = np.arange(len(qList))

X, Y = np.meshgrid(nList, qList)
XX, YY = np.meshgrid(nList, indexList)

zfunc = np.vectorize(runSimulation)
N = np.array(U / nList, dtype=int)
#Z = zfunc(nList, U, Y, 5)
Z = zfunc(N, U, Y)


names = ['error', 'gamma', 'beta', 'size', 'width', 'depth', 'fmeasure']
Zs = [Z[:][:][i] for i in range(len(names))]

r = results.Results(output, names, XX, YY, Zs)
r.setX("Number of exported keys")
r.setY("Priority", indexList, ["F" + str(q)[:4] for q in qList])

with open(output + "data.p", "wb") as f:
  pickle.dump(r, f)
