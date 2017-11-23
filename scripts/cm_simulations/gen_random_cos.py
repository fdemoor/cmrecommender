import numpy as np
import random
import string
import os
import sys


u = 500
datasetId = 2
sizeCorpus = 30
seed = 16831627

output = 'datasets/random_cos/' + str(datasetId) + '/'
os.system('mkdir -p ' + output)

### MAIN

np.random.seed(seed)
random.seed(seed)

Ukeys = [''.join(random.choice(string.ascii_uppercase + string.digits) for _ in range(10)) for _ in range(u)]
for n in np.arange(2, u - 1, u / sizeCorpus)[:sizeCorpus]:
  keys = np.random.choice(Ukeys, n, replace=False)
  values = np.random.randint(1, 50, size=n)
  with open(output + str(n) + '.csv', 'w')as f:
    for i in range(n):
      f.write(keys[i] + ',' + str(values[i]) + '\n')
      
with open(output + 'params.log', 'w') as f:
  f.write('u = ' + str(u) + '\n')
  f.write('datasetID = ' + str(datasetId) + '\n')
  f.write('sizeCorpus = ' + str(sizeCorpus) + '\n')
  f.write('seed = ' + str(seed) + '\n')
