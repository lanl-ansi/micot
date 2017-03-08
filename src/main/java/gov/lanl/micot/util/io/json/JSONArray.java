package gov.lanl.micot.util.io.json;

/**
 * Simple interface for JSON arrays
 * @author Russell Bent
 */
public interface JSONArray {

  /**
   * Get the size of the array
   * @return
   */
  public int size();

  /**
   * Get the JSON Object at index i
   * @param i
   * @return
   */
  public JSONObject getObject(int i);

  /**
   * Get a double value
   * @param i
   * @return
   */
  public double getDouble(int i);
  
  /**
   * Get a string value
   * @param i
   * @return
   */
  public String getString(int i);
  
  /**
   * Get an integer value
   * @param i
   * @return
   */
  public int getInt(int i);
  
  /**
   * Get a boolean value
   * @param i
   * @return
   */
  public boolean getBoolean(int i);

  /**
   * Get an array at a certain index
   * @param i
   * @return
   */
  public JSONArray getArray(int i);
}
