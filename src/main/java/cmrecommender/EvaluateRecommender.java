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
      CountMinSketchConfig sketchConfig = new CountMinSketchConfig(params.getQ());
      sketchConfig.configure(dataModel, params.getDataset().replace("/", "-"));
      if (params.runDistEvaluation()) { runDistEval(sketchConfig, dataModel, params); }
      userSimilarity = new CosineCM(dataModel, sketchConfig, hfBuilder);
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
   * EVALUATION OF SEVERAL DISTRIBUTIONS IN THE DATASET
   ****************************************************************************/
  
  /** Run the evaluation of several distributions in the dataset */
  private static void runDistEval(CountMinSketchConfig sketchConfig, DataModel dataModel, Parameters params) throws TasteException {
    log.info("Start logging of distributions");
    Logger logDIST = LoggerFactory.getLogger("DIST");
    int u = dataModel.getNumItems();
    LongPrimitiveIterator it = dataModel.getUserIDs();
    while (it.hasNext()) {
      long userID = it.next();
      int n = dataModel.getPreferencesFromUser(userID).length();
			double epsilon = sketchConfig.getEpsilon(userID);
			double delta = sketchConfig.getDelta(userID);
			int w = (int) Math.ceil(Math.exp(1) / epsilon);
			int d = (int) Math.ceil(Math.log(1 / delta));
			double beta = CountMinSketchConfig.probaNotExactRetrieve(w, d, n);
			double p = CountMinSketchConfig.probaInserted(w, d, n, u);
			double fmeasure = CountMinSketchConfig.Fmeasure(w, d, n, u, params.getQ());
			logDIST.info("{},{},{},{}", n, beta, p, fmeasure);
		}
		log.info("End of evaluation of profile distribution");
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
      if (params.runKEvaluation()) { runKEval(dataModel, params, hfBuilder); }
      
    } catch (TasteException ex) {
      log.error("TasteException: {}", ex.getMessage());
    } catch (IOException ex) {
      log.error("IOException: {}", ex.getMessage());
    } catch (ParseException ex) {
        log.error("ParseException: {}", ex.getMessage());
    }
    
  }
  
}
