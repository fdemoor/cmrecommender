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

import org.apache.mahout.cf.taste.impl.common.DoubleCountMinSketch;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluateRecommender {
  
  private static final Logger log = LoggerFactory.getLogger(EvaluateRecommender.class);
  
  public static void main(String[] args) {
    
    try {
      
      Parameters params = new Parameters(args);
      
      DoubleCountMinSketch cm1 = new DoubleCountMinSketch(0.1, 0.3);
      DoubleCountMinSketch cm2 = new DoubleCountMinSketch(0.1, 0.3);
      for (int i = 0; i < 6; i++) {
        cm1.update(i, i + 3);
        cm2.update(i, 2 * (i + 1) - 2);
      }
      
      log.info("CM1: {}, CM2: {}, cosine: {}", cm1, cm2, DoubleCountMinSketch.cosine(cm1, cm2));
      
      //DataModel dataModel = new FileDataModel(new File(params.getDataset()));
      //RecommenderEvaluator evaluator = new RMSRecommenderEvaluatorKFold();
      
      //ItemSimilarity itemSimilarity = new UncenteredCosineSimilarity(dataModel);
      ////ItemSimilarity itemSimilarity = new CosineCM(dataModel);
      
      //RecommenderBuilder builder = new EvaluateRecommenderBuilder(itemSimilarity);
      //double result = evaluator.evaluate(builder, null, dataModel, params.getNbFolds(), 1.0);
      
    } catch (Exception ex) { log.error("{}", ex.getMessage()); }
    
  }
  
}
