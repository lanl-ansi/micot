package gov.lanl.micot.util.io.json;

import java.util.Collection;


/**
 * Simple interface for JSON objects
 * @author Russell Bent
 */
public interface JSONObject {

  /**
   * Get the double associated with an object
   * @param key
   * @return
   */
  public double getDouble(String key);
  
  /**
   * Get the integer associated with an object
   * @param key
   * @return
   */
  public int getInt(String key);
  
  /**
   * Get the string associated with an object
   * @param key
   * @return
   */
  public String getString(String key);

  /**
   * Get the boolean associated with an object
   * @param key
   * @return
   */
  public boolean getBoolean(String key);
  
  /**
   * Gets the JSON object associated with a key
   * @param key
   * @return
   */
  public JSONObject getObject(String key);

  /**
   * Get all the keys in this object
   * @return
   */
  public Collection<String> getKeys();

  /**
   * Get a json array from the object
   * @param key
   * @return
   */
  public JSONArray getArray(String key);

  /**
   * Does the object contain a key
   * @param key
   * @return
   */
  public boolean containsKey(String key);
  
}
