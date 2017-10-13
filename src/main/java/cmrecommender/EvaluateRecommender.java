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
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CosineCM;
import org.apache.mahout.cf.taste.impl.common.CountMinSketchConfig;

import org.apache.commons.cli.ParseException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.lang.Runnable;
import java.lang.Thread;
import java.lang.InterruptedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluateRecommender {
  
  private static final Logger log = LoggerFactory.getLogger(EvaluateRecommender.class);
  
  private static UserSimilarity getUserSimilarity(DataModel dataModel, Parameters params) throws TasteException {
    UserSimilarity userSimilarity = null;
    if (params.useCM()) {
      CountMinSketchConfig sketchConfig = new CountMinSketchConfig(params.getGamma(), params.getError(), params.getDepth());
      sketchConfig.configure(dataModel, params.getDataset());
      double epsilon = sketchConfig.getEpsilon();
      double delta = sketchConfig.getDelta();
      userSimilarity = new CosineCM(dataModel, epsilon, delta);
    } else {
      userSimilarity = new UncenteredCosineSimilarity(dataModel);
    }
    return userSimilarity;
  }
  
  private static void runKEval(DataModel dataModel, Parameters params) throws TasteException {
    log.info("Start RMSE evaluation with different k");
    UserSimilarity userSimilarity = getUserSimilarity(dataModel, params);
    RecommenderEvaluator evaluator = new RMSRecommenderEvaluatorKFold();
    int[] kValues = {2, 5, 8, 10, 12, 15, 20, 30, 50, 75, 100};
    ArrayList<Thread> threads = new ArrayList<Thread>(12);
    Logger logRMSE = LoggerFactory.getLogger("RMSE");
    int n = params.getNbFolds();
    for (int k : kValues) {
      log.info("Start evaluation for k={} in new thread", k);
      Thread t = new Thread(new runKEval(k, userSimilarity, logRMSE, dataModel, evaluator, n));
      threads.add(t);
      t.start();
    //} // Not sure parallelism here is needed, evaluation is already multi-threaded
        // Can too many threads slow down the program? Lot of context switch
    //for (Thread t : threads) { 
      try {
        t.join();
      } catch (InterruptedException ex) {
        log.error("InterruptedException: {}", ex.getMessage());
      }
    }
    log.info("End of RMSE evaluation with different k");
  }
  
  static class runKEval implements Runnable {
    
    private final int kValue;
    private final UserSimilarity userSimilarity;
    private final Logger logRMSE;
    private final DataModel dataModel;
    private final RecommenderEvaluator evaluator;
    private final int nbFolds;
    
    runKEval(int k, UserSimilarity sim, Logger logger, DataModel model, RecommenderEvaluator eval, int n) {
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
  
  public static void main(String[] args) {
    
    try {
      
      Parameters params = new Parameters(args);
      Logger logPARAMS = LoggerFactory.getLogger("PARAMS");
      logPARAMS.info("{}", params);
      
      DataModel dataModel = new FileDataModel(new File(params.getDataset()));
      
      runKEval(dataModel, params);
      
    } catch (TasteException ex) {
      log.error("TasteException: {}", ex.getMessage());
    } catch (IOException ex) {
      log.error("IOException: {}", ex.getMessage());
    } catch (ParseException ex) {
        log.error("ParseException: {}", ex.getMessage());
    }
    
  }
  
}
