package gov.lanl.micot.infrastructure.project;

/**
 * Continues some useful constants for json configuration utilities
 * @author Russell Bent
 */
public class JsonConfigurationUtilities {

  protected static final String VERSION_TAG = "version";
  
  protected static final String APPLICATION_TAG = "application";
  protected static final String APPLICATION_FACTORY_TAG = "application_factory";

  protected static final String MODEL_TAG = "models";
  protected static final String MODEL_FILE_TAG = "model_file";
  protected static final String MODEL_FILE_FACTORY_TAG = "model_file_factory";

  public static final String MODIFICATIONS_TAG = "modifications";
  protected static final String MODIFICATION_COMPONENT_TAG = "component_type";
  protected static final String ID_TAG = "id";
  protected static final String MODIFICATION_IDENTIFIER_KEY_TAG = "id_key";
  protected static final String MODIFICATION_IDENTIFIER_VALUE_TAG = "id_value";
  protected static final String MODIFICATION_IDENTIFIER_TYPE_TAG = "id_type";
  protected static final String MODIFICATION_ATTRIBUTE_TAG = "modification";
  protected static final String MODIFICATION_ATTRIBUTE_KEY_TAG = "modification_key";
  protected static final String MODIFICATION_ATTRIBUTE_VALUE_TAG = "modification_value";
  protected static final String MODIFICATION_ATTRIBUTE_TYPE_TAG = "modification_type";

  protected static final String INT_TAG = "int";
  protected static final String DOUBLE_TAG = "double";
  protected static final String FLOAT_TAG = "float";
  protected static final String STRING_TAG = "string";
  protected static final String BOOLEAN_TAG = "boolean";

  protected static final String ALGORITHM_TAG = "algorithms";
  protected static final String ALGORITHM_FACTORY_TAG = "algorithm_factory";

  protected static final String SIMULATOR_TAG = "simulators";
  protected static final String SIMULATOR_FACTORY_TAG = "simulator_factory";

  protected static final String SCENARIOS_TAG            = "scenarios";
  protected static final String SCENARIO_PROBABILITY_TAG = "probability";
  protected static final String SCENARIO_INDEX_TAG = "index";
  protected static final String SCENARIO_BUILDER_TAG = "scenario_builder_factory";

  /**
   * Constructor
   */
  private JsonConfigurationUtilities() {
  }
  
}
