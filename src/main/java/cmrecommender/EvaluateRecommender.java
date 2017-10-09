package cmrecommender;

import cmrecommender.EvaluateRecommenderBuilder;
import cmrecommender.util.Parameters;

import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluatorKFold;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.CosineCM;

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
      
      //ItemSimilarity itemSimilarity = new UncenteredCosineSimilarity(dataModel);
      ItemSimilarity itemSimilarity = new CosineCM(dataModel, 0.1, 0.3);
      
      RecommenderBuilder builder = new EvaluateRecommenderBuilder(itemSimilarity);
      double result = evaluator.evaluate(builder, null, dataModel, params.getNbFolds(), 1.0);
      
      log.info("{},{},{}", 0.1, 0.3, result);
      
    } catch (Exception ex) { log.error("{}", ex.getMessage()); }
    
  }
  
}
