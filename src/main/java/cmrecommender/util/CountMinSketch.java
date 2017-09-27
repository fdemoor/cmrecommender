package cmrecommender.util;

import java.lang.Exception;
import java.lang.Math;

import gnu.trove.list.array.TDoubleArrayList;

public class CountMinSketch {
  
  public class CMException extends Exception {
    
    public CMException(String message) {
      super("CountMinSketch: " + message);
    }
    
  }
  
  private final int w;
  private final int d;
  private final int k_;
  private final TDoubleArrayList count;
  
  CountMinSketch(double delta, double epsilon, int k) throws CMException {
    
    if (delta <= 0 || delta >= 1) {
      throw new CMException("delta must be between 0 and 1, exclusive");
    }
    if (epsilon <= 0 || epsilon >= 1) {
      throw new CMException("epsilon must be between 0 and 1, exclusive");
    }
    if (k < 1) {
      throw new CMException("k must be a positive integer");
    }
    
    w = (int) (Math.ceil( Math.exp(1) / epsilon ));
    d = (int) (Math.ceil( Math.log(1 / delta) ));
    k_ = k;
    
    count = new TDoubleArrayList(w * d, 0);
    
  }
  
  
  
  
  
}
