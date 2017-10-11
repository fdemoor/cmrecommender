package cmrecommender;

import cmrecommender.EvaluateRecommenderBuilder;
import cmrecommender.util.Parameters;

import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluatorKFold;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CosineCM;
import org.apache.mahout.cf.taste.impl.common.CountMinSketchConfig;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluateRecommender {
  
  private static final Logger log = LoggerFactory.getLogger(EvaluateRecommender.class);
  
  public static void main(String[] args) {
    
    try {
      
      Parameters params = new Parameters(args);
      
      DataModel dataModel = new FileDataModel(new File(params.getDataset()));
      RecommenderEvaluator evaluator = new RMSRecommenderEvaluatorKFold();
      
      //UserSimilarity userSimilarity = new UncenteredCosineSimilarity(dataModel);
      
      double gamma = 0.2;
      double error = 0.05;
      int k = 10;
      
      CountMinSketchConfig sketchConfig = new CountMinSketchConfig(gamma, error);
      sketchConfig.configure(dataModel);
      double epsilon = sketchConfig.getEpsilon();
      double delta = sketchConfig.getDelta();
      //UserSimilarity userSimilarity = new CosineCM(dataModel, epsilon, delta);
      
      //RecommenderBuilder builder = new EvaluateRecommenderBuilder(userSimilarity, k);
      //double result = evaluator.evaluate(builder, null, dataModel, params.getNbFolds(), 1.0);
      
      //Logger logRMSE = LoggerFactory.getLogger("RMSE");
      //logRMSE.info("{},{},{},{},{},{}", result, epsilon, delta, gamma, error, k);
      
    } catch (Exception ex) { log.error("{}", ex.getMessage()); }
    
  }
  
}
