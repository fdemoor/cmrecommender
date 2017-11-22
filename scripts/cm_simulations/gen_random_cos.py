import numpy as np
import random
import string
import os
import sys


u = 500
datasetId = 1
sizeCorpus = 100
seed = 16837242

output = 'datasets/random_cos/' + str(datasetId) + '/'
os.system('mkdir -p ' + output)

### MAIN

np.random.seed(seed)
random.seed(seed)

Ukeys = [''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(10)) for _ in range(u)]
for j in range(sizeCorpus):
  n = np.random.randint(2, u)
  keys = np.random.choice(Ukeys, n, replace=False)
  values = np.random.randint(1, 50, size=n)
  with open(output + str(j) + '.csv', 'w')as f:
    for i in range(n):
      f.write(keys[i] + ',' + str(values[i]) + '\n')
      
with open(output + 'params.log', 'w') as f:
  f.write('u = ' + str(u) + '\n')
  f.write('datasetID = ' + str(datasetId) + '\n')
  f.write('sizeCorpus = ' + str(sizeCorpus) + '\n')
  f.write('seed = ' + str(seed) + '\n')
