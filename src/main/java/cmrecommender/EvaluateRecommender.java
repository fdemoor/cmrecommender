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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluateRecommender {
  
  private static final Logger log = LoggerFactory.getLogger(EvaluateRecommender.class);
  
  private static UserSimilarity getUserSimilarity(DataModel dataModel, Parameters params) throws TasteException {
    UserSimilarity userSimilarity = null;
    if (params.useCM()) {
      CountMinSketchConfig sketchConfig = new CountMinSketchConfig(params.getGamma(), params.getError(), params.getDepth());
      sketchConfig.configure(dataModel);
      double epsilon = sketchConfig.getEpsilon();
      double delta = sketchConfig.getDelta();
      userSimilarity = new CosineCM(dataModel, epsilon, delta);
    } else {
      userSimilarity = new UncenteredCosineSimilarity(dataModel);
    }
    return userSimilarity;
  }
  
  private static void runKEval(DataModel dataModel, Parameters params) throws TasteException {
    UserSimilarity userSimilarity = getUserSimilarity(dataModel, params);
    RecommenderEvaluator evaluator = new RMSRecommenderEvaluatorKFold();
    int[] kValues = {1, 2, 5, 8, 10, 12, 15, 20, 30, 50, 75, 100};
    for (int k : kValues) {
      RecommenderBuilder builder = new EvaluateRecommenderBuilder(userSimilarity, k);
      double result = evaluator.evaluate(builder, null, dataModel, params.getNbFolds(), 1.0);
      Logger logRMSE = LoggerFactory.getLogger("RMSE");
      logRMSE.info("{},{}", result, k);
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
