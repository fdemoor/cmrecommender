import math

MIN_DEPTH = 1
MAX_DEPTH = 25

  
def Fmeasure(w, d, n, u, q):
  """
  Function we want to maximize in our optimization problem
  """
  beta_ = 1 - pointErrorProba(w, d, n)
  #gamma = gammaDeniability(w, d, n, u)
  gamma = 1 - probaInserted(w, d, n, u)
  if beta_ == 0 or gamma == 0:
    return 0
  return (1 + q * q) * beta_ * gamma / (q * q * beta_ + gamma)
  

def gammaDeniability(w, d, n, u):
  """
  Returns the gamma-deniability value
  """
  p = 1 - math.pow(1 - 1 / float(w), n)
  return math.pow(1 - math.pow(1 - 1 / (float(w) * p), (float(u) - float(n)) * p), d)
  
  
def probaInserted(w, d, n, u):
  falseP = math.pow(1 - math.pow(1 - 1 / float(w), n), d)
  return n / float(n + falseP * (u - n))
  

def pointErrorProba(w, d, n):
  """
  Returns the probability for a point-query to NOT return the actual value
  """
  return math.pow(1 - math.pow(1 - 1 / float(w), n), d)


def getSize(n, u, q):
  """
  Returns the width and depth by solving the optimzation problem
  """
  bestWidth = 0
  bestDepth = 0
  bestMax = 0
  for d in range(MIN_DEPTH, MAX_DEPTH + 1):
    for w in range(d, n + 1):
      x = Fmeasure(w, d, n, u, q)
      if x > bestMax:
        bestWidth = w
        bestDepth = d
        bestMax = x
  return bestWidth, bestDepth
