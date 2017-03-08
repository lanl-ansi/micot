package gov.lanl.micot.util.io.json;

/**
 * Simple interface for JSON writers
 * @author 210117
 *
 */
public interface JSONWriter {

  /**
   * Close the writer
   */
  public void close();

  /**
   * Write the JSON object
   * @param build
   */
  public void write(JSONObject build);

}
