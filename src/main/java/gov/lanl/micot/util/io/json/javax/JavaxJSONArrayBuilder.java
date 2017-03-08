package gov.lanl.micot.util.io.json.javax;

import javax.json.JsonArrayBuilder;

import gov.lanl.micot.util.io.json.JSONArrayBuilder;
import gov.lanl.micot.util.io.json.JSONObjectBuilder;

/**
 * Thin wrapper of JSON array builders
 * @author Russell Bent
 */
public class JavaxJSONArrayBuilder implements JSONArrayBuilder {
  protected JsonArrayBuilder builder = null;

  /**
   * Constructor
   * @param builder
   */
  protected JavaxJSONArrayBuilder(JsonArrayBuilder builder) {
    this.builder = builder;
  }

  @Override
  public JSONArrayBuilder add(JSONObjectBuilder branchBuilder) {
    return new JavaxJSONArrayBuilder(builder.add(((JavaxJSONObjectBuilder)branchBuilder).getObjectBuilder()));
  }

  /**
   * Get the array builder
   * @return
   */
  protected JsonArrayBuilder getArrayBuilder() {
    return builder;
  }

  @Override
  public JSONArrayBuilder add(double value) {
    return new JavaxJSONArrayBuilder(builder.add(value));
  }
  
  @Override
  public JSONArrayBuilder add(boolean value) {
    return new JavaxJSONArrayBuilder(builder.add(value));
  }

  @Override
  public JSONArrayBuilder add(String value) {
    return new JavaxJSONArrayBuilder(builder.add(value));
  }

  @Override
  public JSONArrayBuilder add(JSONArrayBuilder value) {
    return new JavaxJSONArrayBuilder(builder.add(((JavaxJSONArrayBuilder)value).getArrayBuilder()));
  }


}
