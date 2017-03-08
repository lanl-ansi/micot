package gov.lanl.micot.util.io.json;

/**
 * Simple interface for building arrays for JSON
 * @author Russell Bent
 *
 */
public interface JSONArrayBuilder {

  /**
   * Add an object builder to the array builder
   * @param branchBuilder
   * @return
   */
  public JSONArrayBuilder add(JSONObjectBuilder branchBuilder);
  
  /**
   * Add a double
   * @param quadcoeff
   * @return
   */
  public JSONArrayBuilder add(double value);

  /**
   * Add a boolean
   * @param value
   * @return
   */
  public JSONArrayBuilder add(boolean value);

  /**
   * Add a boolean
   * @param value
   * @return
   */
  public JSONArrayBuilder add(String value);
  
  /**
   * Add a boolean
   * @param value
   * @return
   */
  public JSONArrayBuilder add(JSONArrayBuilder value);

}
