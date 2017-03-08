package gov.lanl.micot.util.io.json;

public interface JSONObjectBuilder {

  /**
   * Build the JSON object
   * @return
   */
  public JSONObject build();

  /**
   * Add a boolean
   * @param key
   * @param value
   * @return
   */
  public JSONObjectBuilder add(String key, boolean value);

  /**
   * Add an int
   * @param key
   * @param value
   * @return
   */
  public JSONObjectBuilder add(String key, int value);

  /**
   * Add a double
   * @param key
   * @param value
   * @return
   */
  public JSONObjectBuilder add(String key, double value);

  /**
   * Add a string
   * @param key
   * @param value
   * @return
   */
  public JSONObjectBuilder add(String key, String value);

  /**
   * Add an array builder
   * @param branchTag
   * @param arrayBuilder
   * @return
   */
  public JSONObjectBuilder add(String key, JSONArrayBuilder arrayBuilder);

  /**
   * Add a builder to this object builder
   * @param key
   * @param builder
   * @return
   */
  public JSONObjectBuilder add(String key, JSONObjectBuilder builder);

  /**
   * Add a generic object to the builder
   * @param key
   * @param object
   * @return
   */
  public JSONObjectBuilder add(String key, Object object);
}
