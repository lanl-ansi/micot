package gov.lanl.micot.util.io.json.javax;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonWriterFactory;
import javax.json.stream.JsonGenerator;

import gov.lanl.micot.util.io.json.JSON;
import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;
import gov.lanl.micot.util.io.json.JSONReader;
import gov.lanl.micot.util.io.json.JSONWriter;

/**
 * Thin wrapper of the Javax JSON object
 * @author Russell Bent
 *
 */
public class JavaxJSON extends JSON {

  @Override
  public JSONReader createReader(InputStream stream) {
    return new JavaxJSONReader(Json.createReader(stream));
  }
  
  @Override
  public JSONWriter createWriter(OutputStream stream) {
    Map<String, Boolean> config = new HashMap<>();
    config.put(JsonGenerator.PRETTY_PRINTING, true);
    JsonWriterFactory jwf = Json.createWriterFactory(config);
//    return new JavaxJSONWriter(Json.createWriter(stream));
    return new JavaxJSONWriter(jwf.createWriter(stream));
  }


  @Override
  public JSONObjectBuilder createObjectBuilder() {
    return new JavaxJSONObjectBuilder(Json.createObjectBuilder());
  }

  @Override
  public JSONArrayBuilder createArrayBuilder() {
    return new JavaxJSONArrayBuilder(Json.createArrayBuilder());
  }

}
