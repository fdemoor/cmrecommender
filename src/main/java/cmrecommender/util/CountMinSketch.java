package cmrecommender.util;

import cmrecommender.util.HashFunction;

import java.lang.Exception;
import java.lang.Math;

import gnu.trove.list.array.TDoubleArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class CountMinSketch {
  
  private static final Logger log = LoggerFactory.getLogger(CountMinSketch.class);
  
  public class CMException extends Exception {
    
    public CMException(String message) {
      super("CountMinSketch: " + message);
    }
    
  }
  
  private final int w;
  private final int d;
  private final TDoubleArrayList count;
  private final HashFunction[] hashFunctions;
  
  /** Setup a new count-min sketch with parameters delta, epsilon, and k
   * The parameters delta,epsilon and k control the accuracy of the estimates of the sketch
   * 
   * @param delta     A value in the unit interval that sets the precision of the sketch
   * @param epsilon   A value in the unit interval that sets the precision of the sketch
   * 
   * @throws  CountMinSketch.CMException  If delta or epsilon are not in the unit interval
   */
  public CountMinSketch(double delta, double epsilon, int k) throws CMException {
    
    if (delta <= 0 || delta >= 1) {
      throw new CMException("delta must be between 0 and 1, exclusive");
    }
    if (epsilon <= 0 || epsilon >= 1) {
      throw new CMException("epsilon must be between 0 and 1, exclusive");
    }
    
    w = (int) (Math.ceil( Math.exp(1) / epsilon ));
    d = (int) (Math.ceil( Math.log(1 / delta) ));

    if (d > 21) { log.error("d > 21 is not supported"); } // Not enough random parameters for hash functions
    
    log.debug("Creating count-min sketch with width {} and depth {}", w, d);
    
    hashFunctions = new HashFunction[d];
    for (int i = 0; i < d; i++) { hashFunctions[i] = new HashFunction(i, w); }
    
    count = new TDoubleArrayList(w * d);
    for (int i = 0; i < w * d; i++) { count.insert(i, 0); }
    
  }
  
  /** Updates the sketch for the item with name of key by the amount specified in increment
   * 
   * @param key         The item to update the value of in the sketch
   * @param increment   The amount to update the sketch by for the given key
   * 
   */
  public void update(long key, double increment) {
    for (int i = 0; i < d; i++) {
      int j = hashFunctions[i].hash(key);
      double value = count.get(j + i * w);
      count.set(j + i * w, value + increment);
      log.debug("Update value row {} column {}: previous value {}, new value {}", i, j, value, value + increment);
    }
  }
  
  /** Fetches the sketch estimate for the given key
   * 
   * @param key   The item to produce an estimate for
   * 
   * @return    The best estimate of the count for the given key based on the sketch
   * 
   * For an item i with count a_i, the estimate from the sketch a_i_hat will satisfy the relation
   *        a_hat_i <= a_i + epsilon * ||a||_1
   * with probability at least 1 - delta, where a is the the vector of
   * all counts and ||x||_1 is the L1 norm of a vector x
   * 
   */
  public double get(long key) {
    double estimate = Double.MAX_VALUE;
    for (int i = 0; i < d; i++) {
      int j = hashFunctions[i].hash(key);
      double value = count.get(j + i * w);
      if (value < estimate) { estimate = value; }
    }
    log.debug("Estimate value for key {} is {}", key, estimate);
    return estimate;
  }
  
  /** Returns a nice string representation of the count-min sketch content
   * 
   * @return matrix-like string representation of the sketch
   * 
   */
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append(System.lineSeparator());
    for (int i = 0; i < d; i++) {
      builder.append("| ");
      for (int j = 0; j < w; j++) {
        builder.append(count.get(j + i * w));
        builder.append(" | ");
      }
      builder.append(System.lineSeparator());
    }
    return builder.toString();
  }
  
  
  
  
  
}
