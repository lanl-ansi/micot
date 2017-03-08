package gov.lanl.micot.infrastructure.optimize;

import java.util.HashMap;

/**
 * A class for profiling the performance of the optimizer
 * @author Russell Bent
 *
 */
public class OptimizerStatistics extends HashMap<String, Object> {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   */
  public OptimizerStatistics() {
    super();
  }

  /**
   * Get a parameter as a double
   * @param key
   * @return
   */
  public double getDouble(String key) {
    return (Double)get(key);
  }
  
  /**
   * Get a parameter as an integer
   * @param key
   * @return
   */
  public int getInteger(String key) {
    return (Integer)get(key);
  }

  /**
   * get a parameter as a boolean
   * @param key
   * @return
   */
  public boolean getBoolean(String key) {
    return (Boolean)get(key);
  }
  
  /**
   * Get a parameter as a string
   * @param key
   * @return
   */
  public String getString(String key) {
    return (String)get(key);
  }
}
