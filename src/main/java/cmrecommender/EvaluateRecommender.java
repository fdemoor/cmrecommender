package cmrecommender;

import cmrecommender.EvaluateRecommenderBuilder;
import cmrecommender.util.Parameters;

import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluatorKFold;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.similarity.CosineSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CosineCM;
import org.apache.mahout.cf.taste.impl.common.CountMinSketchConfig;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.common.HashFunctionBuilder;

import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Runnable;
import java.lang.Thread;
import java.lang.InterruptedException;
import java.lang.Math;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluateRecommender {
  
  private static final Logger log = LoggerFactory.getLogger(EvaluateRecommender.class);
  
  
  /** Returns the similarity based on the parameters asked
   *  
   *  @param    dataModel     dataset
   *  @param    params        parameters
   * 
   *  @return   user similarity
   */
  private static UserSimilarity getUserSimilarity(DataModel dataModel, Parameters params, HashFunctionBuilder hfBuilder) throws TasteException {
    UserSimilarity userSimilarity = null;
    if (params.useCM()) {
      CountMinSketchConfig sketchConfig = new CountMinSketchConfig(params.getGamma(), params.getError());
      sketchConfig.configure(dataModel, params.getDataset());
      double epsilon = sketchConfig.getEpsilon();
      double delta = sketchConfig.getDelta();
      userSimilarity = new CosineCM(dataModel, epsilon, delta, hfBuilder);
    } else {
      userSimilarity = new CosineSimilarity(dataModel);
    }
    return userSimilarity;
  }
  
  
  /*****************************************************************************
   * RMSE EVALUATION WITH DIFFERENT VALUES OF k
   ****************************************************************************/
  
  static class RunKEval implements Runnable {
    
    private final int kValue;
    private final UserSimilarity userSimilarity;
    private final Logger logRMSE;
    private final DataModel dataModel;
    private final RecommenderEvaluator evaluator;
    private final int nbFolds;
    
    RunKEval(int k, UserSimilarity sim, Logger logger, DataModel model, RecommenderEvaluator eval, int n) {
      kValue = k;
      userSimilarity = sim;
      logRMSE = logger;
      dataModel = model;
      evaluator = eval;
      nbFolds = n;
    }
    
    public void run() {
      try {
        RecommenderBuilder builder = new EvaluateRecommenderBuilder(userSimilarity, kValue);
        double result = evaluator.evaluate(builder, null, dataModel, nbFolds, 1.0);
        synchronized(logRMSE) {
          logRMSE.info("{},{}", result, kValue);
        }
      } catch (TasteException ex) {
        log.error("TasteException: {}", ex.getMessage());
      }
    }
    
  }
  
  /** Run RMSE evaluation for different values of k (as in kNN) */
  private static void runKEval(DataModel dataModel, Parameters params, HashFunctionBuilder hfBuilder) throws TasteException {
    log.info("Start RMSE evaluation with different k");
    UserSimilarity userSimilarity = getUserSimilarity(dataModel, params, hfBuilder);
    RecommenderEvaluator evaluator = new RMSRecommenderEvaluatorKFold();
    int[] kValues = {2, 5, 8, 10, 12, 15, 20, 30, 50, 75, 100};
    Logger logRMSE = LoggerFactory.getLogger("RMSE");
    int n = params.getNbFolds();
    for (int k : kValues) {
      log.info("Start evaluation for k={} in new thread", k);
      Thread t = new Thread(new RunKEval(k, userSimilarity, logRMSE, dataModel, evaluator, n));
      t.start();
      try {
        t.join();
      } catch (InterruptedException ex) {
        log.error("InterruptedException: {}", ex.getMessage());
      }
    }
    log.info("End of RMSE evaluation with different k");
  }
  
  
  /*****************************************************************************
   * EVALUATION OF PROFILE SIZE DISTRIBUTION IN THE DATASET
   ****************************************************************************/
  
  /** Run the evaluation of the profile size distribution in the dataset */
  private static void runProfileDistEval(DataModel dataModel) throws TasteException {
    log.info("Start evaluation of profile distribution");
    Logger logPDIST = LoggerFactory.getLogger("PDIST");
    LongPrimitiveIterator it = dataModel.getUserIDs();
    while (it.hasNext()) {
      long userID = it.next();
      int l = dataModel.getPreferencesFromUser(userID).length();
      logPDIST.info("{}", l);
    }
    log.info("End of evaluation of profile distribution");
  }
  
  
  /*****************************************************************************
   * EVALUATION OF THE ERROR BETWEEN COSINE AND COSINECM FOR DIFFERENT WIDTHS
   ****************************************************************************/
  
  static class RunErrorWidthEval implements Runnable {
    
    private final DataModel dataModel;
    private final double epsilon;
    private final double delta;
    private final int width;
    private final Logger logEW;
    private final UserSimilarity sim;
    private final HashFunctionBuilder hfBuilder;
    
    RunErrorWidthEval(DataModel model, int w, double del, Logger logger,
                      UserSimilarity cosSim, HashFunctionBuilder hfBuilder_) {
      dataModel = model;
      width = w;
      epsilon = Math.exp(1) / (double) width;
      delta = del;
      logEW = logger;
      sim = cosSim;
      hfBuilder = hfBuilder_;
    }
    
    public void run() {
      try {
        UserSimilarity simCM = new CosineCM(dataModel, epsilon, delta, hfBuilder);
        LongPrimitiveIterator it1 = dataModel.getUserIDs();
        while (it1.hasNext()) {
          long userID1 = it1.next();
          LongPrimitiveIterator it2 = dataModel.getUserIDs();
          while (it2.hasNext()) {
            long userID2 = it2.next();
            if ((userID1 + userID2) % 100 == 0) { // Consider only some couples
              double refValue = sim.userSimilarity(userID1, userID2);
              double value = simCM.userSimilarity(userID1, userID2);
              double error = Math.abs(refValue - value);
              synchronized(logEW) {
                logEW.info("{},{}", error, width);
              }
            }
          }
        }
      } catch (TasteException ex) {
        log.error("TasteException: {}", ex.getMessage());
      }
    }
  }
  
  /** Run the evaluation of the error bewteen cosine and cosineCM with different widths */
  private static void runErrorWidthEval(DataModel dataModel, Parameters params, HashFunctionBuilder hfBuilder) throws TasteException {
    log.info("Start evaluation error / width");
    Logger logEW = LoggerFactory.getLogger("EW");
    UserSimilarity sim = new CosineSimilarity(dataModel);
    double delta = Math.exp(- 11.0);
    int A = 10, B = 1000, H = 25;
    ArrayList<Thread> threads = new ArrayList<Thread>((B - A) / H + 1);
    for (int width = A; width < B; width += H) {
      log.info("Processing width {} in a new thread", width);
      Thread t = new Thread(new RunErrorWidthEval(dataModel, width, delta, logEW, sim, hfBuilder));
      threads.add(t);
      t.start();
    }
    for (Thread t : threads) { 
      try {
        t.join();
      } catch (InterruptedException ex) {
        log.error("InterruptedException: {}", ex.getMessage());
      }
    }
    log.info("End of evaluation error / width");
  }
  
  
  /*****************************************************************************
   * MAIN
   ****************************************************************************/
   
  public static void main(String[] args) {
    
    try {
      
      /* Retrieve and log the parameters */
      Parameters params = new Parameters(args);
      Logger logPARAMS = LoggerFactory.getLogger("PARAMS");
      logPARAMS.info("{}", params);
      
      HashFunctionBuilder hfBuilder = new HashFunctionBuilder(params.getSeed());
      
      /* Load the dataset */
      DataModel dataModel = new FileDataModel(new File(params.getDataset()));
      
      /* Run the different evaluations requested */
      if (params.runProfileDist()) { runProfileDistEval(dataModel); }
      if (params.runKEvaluation()) { runKEval(dataModel, params, hfBuilder); }
      if (params.runEWEvaluation()) { runErrorWidthEval(dataModel, params, hfBuilder); }
      
    } catch (TasteException ex) {
      log.error("TasteException: {}", ex.getMessage());
    } catch (IOException ex) {
      log.error("IOException: {}", ex.getMessage());
    } catch (ParseException ex) {
        log.error("ParseException: {}", ex.getMessage());
    }
    
  }
  
}
