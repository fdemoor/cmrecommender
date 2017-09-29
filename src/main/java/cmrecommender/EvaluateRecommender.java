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
      
      CountMinSketch cm1 = new CountMinSketch(0.1, 0.3);
      CountMinSketch cm2 = new CountMinSketch(0.1, 0.3);
      cm1.update(1, 3.5);
      cm1.update(2, 4.2);
      cm1.update(7, 2.7);
      cm1.update(4, 3.1);
      cm2.update(1, 5.3);
      cm2.update(2, 9.8);
      cm2.update(5, 6.5);
      cm2.update(8, 8.2);
      log.info("{}", cm1);
      log.info("{}", cm2);
      log.info("Cosine 1-1 is {}, 2-2 is {}, 1-2 is {}, 2-1 is {}", 
        CountMinSketch.cosine(cm1, cm1), CountMinSketch.cosine(cm2, cm2), 
        CountMinSketch.cosine(cm1, cm2), CountMinSketch.cosine(cm2, cm1));
      
      //DataModel dataModel = new FileDataModel(new File(params.getDataset()));
      //RecommenderEvaluator evaluator = new RMSRecommenderEvaluatorKFold();
      //ItemSimilarity itemSimilarity = new UncenteredCosineSimilarity(dataModel);
      //RecommenderBuilder builder = new EvaluateRecommenderBuilder(itemSimilarity);
      //double result = evaluator.evaluate(builder, null, dataModel, params.getNbFolds(), 1.0);
      
    } catch (Exception ex) { log.error("{}", ex.getMessage()); }
    
  }
  
}
