package aa.vv.cc.client;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndContentImpl;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndEntryImpl;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndFeedImpl;
import com.sun.syndication.feed.synd.SyndLinkImpl;
import com.sun.syndication.io.SyndFeedOutput;

    
/**
 * This is the utility class for all servlets. It provides method for inserting,
 * deleting, searching the entity from data store. Also contains method for
 * displaying the entity in JSON format.
 * 
 */
public class Util {

  private static final Logger logger = Logger.getLogger(Util.class.getCanonicalName());
  private static DatastoreService datastore =  DatastoreServiceFactory.getDatastoreService(); 

/**
 * 
 * @param entity  : entity to be persisted
 */
  public static void persistEntity(Entity entity) {
  	logger.log(Level.INFO, "Saving entity");
  	datastore.put(entity);  	
  }

	/**
	 * Delete the entity from persistent store represented by the key
	 * @param key : key to delete the entity from the persistent store
	 */
  public static void deleteEntity(Key key) {
    logger.log(Level.INFO, "Deleting entity");
    datastore.delete(key);  	
  }
  
  /**
   * Delete list of entities given their keys
   * @param keys
   */
  public static void deleteEntity(final List<Key> keys){
    datastore.delete(new Iterable<Key>() {
		public Iterator<Key> iterator() {
		  return keys.iterator();
		}
      });    
  }

	/**
	 * Search and return the entity from datastore.
	 * @param key : key to find the entity
	 * @return  entity
	 */
 
  public static Entity findEntity(Key key) {
  	logger.log(Level.INFO, "Search the entity");
  	try {	  
  	  return datastore.get(key);
  	} catch (EntityNotFoundException e) {
  	  return null;
  	}
  }
 

