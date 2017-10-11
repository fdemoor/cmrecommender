package cmrecommender;

import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluateRecommenderBuilder implements RecommenderBuilder {
  
  private static final Logger log = LoggerFactory.getLogger(EvaluateRecommenderBuilder.class);
    
  private final UserSimilarity userSimilarity;
  private final int k;
  
  EvaluateRecommenderBuilder(UserSimilarity sim, int sizeNeighborhood) {
    userSimilarity = sim;
    k = sizeNeighborhood;
  }
  
  @Override
  public Recommender buildRecommender(DataModel dataModel) throws TasteException {
    UserNeighborhood neighborhood = new NearestNUserNeighborhood(k, userSimilarity, dataModel);
    return new GenericUserBasedRecommender(dataModel, neighborhood, userSimilarity);
  }
  
}
