package gov.lanl.micot.util.io.json;

/**
 * Wrapper class for JSONReaderes
 * @author Russell Bent
 */
public interface JSONReader {
  
  /**
   * Read the main object from the stream
   * @return
   */
  public JSONObject readObject();

}
