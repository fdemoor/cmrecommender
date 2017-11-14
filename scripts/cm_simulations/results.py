class Results:

  def __init__(self, output, names, X, Y, Zs):
    
    self.output = output
    self.names = names
    self.X = X
    self.Y = Y
    self.Zs = Zs
    
    self.setX()
    self.setY()

  def setX(self, xlabel='', xticks=[], xticklabels=[]):
    self.xlabel = xlabel
    self.xticks = xticks
    self.xticklabels = xticklabels
    
  def setY(self, ylabel='', yticks=[], yticklabels=[]):
    self.ylabel = ylabel
    self.yticks = yticks
    self.yticklabels = yticklabels
