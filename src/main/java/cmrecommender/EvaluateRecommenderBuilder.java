package cmrecommender;

import org.apache.mahout.cf.taste.similarity.ItemSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.impl.recommender.GenericItemBasedRecommender;
import org.apache.mahout.cf.taste.recommender.Recommender;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EvaluateRecommenderBuilder implements RecommenderBuilder {
  
  private static final Logger log = LoggerFactory.getLogger(EvaluateRecommenderBuilder.class);
    
  private final ItemSimilarity itemSimilarity;
  
  EvaluateRecommenderBuilder(ItemSimilarity sim) {
    itemSimilarity = sim;
  }
  
  @Override
  public Recommender buildRecommender(DataModel dataModel) throws TasteException {
    return new GenericItemBasedRecommender(dataModel, itemSimilarity);
  }
  
}
