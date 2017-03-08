package gov.lanl.micot.util.io.json.javax;

import javax.json.JsonReader;

import gov.lanl.micot.util.io.json.JSONObject;
import gov.lanl.micot.util.io.json.JSONReader;

/**
 * Thin wrapper of javax's readers of json files in case it doesn't go main stream
 * @author Russell Bent
 *
 */
public class JavaxJSONReader implements JSONReader {
  private JsonReader reader = null;

  /**
   * Constructor
   * @param reader
   */
  protected JavaxJSONReader(JsonReader reader) {
    this.reader = reader;
  }
  
  @Override
  public JSONObject readObject() {
    return new JavaxJSONObject(reader.readObject());
  }
  
  
}
