package cmrecommender;

import cmrecommender.EvaluateRecommenderBuilder;

import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.eval.RMSRecommenderEvaluatorKFold;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;

import java.io.File;

public class EvaluateRecommender {
  
  public static void main(String[] args) {
    
    try {
      
      DataModel dataModel = new FileDataModel(new File("datasets/test.csv"));
      //RecommenderEvaluator evaluator = new RMSRecommenderEvaluatorKFold();
      RecommenderEvaluator evaluator = new AverageAbsoluteDifferenceRecommenderEvaluator();
      UserSimilarity userSimilarity = new UncenteredCosineSimilarity(dataModel);
      RecommenderBuilder builder = new EvaluateRecommenderBuilder(userSimilarity, 10);
      double result = evaluator.evaluate(builder, null, dataModel, 0.9, 1.0);
      
    } catch (Exception ex) { System.out.println(ex.getMessage()); }
    
  }
  
  
  
}
