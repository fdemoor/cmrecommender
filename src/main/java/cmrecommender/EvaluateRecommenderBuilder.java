package cmrecommender;

import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.io.File;

public class EvaluateRecommenderBuilder implements RecommenderBuilder {
    
  UserSimilarity userSimilarity;
  int k;
  
  EvaluateRecommenderBuilder(UserSimilarity sim, int nk) {
    userSimilarity = sim;
    k = nk;
  }
  
  @Override
  public Recommender buildRecommender(DataModel dataModel) throws TasteException {
    UserNeighborhood neighborhood = new NearestNUserNeighborhood(k, userSimilarity, dataModel);
    return new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
  }
  
}