	/***
	 * Search entities based on search criteria
	 * @param kind
	 * @param searchBy
	 *            : Searching Criteria (Property)
	 * @param searchFor
	 *            : Searching Value (Property Value)
	 * @return List all entities of a kind from the cache or datastore (if not
	 *         in cache) with the specified properties
	 */
  public static Iterable<Entity> listEntities(String kind, String searchBy,
			String searchFor) {
  	logger.log(Level.INFO, "Search entities based on search criteria");
  	Query q = new Query(kind);
  	if (searchFor != null && !"".equals(searchFor)) {
  	  q.addFilter(searchBy, FilterOperator.EQUAL, searchFor);
  	}
  	PreparedQuery pq = datastore.prepare(q);
  	return pq.asIterable();
  }
  
  
  /**
   * Search entities based on ancestor
   * @param kind
   * @param ancestor
   * @return
   */
  public static Iterable<Entity> listChildren(String kind, Key ancestor) {
  	logger.log(Level.INFO, "Search entities based on parent");
  	Query q = new Query(kind);
  	q.setAncestor(ancestor);
  	q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, ancestor);
  	PreparedQuery pq = datastore.prepare(q);
  	return pq.asIterable();
  }
  
  /**
   * 
   * @param kind
   * @param ancestor
   * @return
   */
  public static Iterable<Entity> listChildKeys(String kind, Key ancestor) {
  	logger.log(Level.INFO, "Search entities based on parent");
  	Query q = new Query(kind);
  	q.setAncestor(ancestor).setKeysOnly();
  	q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, ancestor);
  	PreparedQuery pq = datastore.prepare(q);
  	return pq.asIterable();
  }

	/**
	 * List the entities in JSON format
	 * 
	 * @param entities  entities to return as JSON strings
	 */
  public static String writeJSON(Iterable<Entity> entities) {
    logger.log(Level.INFO, "creating JSON format object");
  	StringBuilder sb = new StringBuilder();
  	
  	int i = 0;
  	sb.append("{\"data\": [");
  	for (Entity result : entities) {
  	  Map<String, Object> properties = result.getProperties();
  	  sb.append("{");
  	  if (result.getKey().getName() == null)
  		sb.append("\"name\" : \"" + result.getKey().getId() + "\",");
  	  else
  		sb.append("\"name\" : \"" + result.getKey().getName() + "\",");
  
  	  for (String key : properties.keySet()) {
  		sb.append("\"" + key + "\" : \"" + properties.get(key) + "\",");
  	  }
  	  sb.deleteCharAt(sb.lastIndexOf(","));
  	  sb.append("},");
  	  i++;
  	}
  	if (i > 0) {
  	  sb.deleteCharAt(sb.lastIndexOf(","));
  	}  
  	sb.append("]}");
  	return sb.toString();
  }
  
  /**
	 * Retrieves Parent and Child entities into JSON String
	 * 
	 * @param entities
	 *            : List of parent entities
	 * @param childKind
	 *            : Entity type for Child
	 * @param fkName
	 *            : foreign-key to the parent in the child entity
	 * @return JSON string
	 */
  public static String writeJSON(Iterable<Entity> entities, String childKind, String fkName) {
  	logger.log(Level.INFO, "creating JSON format object for parent child relation");
  	StringBuilder sb = new StringBuilder();
  	int i = 0;
  	sb.append("{\"data\": [");
  	for (Entity result : entities) {
  	  Map<String, Object> properties = result.getProperties();
  	  sb.append("{");
  	  if (result.getKey().getName() == null)
  		sb.append("\"name\" : \"" + result.getKey().getId() + "\",");
  	  else
  		sb.append("\"name\" : \"" + result.getKey().getName() + "\",");
  	  for (String key : properties.keySet()) {
  		sb.append("\"" + key + "\" : \"" + properties.get(key) + "\",");
  	  }
  	  Iterable<Entity> child = listEntities(childKind, fkName,
  	  String.valueOf(result.getKey().getId()));
  	  for (Entity en : child) {
  		for (String key : en.getProperties().keySet()) {
  		  sb.append("\"" + key + "\" : \"" + en.getProperties().get(key)+ "\",");
  		}
  	  }
  	  sb.deleteCharAt(sb.lastIndexOf(","));
  	  sb.append("},");
  	  i++;
  	}
  	if (i > 0) {
  	  sb.deleteCharAt(sb.lastIndexOf(","));
  	}  
  	sb.append("]}");
  	return sb.toString();
  }
 
  
	/**
	 * Utility method to send the error back to UI
	 * @param data
	 * @param resp
	 * @throws IOException 
	 */
  public static String getErrorMessage(Exception ex) throws IOException{
    return "Error:"+ex.toString();
  }
  
  public static void main(String[] args){
	  System.out.println(new Util().fromDataStoreToFeed("fff"));
  }
  
  public Writer  fromDataStoreToFeed(String pippo){
	  String ret ="";
	  String feedType = "atom_1.0";
	  SyndFeed feed = new SyndFeedImpl();
	 
      feed.setFeedType(feedType);
      
      feed.setTitle("Sample Feed (created with ROME)");
      feed.setLink("http://rome.dev.java.net");
      List links = feed.getLinks();
      SyndLinkImpl s = new SyndLinkImpl();
      s.setHref("http://pubsubhubbub.appspot.com");
      s.setRel("hub");
      links.add(s); feed.setLinks(links); 
      feed.setLinks(links);
      feed.setDescription("This feed has been created using ROME (Java syndication utilities");
      feed.setEntries(createEntryList());

      Writer writer = new StringWriter();
      SyndFeedOutput output = new SyndFeedOutput();
      try {
      output.output(feed, writer);
     // ret = writer.toString();
      writer.close();
      } catch (Exception ex){ex.printStackTrace();}
	  return writer;//ret;
  }
  
  public String  fromDataStoreToFeedOLD(String pippo){
	  String ret ="";
	  String feedType = "atom_1.0";
	  List entries = new ArrayList();
	  SyndFeed feed = new SyndFeedImpl();

      feed.setFeedType(feedType);

      feed.setTitle("Sample Feed (created with ROME)");
      feed.setLink("http://rome.dev.java.net");
      feed.setDescription("This feed has been created using ROME (Java syndication utilities");
      
      SyndEntry entry;
      SyndContent description;

      entry = new SyndEntryImpl();
      entry.setTitle("ROME v1.0");
      entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome01");
      entry.setPublishedDate(new Date());
      description = new SyndContentImpl();
      description.setType("text/plain");
      description.setValue("Initial release of ROME");
      entry.setDescription(description);
      entries.add(entry);
      feed.setEntries(entries);

      Writer writer = new StringWriter();
      SyndFeedOutput output = new SyndFeedOutput();
      try {
      output.output(feed, writer);
      ret = writer.toString();
      writer.close();
      } catch (Exception ex){ex.printStackTrace();}
	  return ret;
  }
  
  private List<SyndEntry> createEntryList(){
	  Iterable<Entity> entities = Info.getAllInfos("Info");
	  List entries = new ArrayList();
	  for (Entity result : entities) {
	  	  Map<String, Object> properties = result.getProperties();
	  	  SyndEntry entry;
	      SyndContent description;
	      entry = new SyndEntryImpl();
	      entry.setTitle((String)properties.get("title"));	     
	      entry.setPublishedDate((Date)properties.get("pubDate"));
	      entry.setLink("http://wiki.java.net/bin/view/Javawsxml/Rome01");
	      description = new SyndContentImpl();
	      description.setType("text/plain");
	      description.setValue((String)properties.get("content"));
	      entry.setDescription(description);
	      entries.add(entry);
	  }
	  return entries;
	  
  }
 
  /**
   * get DatastoreService instance
   * @return DatastoreService instance
   */
  public static DatastoreService getDatastoreServiceInstance(){
	  return datastore;
  }

}