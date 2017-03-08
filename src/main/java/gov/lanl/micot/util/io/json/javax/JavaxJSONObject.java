package gov.lanl.micot.util.io.json.javax;

import java.util.Collection;
import javax.json.JsonObject;
import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;

/**
 * Thin wrapper of the javax JSON objects in case they never become part of the 
 * main distribution of java
 * @author Russell Bent
 *
 */
public class JavaxJSONObject implements JSONObject {
  private JsonObject object;

  /**
   * Constructor
   * @param object
   */
  public JavaxJSONObject(JsonObject object) {
    this.object = object;
  }

  @Override
  public double getDouble(String key) {
    return object.getJsonNumber(key).doubleValue();
  }

  @Override
  public int getInt(String key) {
    return object.getJsonNumber(key).intValue();
  }

  @Override
  public String getString(String key) {
    try {
      return object.getString(key);
    }
    catch (ClassCastException e) {      
    }
    
    try {
      return object.getBoolean(key)+"";
    }
    catch (ClassCastException e) {      
    }
    
    try {
      return object.getJsonNumber(key).doubleValue()+"";
    }
    catch (ClassCastException e) {      
    }

    return object.getString(key); // just to throw the exception
  }

  @Override
  public JSONObject getObject(String key) {
    return new JavaxJSONObject(object.getJsonObject(key));
  }
 
  /**
   * Get the JSON object
   * @return
   */
  protected JsonObject getJsonObject() {
    return object;
  }

  @Override
  public Collection<String> getKeys() {
    return object.keySet();
  }

  @Override
  public JSONArray getArray(String key) {
    return new JavaxJSONArray(object.getJsonArray(key));
  }

  @Override
  public boolean containsKey(String key) {
    return object.containsKey(key);
  }
  
  @Override
  public String toString() {
    return object.toString();
  }

  @Override
  public boolean getBoolean(String key) {
    return object.getBoolean(key);
  }  
}
