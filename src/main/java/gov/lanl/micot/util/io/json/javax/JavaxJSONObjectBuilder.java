package gov.lanl.micot.util.io.json.javax;

import javax.json.JsonObjectBuilder;

import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObject;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;

/**
 * Thin wrapper of JSON object builders
 * @author 210117
 *
 */
public class JavaxJSONObjectBuilder implements JSONObjectBuilder {
  private JsonObjectBuilder builder = null;
  
  /**
   * Constructor
   * @param builder
   */
  protected JavaxJSONObjectBuilder(JsonObjectBuilder builder) {
    this.builder = builder;
  }

  @Override
  public JSONObject build() {
    return new JavaxJSONObject(builder.build());
  }

  @Override
  public JSONObjectBuilder add(String key, boolean value) {
    return new JavaxJSONObjectBuilder(builder.add(key, value));
  }

  @Override
  public JSONObjectBuilder add(String key, int value) {
    return new JavaxJSONObjectBuilder(builder.add(key, value));
  }

  @Override
  public JSONObjectBuilder add(String key, double value) {
    return new JavaxJSONObjectBuilder(builder.add(key, value));
  }

  @Override
  public JSONObjectBuilder add(String key, String value) {
    return new JavaxJSONObjectBuilder(builder.add(key, value));
  }

  /**
   * Get the builder
   * @return
   */
  protected JsonObjectBuilder getObjectBuilder() {
    return builder;
  }

  @Override
  public JSONObjectBuilder add(String key, JSONArrayBuilder value) {
    return new JavaxJSONObjectBuilder(builder.add(key, ((JavaxJSONArrayBuilder)value).getArrayBuilder()));
  }

  @Override
  public JSONObjectBuilder add(String key, JSONObjectBuilder value) {
    return new JavaxJSONObjectBuilder(builder.add(key, ((JavaxJSONObjectBuilder)value).getObjectBuilder()));
  }

  @Override
  public JSONObjectBuilder add(String key, Object object) {
    if (object instanceof Integer || object instanceof Long) {
      return add(key, ((Number)object).intValue());
    }
    if (object instanceof Number) {
      return add(key, ((Number)object).doubleValue());
    }
    if (object instanceof Boolean) {
      return add(key, ((Boolean)object).booleanValue());
    }
    if (object instanceof JSONObjectBuilder) {
      return add(key, ((JSONObjectBuilder)object));
    }
    if (object instanceof JSONArrayBuilder) {
      return add(key, ((JSONArrayBuilder)object));
    }
    return add(key, object.toString());
  }
  
}
