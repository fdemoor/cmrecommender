import numpy as np
import random
import os
import cPickle as pickle

import sketch
import opti
import results

feature = 'domain_malware'
featureFolder = 'datasets/AMICO/' + feature
files = []
for f in os.listdir(featureFolder):
  if f.endswith('.csv'):
    files.append(featureFolder + '/' + f)
    
output = 'output/AMICO/' + feature + '/'
os.system('mkdir -p ' + output)

U = 663


def runSimulation(n, u, q, datasets):
  """
  Run a simulation to compute an average error on the point-querry
  """
  pairs = datasets[n]
  if n == 0:
    return (float('NaN'), float('NaN'), float('NaN'), float('NaN'), float('NaN'), float('NaN'), float('NaN'))
  w, d = opti.getSize(n, u, q)
  eps = np.exp(1) / w
  delta = np.exp(-d)
  print "n =", n, "u =", u, "q =", q, "width =", w, "depth =", d
  
  errorInserted = 0
  totalInserted = 0

  s = sketch.Sketch(delta, eps, 10000, 0)
  for pair in pairs:
    s.update(pair[0], pair[1])
    
  for pair in pairs:
    x = s.get(pair[0])
    y = pair[1]
    errorInserted += abs(x - y) / float(y)
    totalInserted += 1
      
  meanErrorInserted = errorInserted / float(totalInserted)
  gamma = opti.gammaDeniability(w, d, n, u)
  beta = opti.pointErrorProba(w, d, n)
  size = w * d
  fmeasure = opti.Fmeasure(w, d, n, u, q)
  p = opti.probaInserted(w, d, n, u)

  return (meanErrorInserted, gamma, beta, size, w, d, fmeasure, p)


### MAIN
  
qList = np.array([1.0 / 4.0, 1.0 / 3.0, 0.5, 1.0, 2.0, 3.0, 4.0], dtype=float)
indexList = np.arange(len(qList))

nValues = []
datasets = {}
for filename in files:
  pairs = []
  with open(filename, 'r') as f:
    for line in f:
			data = line.rstrip('\n').split(',')
			key = data[0]
			value = int(data[1])
			pairs.append((key, value))
  datasets[len(pairs)] = pairs
  nValues.append(len(pairs))
  
nValues.sort()
nList = np.arange(len(nValues))
  
X, Y = np.meshgrid(nList, qList)
XX, YY = np.meshgrid(nList, indexList)

index2n = lambda x: nValues[x]
index2n_vect = np.vectorize(index2n)

run_vect = np.vectorize(runSimulation)
Z = run_vect(index2n_vect(X), U, Y, datasets)

names = ['error_inserted', 'gamma', 'beta', 'size', 'width', 'depth', 'fmeasure', 'proba_inserted']
Zs = [Z[:][:][i] for i in range(len(names))]

r = results.Results(output, names, XX, YY, Zs)
r.setX("Number of exported keys", nList, nValues)
r.setY("Priority", indexList, ["F" + str(q)[:4] for q in qList])

with open(output + "data.p", "wb") as f:
  pickle.dump(r, f)
