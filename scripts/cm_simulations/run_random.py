import numpy as np
import random
import string
import os
from threading import Thread, RLock
import cPickle as pickle

import sketch
import opti
import results

output = 'output/random/'
os.system('mkdir -p ' + output)

U = 663
MAX_N = U

lock = RLock()

class OneSimulation(Thread):
  
  def __init__(self, n, u, delta, eps, results, index):
    Thread.__init__(self)
    self.n = n
    self.u = u
    self.delta = delta
    self.eps = eps
    self.results = results
    self.index = index
    
    
  def genRandomPairs(self, a, b):
    """
    Returns n random key-value pairs
    
    Keys are random sequence of 10 letters
    Values are random floats in [1, 10]
    """
    keys = [''.join(random.SystemRandom().choice(string.ascii_uppercase + string.digits) for _ in range(10)) for _ in range(a)]
    values = np.random.rand(b) * 9 + 1
    return keys, values
    
    
  def run(self):
    
    errorInserted = 0
    totalInserted = 0
  
    errorNotInserted = 0
    totalNotInserted = 0
    
    keys, values = self.genRandomPairs(self.n, self.n)
    s = sketch.Sketch(self.delta, self.eps, 10000, 1)
    
    for j in range(len(values)):
      s.update(keys[j], values[j])
      
    for j in range(len(values)):
      x = s.get(keys[j])
      errorInserted += abs(x - values[j]) / float(values[j])
      totalInserted += 1
      
    keys, values = self.genRandomPairs(100, 0)
    for j in range(100):
      x = s.get(keys[j])
      if x != 0:
        errorNotInserted += 1
      totalNotInserted += 1
    
    with lock:
      self.results[self.index, 0] = errorInserted
      self.results[self.index, 1] = totalInserted
      self.results[self.index, 2] = errorNotInserted
      self.results[self.index, 3] = totalNotInserted


def runSimulation(n, u, q):
  """
  Run a simulation to compute an average error on the point-querry
  """
  w, d = opti.getSize(n, u, q)
  eps = np.exp(1) / w
  delta = np.exp(-d)
  print "n =", n, "u =", u, "q =", q, "width =", w, "depth =", d
  
  errorInserted = 0
  totalInserted = 0
  
  errorNotInserted = 0
  totalNotInserted = 0
  
  threads = []
  nbSimu = 2
  results = np.zeros((nbSimu, 4), dtype=float)
  
  for i in range(nbSimu):
    thread = OneSimulation(n, u, delta, eps, results, i)
    threads.append(thread)
    thread.start()
    
  for t in threads:
    t.join()
    
  meanErrorInserted = sum(results[:,0]) / sum(results[:,1])
  meanErrorNotInserted = sum(results[:,2]) / sum(results[:,3])
  
  gamma = opti.gammaDeniability(w, d, n, u)
  beta = opti.pointErrorProba(w, d, n)
  size = w * d
  fmeasure = opti.Fmeasure(w, d, n, u, q)
  p = opti.probaInserted(w, d, n, u)
      
  return (meanErrorInserted, meanErrorNotInserted, gamma, beta, size, w, d, fmeasure, p)


### MAIN

random.seed("1683726262826")

nList = np.arange(2, MAX_N + 1, MAX_N / 25)[:-1]
#nList = np.linspace(1, 5, 50)[1:]
qList = np.array([1.0 / 4.0, 1.0 / 3.0, 0.5, 1.0, 2.0, 3.0, 4.0], dtype=float)
indexList = np.arange(len(qList))

X, Y = np.meshgrid(nList, qList)
XX, YY = np.meshgrid(nList, indexList)

zfunc = np.vectorize(runSimulation)
#N = np.array(U / nList, dtype=int)
#Z = zfunc(nList, U, Y, 5)
Z = zfunc(nList, U, Y)

names = ['error_inserted', 'error_not_inserted', 'gamma', 'beta', 'size', 'width', 'depth', 'fmeasure', 'proba_inserted']
Zs = [Z[:][:][i] for i in range(len(names))]

r = results.Results(output, names, XX, YY, Zs)
r.setX("Number of exported keys")
r.setY("Priority", indexList, ["F" + str(q)[:4] for q in qList])

with open(output + "data.p", "wb") as f:
  pickle.dump(r, f)
