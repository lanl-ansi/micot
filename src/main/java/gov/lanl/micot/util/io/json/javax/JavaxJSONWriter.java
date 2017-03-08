package gov.lanl.micot.util.io.json.javax;

import javax.json.JsonWriter;

import gov.lanl.micot.util.io.json.JSONObject;
import gov.lanl.micot.util.io.json.JSONWriter;

/**
 * Thin wrapper of JSON object builders
 * @author 210117
 *
 */
public class JavaxJSONWriter implements JSONWriter {
  private JsonWriter writer = null;
  
  /**
   * Constructor
   * @param builder
   */
  protected JavaxJSONWriter(JsonWriter writer) {
    this.writer = writer;
  }

  @Override
  public void close() {
    writer.close();
  }

  @Override
  public void write(JSONObject build) {
    writer.writeObject(((JavaxJSONObject)build).getJsonObject());
  }
  
}
