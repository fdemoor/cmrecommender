package cmrecommender;

import cmrecommender.EvaluateRecommenderBuilder;
import cmrecommender.util.Parameters;

import cmrecommender.util.CountMinSketch;

import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluatorKFold;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluateRecommender {
  
  private static final Logger log = LoggerFactory.getLogger(EvaluateRecommender.class);
  
  public static void main(String[] args) {
    
    try {
      
      Parameters params = new Parameters(args);
      
      CountMinSketch cm = new CountMinSketch(0.1, 0.3, 10);
      cm.update(1, 3.5);
      cm.update(2, 4.2);
      cm.update(3, 2.7);
      cm.update(4, 3.1);
      cm.update(5, 5.3);
      cm.update(6, 9.8);
      cm.update(7, 6.5);
      log.info("{}", cm);
      
      //DataModel dataModel = new FileDataModel(new File(params.getDataset()));
      //RecommenderEvaluator evaluator = new RMSRecommenderEvaluatorKFold();
      //ItemSimilarity itemSimilarity = new UncenteredCosineSimilarity(dataModel);
      //RecommenderBuilder builder = new EvaluateRecommenderBuilder(itemSimilarity);
      //double result = evaluator.evaluate(builder, null, dataModel, params.getNbFolds(), 1.0);
      
    } catch (Exception ex) { log.error("{}", ex.getMessage()); }
    
  }
  
}
