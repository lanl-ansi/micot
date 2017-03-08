package gov.lanl.micot.infrastructure.naturalgas.model.gaslib;

/**
 * Some constants associated with Gaslib Model Data
 * @author Russell Bent
 *
 */
public class GaslibModelConstants {
  
  /**
   * Constructor
   */
  private GaslibModelConstants() {    
  }
  
  public static final String GASLIB_LEGACY_ID_KEY      = "GASLIB_LEGACY_ID";
  
  public static final String COMPRESSOR_FILE_TAG = "compressor.file";
  public static final String NETWORK_FILE_TAG    = "network.file";
  public static final String INJECTION_FILE_TAG  = "injection.file";
  public static final String INJECTION_SCENARIO_TAG = "injection.scenario";
  
  public static final String NODES_TAG = "nodes";
  public static final String NODE_TAG = "node";
  public static final String CONNECTIONS_TAG = "connections";

  public static final String VALUE_TAG = "value";
  public static final String UNIT_TAG = "unit";
  
  public static final String BAR_PRESSURE_CONSTANT = "bar";
  public static final String CELSIUS_CONSTANT = "Celsius";  
  public static final String FLOW_M_CUBED_PER_HOUR_CONSTANT = "1000m_cube_per_hour";
  public static final Object M3_PER_M_CUBE_CONSTANT = "MJ_per_m_cube";
  public static final String KG_PER_M_CUBE_CONSTANT = "kg_per_m_cube";
  public static final String KG_PER_KMOL_CONSTANT = "kg_per_kmol";
  public static final Object KELVIN_CONSTANT = "K";
  public static final String KM_CONSTANT = "km";
  public static final String MM_CONSTANT = "mm";
  public static final String W_PER_M_SQUARE_PER_K_CONSTANT = "W_per_m_square_per_K";

  public static final String NODE_LONGITUDE_TAG = "geoWGS84Long";
  public static final String NODE_LATITUDE_TAG = "geoWGS84Lat";
  public static final String NODE_ID_TAG = "id";
  public static final String NODE_HEIGHT_TAG = "height";  
  public static final String NODE_PRESSURE_MIN_TAG = "pressureMin";
  public static final String NODE_PRESSURE_MAX_TAG = "pressureMax";
  public static final String NODE_FLOW_TAG = "flow";
  public static final String NODE_FLOW_MIN_TAG = "flowMin";
  public static final String NODE_FLOW_MAX_TAG = "flowMax";
  public static final String NODE_GAS_TEMPERATURE_TAG = "gasTemperature";
  public static final String NODE_CALORIFIC_TAG = "calorificValue";
  public static final String NODE_NORMDENSITY_TAG = "normDensity";
  public static final String NODE_COEFFICIENT_A_HEAT_CAPACITY_TAG = "coefficient-A-heatCapacity";
  public static final String NODE_COEFFICIENT_B_HEAT_CAPACITY_TAG = "coefficient-B-heatCapacity";
  public static final String NODE_COEFFICIENT_C_HEAT_CAPACITY_TAG = "coefficient-C-heatCapacity";
  public static final String NODE_MOLAR_MASS_TAG = "molarMass";
  public static final String NODE_PSEUDOCRITICAL_PRESSURE_TAG = "pseudocriticalPressure";
  public static final String NODE_PSEUDOCRITICAL_TEMPERATURE_TAG = "pseudocriticalTemperature";
  public static final String NODE_NORM_DENSITY_TAG = "normDensity";
  public static final String NODE_HEAT_CAPACITY_A_TAG = "coefficient-A-heatCapacity";
  public static final String NODE_HEAT_CAPACITY_B_TAG = "coefficient-B-heatCapacity";
  public static final String NODE_HEAT_CAPACITY_C_TAG = "coefficient-C-heatCapacity";
  public static final String NODE_PSEUDO_CRITICAL_PRESSURE_TAG = "pseudocriticalPressure";
  public static final String NODE_PSEUDO_CRITICAL_TEMPERATURE_TAG = "pseudocriticalTemperature";

  public static final String CONNECTION_ID_TAG = "id";
  public static final String CONNECTION_FLOW_MIN_TAG = "flowMin";
  public static final String CONNECTION_FLOW_MAX_TAG = "flowMax";
  public static final String CONNECTION_FROM_TAG = "from";
  public static final String CONNECTION_TO_TAG = "to";
  public static final String CONNECTION_LENGTH_TAG = "length";
  public static final String CONNECTION_DIAMETER_TAG = "diameter";
  public static final String CONNECTION_ROUGHNESS_TAG = "roughness";
  public static final String CONNECTION_PRESSURE_MAX_TAG = "pressureMax";
  public static final String CONNECTION_HEAT_TRANSFER_COEFFICIENT_TAG = "heatTransferCoefficient";
  public static final String CONNECTION_FPRESSURE_DIFFERENTIAL_MAX_TAG = "pressureDifferentialMax";
  public static final String CONNECTION_FPRESSURE_DIFFERENTIAL_MIN_TAG = "pressureDifferentialMin";
  
  public static final String SOURCE_TAG = "source";
  public static final String SINK_TAG = "sink";
  public static final String INNODE_TAG = "innode";
  public static final String PIPE_TAG = "pipe";
  public static final String SHORT_PIPE_TAG = "shortPipe";
  public static final String VALVE_TAG = "valve";
  public static final String CONTROL_VALVE_TAG = "controlValve";
  public static final String RESISTOR_TAG = "resistor";
  public static final String COMPRESSOR_TAG = "compressorStation";

  public static final String COMPRESSOR_DIAMETER_IN_TAG = "diameterIn";
  public static final String COMPRESSOR_DIAMETER_OUT_TAG = "diameterOut";
  public static final String COMPRESSOR_DRAG_FACTOR_OUT_TAG = "dragFactorOut";
  public static final String COMPRESSOR_DRAG_FACTOR_IN_TAG = "dragFactorIn";
  public static final String COMPRESSOR_PRESSURE_IN_MIN = "pressureInMin";
  public static final String COMPRESSOR_PRESSURE_OUT_MIN = "pressureOutMax";
  public static final String COMPRESSOR_COOLER_EXISTING_TAG = "gasCoolerExisting";
  public static final String INTERNAL_BYPASS_REQUIRED_TAG = "internalBypassRequired";
  public static final String GAS_PREHEATER_EXISTING_TAG = "gasPreheaterExisting";
  
  public static final String PRESSURE_OUT_MAX_TAG = "pressureOutMax";
  public static final String PRESSURE_IN_MIN_TAG = "pressureInMin";

  public static final String SCENARIO_TAG    = "scenario";
  public static final String SCENARIO_ID_TAG = "id";

  public static final String VALVE_PRESSURE_LOSS_IN_TAG = "pressureLossIn";
  public static final String VALVE_PRESSURE_LOSS_OUT_TAG = "pressureLossOut";

  public static final String DRAG_FACTOR_TAG = "dragFactor";


 

}
