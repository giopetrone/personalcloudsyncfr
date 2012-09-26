package aa.vv.cc.client;

import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Date;

public class Info {
	
	 public static void createInfo(HashMap <String, String> args) {
		    Entity info = new Entity("Info");
		  	for (String k : args.keySet()){		  		
		  	  info.setProperty(k, args.get(k));
		  	} 
		  	Date pubDate = new Date();
		  	info.setProperty("pubDate", pubDate);

		  	Util.persistEntity(info);
		  }
	 
	 public static void createOrUpdateInfo(String name, String description) {
		    Entity product = getInfo(name);
		  	if (product == null) {
		  	  product = new Entity("Info", name);
		  	  product.setProperty("description", description);
		  	} else {
		  	  product.setProperty("description", description);
		  	}
		  	Util.persistEntity(product);
		  }

		  /**
		   * Retrun all the products
		   * @param kind : of kind product
		   * @return  products
		   */
		  public static Iterable<Entity> getAllInfos(String kind) {
		    return Util.listEntities(kind, null, null);
		  }

		  /**
		   * Get product entity
		   * @param name : name of the product
		   * @return: product entity
		   */
		  public static Entity getInfo(String name) {
		  	Key key = KeyFactory.createKey("Info",name);
		  	return Util.findEntity(key);
		  }

		  /**
		   * Get all items for a product
		   * @param name: name of the product
		   * @return list of items
		   */
		  
		  public static List<Entity> getItems(String name) {
		  	Query query = new Query();
		  	Key parentKey = KeyFactory.createKey("Info", name);
		  	query.setAncestor(parentKey);
		  	query.addFilter(Entity.KEY_RESERVED_PROPERTY, Query.FilterOperator.GREATER_THAN, parentKey);
		  		List<Entity> results = Util.getDatastoreServiceInstance()
		  				.prepare(query).asList(FetchOptions.Builder.withDefaults());
		  		return results;
			}
		  
		  /**
		   * Delete product entity
		   * @param productKey: product to be deleted
		   * @return status string
		   */
		  public static String deleteInfo(String productKey)
		  {
			  Key key = KeyFactory.createKey("Info",productKey);	   
			  
			  List<Entity> items = getItems(productKey);	  
			  if (!items.isEmpty()){
			      return "Cannot delete, as there are items associated with this product.";	      
			    }	    
			  Util.deleteEntity(key);
			  return "Info deleted successfully";
			  
		  }
}
