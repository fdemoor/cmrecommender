import numpy as np
import seaborn as sns
import matplotlib.pyplot as plt
import os

import opti

output = 'output_probaDen/'
os.system('mkdir -p ' + output)

### MAIN


sns.set(font_scale=2)
sns.set_style("whitegrid")

f, ax = plt.subplots(1, 1, figsize=(10, 6))

R = []

for u in [2000]:
  for n in range(2, u, 25):
    for d in range(1, 25, 7):
      for w in range(d, n, 100):
        x = gammaDeniability(w, d, n, u)
        y = probaInserted(w, d, n, u)
        R.append((x, y))
          
X = [e[0] for e in R]
Y = [e[1] for e in R]
im = ax.scatter(X, Y, marker='o', s=50, c=Y, cmap='RdYlGn_r')

ax.set_xlabel("Deniability")
ax.set_xlim([0, 1])
ax.set_ylabel("Inserted probability")
ax.set_ylim([0, 1])

f.tight_layout(h_pad=3)
f.savefig(output + 'probaDen.svg')
