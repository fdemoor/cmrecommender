import numpy as np
import math
import random
import string
import os
import sys
import cPickle as pickle
from threading import Thread, RLock

import sketch
import opti
import results

lock = RLock()

if len(sys.argv) < 2:
  print "Missing argument"
  print "Usage: python run_random_cos.py <datasetFolder>"
  exit(1)

datasetFolder = sys.argv[1]
print "Got dataset folder:", datasetFolder
if not datasetFolder.endswith('/'):
  datasetFolder = datasetFolder + '/'

files = []
for f in os.listdir(datasetFolder):
  if f.endswith('.csv'):
    files.append(datasetFolder + '/' + f)

with open(datasetFolder + 'params.log', 'r') as f:
  for line in f:
    data = line.rstrip('\n').split(' ')
    if data[0] == "u":
      u = int(data[2])
      break

output = 'output/' + datasetFolder.replace('datasets/', '')
os.system('mkdir -p ' + output)


def cosine(pa, pb):
  ab = 0
  aa = 0
  bb = 0
  for pair1 in pa:
    aa += pair1[1] * pair1[1]
    for pair2 in pb:
      if pair1[0] == pair2[0]:
        ab += pair1[1] * pair2[1]
        break
  for pair2 in pb:
    bb += pair2[1] * pair2[1]
  if aa == 0 or bb == 0:
    return float('NaN')
  return ab / math.sqrt(float(aa * bb))
  
  
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
  
 
class OneSimulation(Thread):
	
	def __init__(self, datasets, u, q, index, X, Y, Z, names):
		Thread.__init__(self)
		self.datasets = datasets
		self.u = u
		self.q = q
		self.index = index
		self.X = X
		self.Y = Y
		self.Y = Z
		self.names = names
		
	def run(self):
		i = 0
		for f1 in files:
			for f2 in files:
				if f1 != f2:
					i += 1
					print "Progression:", i, "/", (len(files) - 1) ** 2
					r = runSimulation(self.datasets, f1, f2, nValues[f1], self.u, self.q)
					with lock:
						self.X.append(nValues[f1])
						self.Y.append(self.index)
						for k in range(len(self.names)):
							self.Z[k].append(r[k])
		

def runSimulation(datasets, f1, f2, n, u, q):
  
  pairs1 = datasets[f1]
  pairs2 = datasets[f2]
  
  w, d = opti.getSize(n, u, q)
  eps = np.exp(1) / w
  delta = np.exp(-d)
  #print "n =", n, "u =", u, "q =", q, "width =", w, "depth =", d
  
  error = 0

  s1 = sketch.Sketch(delta, eps, 10000, 0)
  s2 = sketch.Sketch(delta, eps, 10000, 0)
  
  for pair in pairs1:
      s1.update(pair[0], pair[1])
  for pair in pairs2:
      s2.update(pair[0], pair[1])
      
  y = cosine(pairs1, pairs2)
  x = cosineCM(s1, s2, w, d)
  
  #print "cosine is", y, "we got", x
        
  error += abs(x - y)
      
  gamma = opti.gammaDeniability(w, d, n, u)
  beta = opti.pointErrorProba(w, d, n)
  size = w * d
  fmeasure = opti.Fmeasure(w, d, n, u, q)
      
  return (error, gamma, beta, size, w, d, fmeasure)


### MAIN

nValues = {}
datasets = {}
for filename in files:
  pairs = []
  with open(filename, 'r') as f:
    for line in f:
      data = line.rstrip('\n').rstrip('\r').split(',')
      key = data[0]
      value = int(data[1])
      pairs.append((key, value))
  datasets[filename] = pairs
  nValues[filename] = len(pairs)

qList = np.array([1.0 / 4.0, 1.0 / 3.0, 0.5, 1.0, 2.0, 3.0, 4.0], dtype=float)
indexList = np.arange(len(qList))

names = ['error', 'gamma', 'beta', 'size', 'width', 'depth', 'fmeasure']
X = []
Y = []
Z = [[] for k in range(len(names))]

threads = []
for index in indexList:
  q = qList[index]
  thread = OneSimulation(datasets, u, q, index, X, Y, Z, names)
  threads.append(thread)
  thread.start()
    
for t in threads:
	t.join()

r = results.Results(output, names, X, Y, Z)
r.setX("Number of exported keys")
r.setY("Priority", indexList, ["F" + str(q)[:4] for q in qList])

with open(output + "data.p", "wb") as f:
  pickle.dump(r, f)
 
  
  
