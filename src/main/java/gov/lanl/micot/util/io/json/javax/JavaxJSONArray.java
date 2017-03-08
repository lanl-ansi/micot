package gov.lanl.micot.util.io.json.javax;

import javax.json.JsonArray;

import gov.lanl.micot.util.io.json.JSONArray;
import gov.lanl.micot.util.io.json.JSONObject;

/**
 * Thin wrapper of the javax JSON arrays in case they never become part of the 
 * main distribution of java
 * @author Russell Bent
 *
 */
public class JavaxJSONArray implements JSONArray {
  private JsonArray array;

  /**
   * Constructor
   * @param object
   */
  public JavaxJSONArray(JsonArray array) {
    this.array = array;
  }

  @Override
  public int size() {
    if (array == null) {
      return 0;
    }
    return array.size();
  }

  @Override
  public JSONObject getObject(int i) {
    return new JavaxJSONObject(array.getJsonObject(i));
  }
  
  @Override
  public String toString() {
    return array.toString();
  }

  @Override
  public double getDouble(int i) {
    return array.getJsonNumber(i).doubleValue();
  }

  @Override
  public String getString(int i) {
    return array.getString(i);
  }

  @Override
  public int getInt(int i) {
    return array.getJsonNumber(i).intValue();
  }

  @Override
  public boolean getBoolean(int i) {
    return array.getBoolean(i);
  }
  
  @Override
  public JSONArray getArray(int i) {
    return new JavaxJSONArray(array.getJsonArray(i));
  }
 }
