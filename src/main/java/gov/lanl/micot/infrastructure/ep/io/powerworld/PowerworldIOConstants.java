package gov.lanl.micot.infrastructure.ep.io.powerworld;


/**
 * Some constants associated with interacting with com objects in powerworld
 * @author Russell Bent
 *
 */
public class PowerworldIOConstants {

  
  // General commands
  protected static final String POWERWORLD                         = "pwrworld.SimulatorAuto";
  public static final String OPEN_CASE                             = "OpenCase";
  public    static final String CLOSE_CASE                         = "CloseCase";
  public    static final String LIST_OF_DEVICES                    = "ListOfDevices";
  public    static final String GET_FIELD_LIST                     = "GetFieldList";
  public    static final String LIST_OF_DEVICES_AS_VARIANT_STRINGS = "ListOfDevicesAsVariantStrings";
  public    static final String GET_PARAMETERS_SINGLE_ELEMENT      = "GetParametersSingleElement";
  public    static final String GET_PARAMETERS_MULTIPLE_ELEMENT    = "GetParametersMultipleElement";
  public    static final String RUN_SCRIPT_COMMAND                 = "RunScriptCommand";
  public    static final String CHANGE_PARAMETERS_SINGLE_ELEMENT   = "ChangeParametersSingleElement";
    
  // object types
  public    static final String BUS               = "bus";
  public    static final String GENERATOR         = "gen";
  public    static final String BRANCH            = "branch";
  public    static final String AREA              = "area";
  public    static final String ZONE              = "zone";
  public    static final String LOAD              = "load";
  public    static final String SHUNT             = "shunt";
  public    static final String DC_TWO_TERMINAL   = "dctransmissionline";
  public    static final String DC_VOLTAGE_SOURCE = "VSCDCLine";
  public    static final String DC_MULTI_TERMINAL = "MTDCConverter";
  public    static final String DC_BUS            = "MTDCBus";
  public    static final String THREE_WINDING     = "3WXFormer";
  public    static final String SIMULATOR_OPTIONS = "Sim_Solution_Options";
  
  public    static final String SIM_SOLUTION_OPTIONS = "Sim_Solution_Options";

  // simulator fields
  public    static final String MVA_BASE             = "pwSbase";
    
  // bus data fields
  public static final String BUS_NUM          = "pwBusNum";
  public static final String BUS_NAME         = "pwBusName";
  public static final String BUS_AREA         = "pwAreaNum";
  public static final String BUS_ZONE         = "pwZoneNum";
  public static final String BUS_PU_VOLTAGE   = "pwBusPUVolt";
  public static final String BUS_ANGLE        = "pwBusAngle";
  public static final String BUS_LATITUDE     = "pwLatitude";
  public static final String BUS_LONGITUDE    = "pwLongitude";
  public static final String BUS_KV_VOLTAGE   = "pwBusKVVolt";
  public static final String BUS_KV_NOMINAL_VOLTAGE   = "pwBusNomVolt";  
  public static final String BUS_MAX_VOLTAGE  = "pwBusVoltLimHigh";
  public static final String BUS_MIN_VOLTAGE  = "pwBusVoltLimLow";
  public static final String BUS_SLACK        = "pwBusSlack";
  public static final String BUS_YES          = "yes";
  public static final String BUS_CAT          = "pwBusCat";
  public static final String BUS_STATUS       = "pwBusStatus";
  public static final String BUS_CONNECTED    = "connected";
  public static final String BUS_DISCONNECTED = "disconnected";
  public static final String BUS_DEAD         = "dead";
  public static final String BUS_PQ           = "PQ";
  public static final String BUS_PV           = "PV";
  public static final String BUS_SLACK_STRING = "slack";
  public static final String BUS_OWNER_NAME   = "pwOwnerName";

  // DC bus fields
  public static final String DC_BUS_NUM          = "pwBusNum";
  public static final String DC_RECORD_NUM       = "pwMTDCNum";
  public static final String DC_BUS_NAME         = "pwBusName";
  public static final String DC_BUS_AREA         = "pwAreaNum";
  public static final String DC_BUS_ZONE         = "pwZoneNum";
  public static final String DC_BUS_VOLTAGE_KV   = "pwMTDCBusVolt";
  
  // generator data fields
  public static final String GEN_NUM               = "pwGenID";
  public static final String GEN_FIXED_COST        = "pwGenFixedCost";
  public static final String GEN_COST_CURVE_POINTS = "pwGenCostCurvePoints";
  public static final String GEN_COST_MODEL        = "pwGenCostModel";
  public static final String GEN_FUEL_COST         = "pwGenFuelCost";
  public static final String GEN_FUEL_TYPE         = "pwGenFuelType";
  public static final String GEN_COST_CONSTANT     = "pwGenIOA";
  public static final String GEN_COST_LINEAR       = "pwGenIOB";
  public static final String GEN_COST_SQUARE       = "pwGenIOC";
  public static final String GEN_COST_CUBE         = "pwGenIOD";
  public static final String GEN_MVA_BASE          = "pwGenMVABase";
  public static final String GEN_MVAR              = "pwGenMVR";
  public static final String GEN_MVAR_MAX          = "pwGenMVRMax";
  public static final String GEN_MVAR_MIN          = "pwGenMVRMin";
  public static final String GEN_MW                = "pwGenMW";
  public static final String GEN_MW_MAX            = "pwGenMWMax";
  public static final String GEN_MW_MIN            = "pwGenMWMin";
  public static final String GEN_STATUS            = "pwGenStatus";
  public static final String GEN_VOLTAGE           = "pwGenVoltSet"; // this is the target setting for the voltage this bus is regulating 
  public static final String GEN_AREA              = "pwAreaNum"; 
  public static final String GEN_ZONE              = "pwZoneNum";
  public static final String GEN_CLOSED            = "closed";
  public static final String GEN_OPEN              = "open";
  public static final String REMOTE_BUS            = "pwGenRegNum"; 
  
  // load fields
  public static final String LOAD_NUM              = "pwLoadID";  
  public static final String LOAD_MVAR             = "pwLoadMVR";
  public static final String LOAD_MW               = "pwLoadMW";
  public static final String LOAD_STATUS           = "pwLoadStatus";
  public static final String LOAD_AREA             = "pwAreaNum"; 
  public static final String LOAD_ZONE             = "pwZoneNum";
  public static final String LOAD_CLOSED           = "closed";
  public static final String LOAD_OPEN             = "open";

  // shunt fields
  public static final String SHUNT_ID              = "pwShuntId";
  public static final String SHUNT_MODE            = "pwSSCMode";
  public static final String SHUNT_CONTROL_MODEL   = "pwSSCustomControlModelExpressionName";
  public static final String SHUNT_MAX_MVAR        = "pwSSMaxMVR";
  public static final String SHUNT_MIN_MVAR        = "pwSSMinMVR";
  public static final String SHUNT_MAX_MW          = "pwSSMaxMW";
  public static final String SHUNT_MIN_MW          = "pwSSMinMW";
  public static final String SHUNT_STATUS          = "pwSSStatus"; 
  public static final String SHUNT_HIGH_VOLTAGE    = "pwSSVHigh";
  public static final String SHUNT_LOW_VOLTAGE     = "pwSSVLow";
  public static final String SHUNT_SUSCEPTANCE     = "pwContinuousElementSusceptance";
  public static final String SHUNT_AREA            = "pwAreaNum"; 
  public static final String SHUNT_ZONE            = "pwZoneNum";
  public static final String SHUNT_FIXED           = "fixed";
  public static final String SHUNT_BUS_SHUNT       = "bus shunt";
  public static final String SHUNT_CLOSED          = "closed";
  public static final String SHUNT_OPEN            = "open";
  public static final String SHUNT_MVAR            = "pwSSNMVR";
  public static final String SHUNT_MW              = "pwSSNMW";
  
  // three winding fields
  public static final String THREE_WINDING_PRIMARY_BUS_NUM           = "pwBusNum3W";
  public static final String THREE_WINDING_SECONDARY_BUS_NUM         = "pwBusNum3W:1";
  public static final String THREE_WINDING_TERTIARY_BUS_NUM          = "pwBusNum3W:2";
  public static final String THREE_WINDING_STAR_BUS_NUM              = "pwBusNum3W:3";
  public static final String THREE_WINDING_CIRCUIT_NUM               = "pwLineCircuit";
    
  // branch fields
  public static final String BRANCH_TYPE               = "pwBranchDeviceType";
  public static final String BRANCH_AREA               = "pwAreaName";
  public static final String BRANCH_ZONE               = "pwZoneName";
  public static final String BRANCH_BUS_FROM_NUM       = "pwBusNum";
  public static final String BRANCH_BUS_TO_NUM         = "pwBusNum:1";
  public static final String BRANCH_THERMAL_LIMIT_A    = "pwLineAMVA";  
  public static final String BRANCH_THERMAL_LIMIT_B    = "pwLineBMVA";
  public static final String BRANCH_THERMAL_LIMIT_C    = "pwLineCMVA";
  public static final String BRANCH_NUM                = "pwLineCircuit";
  public static final String BRANCH_LENGTH             = "pwLineLength";  
  public static final String BRANCH_REACTIVE_LOSS      = "pwLineLossMVR";
  public static final String BRANCH_REAL_LOSS          = "pwLineLossMW";
  public static final String BRANCH_FROM_APPARENT_FLOW = "pwLineMVA";
  public static final String BRANCH_TO_APPARENT_FLOW   = "pwLineMVA:1";  
  public static final String BRANCH_FROM_REACTIVE_FLOW = "pwLineMVR";
  public static final String BRANCH_TO_REACTIVE_FLOW   = "pwLineMVR:1";
  public static final String BRANCH_FROM_REAL_FLOW     = "pwLineMW";
  public static final String BRANCH_TO_REAL_FLOW       = "pwLineMW:1";  
  public static final String BRANCH_RESISTANCE         = "pwLineR";
  public static final String BRANCH_STATUS             = "pwLineStatus";
  public static final String BRANCH_TAP_RATIO          = "pwLineTap";
  public static final String BRANCH_TAP_PHASE          = "pwLineTapPhase";
  public static final String BRANCH_REACTANCE          = "pwLineX";
  public static final String BRANCH_TRANSFORMER_TYPE   = "pwLineXFType";
  public static final String BRANCH_MAX_TAP_RATIO      = "pwXFTapMax";
  public static final String BRANCH_MIN_TAP_RATIO      = "pwXFTapMin";
  public static final String BRANCH_TAP_RATIO_STEPS    = "pwXFStep";
  public static final String BRANCH_CHARGING           = "pwLineC";
  public static final String BRANCH_TRANSFORMER        = "Transformer";
  public static final String BRANCH_TRANSFORMER_WINDING  = "TransformerWinding";
  public static final String BRANCH_CLOSED             = "closed";  
  public static final String BRANCH_OPEN               = "open";  
  public static final String BRANCH_TRANSFORMER_FIXED  = "fixed";
  public static final String BRANCH_TRANSFORMER_LTC    = "ltc";
  public static final String BRANCH_TRANSFORMER_MVAR   = "mvar";
  public static final String BRANCH_TRANSFORMER_PHASE  = "phase";
  public static final String BRANCH_REGULATED_BUS      = "pwXFRegBus";
  public static final String BRANCH_REGULATED_MAX      = "pwXFRegMax";
  public static final String BRANCH_REGULATED_MIN      = "pwXFRegMin";
  public static final String BRANCH_LINE_LIMIT_FROM    = "pwLineLimitFlow";
  public static final String BRANCH_LINE_LIMIT_TO      = "pwLineLimitFlow:1";
  public static final String BRANCH_LINE_LIMIT_TYPE    = "pwLineLimitType";

  // area fields
  public static final String AREA_NUM         = "pwAreaNum";
  public static final String AREA_NAME        = "pwAreaName";

  // zone fields
  public static final String ZONE_NUM         = "pwZoneNum";
  public static final String ZONE_NAME        = "pwZoneName";
  
  // save commands
  public static final String SAVE_CASE      = "SaveCase";
  public static final String PWB            = "PWB";
  protected static final String MATPOWER       = "MatPower";
  protected static final String PTI23          = "PTI23";
  protected static final String PTI33          = "PTI33";

  // powerworld modes
  public    static final String EDIT_MODE          = "EnterMode(EDIT)";
  public    static final String POWERFLOW_MODE     = "Entermode(PowerFlow)";
  public    static final String RUN_MODE           = "EnterMode(RUN)";
  public    static final String CREATE_DATA        = "CreateData";
  public    static final String CREATE_FILTER      = "DATA (FILTER, [ObjectType,FilterName,FilterLogic,FilterPre,Enabled])";
  public    static final String SUBDATA_HEADER     = "<SUBDATA Condition>";
  public    static final String SUBDATA_FOOTER     = "</SUBDATA>";
  public    static final String DELETE             = "Delete";

  public    static final String POWER_FLOW_COMMAND_RECT_NEWTON    = "SolvePowerFlow(RECTNEWT)";
  public    static final String POWER_FLOW_COMMAND_POLAR_NEWTON   = "SolvePowerFlow(POLARNEWT)";
  public    static final String POWER_FLOW_COMMAND_GAUSS_SEIDEL   = "SolvePowerFlow(GAUSSSEIDEL)";
  public    static final String POWER_FLOW_COMMAND_FAST_DECOUPLED = "SolvePowerFlow(FASTDECOUPLED)";
  public    static final String POWER_FLOW_COMMAND_DC             = "SolvePowerFlow(DC)";
  public    static final String POWER_FLOW_COMMAND_ROBUST         = "SolvePowerFlow(ROBUST)";
  public    static final String RESET_TO_FLAT_START               = "ResetToFlatStart(TRUE, NO, NO, NO)";

  // generator fuel types
  public    static final String COAL        = "coal";
  public    static final String NATURAL_GAS = "natural gas";
  public    static final String HYDRO       = "hydro";
  public    static final String NUCLEAR     = "nuclear";
  public    static final String SOLAR       = "solar";
  public    static final String WIND        = "wind";
  public    static final String OIL         = "oil";
  public    static final String DIESEL      = "diesel";
  public    static final String GEOTHERMAL  = "geothermol";
  public    static final String BIOMASS     = "biomass";

  // two terminal fields
  public static final String TWO_TERMINAL_AREA                    = "pwAreaName";
  public static final String TWO_TERMINAL_ZONE                    = "pwZoneName";
  public static final String TWO_TERMINAL_BUS_FROM_NUM            = "pwBusNum";
  public static final String TWO_TERMINAL_BUS_TO_NUM              = "pwBusNum:1";
  public static final String TWO_TERMINAL_NUM                     = "pwDCLID";
  public static final String TWO_TERMINAL_INVERTER_TAP            = "pwDCLITAP";
  public static final String TWO_TERMINAL_INVERTER_MIN_TAP        = "pwDCLITMN"; 
  public static final String TWO_TERMINAL_INVERTER_MAX_TAP        = "pwDCLITMX"; 
  public static final String TWO_TERMINAL_INVERTER_TAP_RATIO      = "pwDCLITR";
  public static final String TWO_TERMINAL_RECTIFIER_REACTIVE_FLOW = "pwDCLMVR";
  public static final String TWO_TERMINAL_INVERTER_REACTIVE_FLOW  = "pwDCLMVR:1";
  public static final String TWO_TERMINAL_RECTIFIER_REAL_FLOW     = "pwDCLMW";
  public static final String TWO_TERMINAL_INVERTER_REAL_FLOW      = "pwDCLMW:1";
  public static final String TWO_TERMINAL_CONTROL_MODE            = "pwDCLMode";
  public static final String TWO_TERMINAL_RECTIFIER_TAP           = "pwDCLRTAP";
  public static final String TWO_TERMINAL_RECTIFIER_TAP_MIN       = "pwDCLRTMN";
  public static final String TWO_TERMINAL_RECTIFIER_TAP_MAX       = "pwDCLRTMX";
  public static final String TWO_TERMINAL_RECTIFIER_TAP_RATIO     = "pwDCLRTTR";
  public static final String TWO_TERMINAL_DC_VOLTAGE              = "pwDCLSetVolt";
  public static final String TWO_TERMINAL_NAME                    = "pwDCName";
  public static final String TWO_TERMINAL_DC_RECTIFIER_VOLTAGE    = "pwMTDCBusVolt";
  public static final String TWO_TERMINAL_DC_INVERTER_VOLTAGE     = "pwMTDCBusVolt:1";
  public static final String TWO_TERMINAL_STATUS                  = "pwDerivedStatus";
  public static final String TWO_TERMINAL_RESISTANCE              = "pwDCLR";
  public static final String TWO_TERMINAL_CLOSED                  = "closed";
  public static final String TWO_TERMINAL_OPEN                    = "open";
  public static final String TWO_TERMINAL_CONTROL_MW              = "mw";
  public static final String TWO_TERMINAL_CONTROL_AMPS            = "amps";
  

  // voltage source fields
  public static final String VOLTAGE_SOURCE_AREA                            = "pwAreaName";
  public static final String VOLTAGE_SOURCE_ZONE                            = "pwZoneName";
  public static final String VOLTAGE_SOURCE_BUS_FROM_NUM                    = "pwBusNum";
  public static final String VOLTAGE_SOURCE_BUS_TO_NUM                      = "pwBusNum:1";
  public static final String VOLTAGE_SOURCE_FROM_AC_CONTROL_MODE            = "pwACMode";
  public static final String VOLTAGE_SOURCE_TO_AC_CONTROL_MODE              = "pwACMode:1";
  public static final String VOLTAGE_SOURCE_FROM_AC_SET_POINT               = "pwACSet";
  public static final String VOLTAGE_SOURCE_TO_AC_SET_POINT                 = "pwACSet:1";
  public static final String VOLTAGE_SOURCE_FROM_MW_INPUT                   = "pwDCLMW";
  public static final String VOLTAGE_SOURCE_TO_MW_INPUT                     = "pwDCLMW:1";
  public static final String VOLTAGE_SOURCE_RESISTANCE                      = "pwDCLR";
  public static final String VOLTAGE_SOURCE_FROM_DC_CONTROL_MODE            = "pwDCMode";
  public static final String VOLTAGE_SOURCE_TO_DC_CONTROL_MODE              = "pwDCMode:1";
  public static final String VOLTAGE_SOURCE_FROM_DC_SET_POINT               = "pwDCSet";
  public static final String VOLTAGE_SOURCE_TO_DC_SET_POINT                 = "pwDCSet:1";
  public static final String VOLTAGE_SOURCE_NAME                            = "pwDCName";
  public static final String VOLTAGE_SOURCE_FROM_MAX_MVAR                   = "pwGenMVRMax";
  public static final String VOLTAGE_SOURCE_TO_MAX_MVAR                     = "pwGenMVRMax:1";
  public static final String VOLTAGE_SOURCE_FROM_MIN_MVAR                   = "pwGenMVRMin";
  public static final String VOLTAGE_SOURCE_TO_MIN_MVAR                     = "pwGenMVRMin:1";
  public static final String VOLTAGE_SOURCE_FROM_CURRENT_MAX                = "pwImax";
  public static final String VOLTAGE_SOURCE_TO_CURRENT_MAX                  = "pwImax:1";
  public static final String VOLTAGE_SOURCE_FROM_FLOW_MVAR                  = "pwLineMVR";
  public static final String VOLTAGE_SOURCE_TO_FLOW_MVAR                    = "pwLineMVR:1";
  public static final String VOLTAGE_SOURCE_FROM_FLOW_MW                    = "pwLineMW";
  public static final String VOLTAGE_SOURCE_TO_FLOW_MW                      = "pwLineMW:1";
  public static final String VOLTAGE_SOURCE_STATUS                          = "pwLineStatus";
  public static final String VOLTAGE_SOURCE_FROM_DC_VOLTAGE                 = "pwMTDCBusVolt";
  public static final String VOLTAGE_SOURCE_TO_DC_VOLTAGE                   = "pwMTDCBusVolt:1";
  public static final String VOLTAGE_SOURCE_FROM_MVA_RATING                 = "pwSMax";
  public static final String VOLTAGE_SOURCE_TO_MVA_RATING                   = "pwSMax:1";  
  public static final String VOLTAGE_SOURCE_AC_CONTROL_MODE_VOLTAGE         = "voltage";
  public static final String VOLTAGE_SOURCE_AC_CONTROL_MODE_POWER_FACTOR    = "power factor";
  public static final String VOLTAGE_SOURCE_DC_CONTROL_MODE_OUT_OF_SERVICE  = "out-of-service";
  public static final String VOLTAGE_SOURCE_DC_CONTROL_MODE_VOLTAGE         = "voltage";
  public static final String VOLTAGE_SOURCE_DC_CONTROL_MODE_POWER           = "power";
  public static final String VOLTAGE_SOURCE_CLOSED                          = "closed";
  public static final String VOLTAGE_SOURCE_OPEN                            = "open";
  
  // multi terminal DC lines
  public static final String MULTI_TERMINAL_AREA                            = "pwAreaName";
  public static final String MULTI_TERMINAL_ZONE                            = "pwZoneName";
  public static final String MULTI_TERMINAL_BUS_FROM_NUM                    = "pwBusNum";
  public static final String MULTI_TERMINAL_BUS_TO_NUM                      = "pwBusNum:1";
  public static final String MULTI_TERMINAL_NUM                             = "pwMTDCNum";
  public static final String MULTI_TERMINAL_BASE_WINDING                    = "pwMTDCConvEBas";
  public static final String MULTI_TERMINAL_FIRING_ANGLE                    = "pwMTDCConvAngMxMn";
  public static final String MULTI_TERMINAL_FIRING_ANGLE_MAX                = "pwMTDCConvAngMxMn:1";
  public static final String MULTI_TERMINAL_FIRING_ANGLE_MIN                = "pwMTDCConvAngMxMn:2";
  public static final String MULTI_TERMINAL_CURRENT_ANGLE                   = "pwMTDCConvIDC";
  public static final String MULTI_TERMINAL_MW                              = "pwMTDCConvPQ";
  public static final String MULTI_TERMINAL_MVAR                            = "pwMTDCConvPQ:1";
  public static final String MULTI_TERMINAL_SETPOINT                        = "pwMTDCConvSetVL";
  public static final String MULTI_TERMINAL_STATUS                          = "pwMTDCConvStatus";
  public static final String MULTI_TERMINAL_TAP                             = "pwMTDCConvTapVals";
  public static final String MULTI_TERMINAL_TAP_MAX                         = "pwMTDCConvTapVals:1";
  public static final String MULTI_TERMINAL_TAP_MIN                         = "pwMTDCConvTapVals:2";
  public static final String MULTI_TERMINAL_TAP_STEP_SIZE                   = "pwMTDCConvTapVals:3";
  public static final String MULTI_TERMINAL_TYPE                            = "pwMTDCConvType";
  public static final String MULTI_TERMINAL_TRANSFORMER_TAP                 = "pwMTDCConvXFRat";
  public static final String MULTI_TERMINAL_CURRENT_RATING                  = "pwMTDCMaxConvCurrent";
  public static final String MULTI_TERMINAL_DC_VOLTAGE                      = "pwMTDCSchedVolt";  
  public static final String MULTI_TERMINAL_TYPE_RECT                       = "rect";
  public static final String MULTI_TERMINAL_TYPE_INV                        = "inv";
  public static final String MULTI_TERMINAL_CLOSED                          = "closed";
  public static final String MULTI_TERMINAL_OPEN                            = "open";
  
  // simulator options
  public static final String SIMULATION_OPTION_CHECK_SHUNTS                 = "pwChkShunts";
  public static final String SIMULATION_OPTION_PHASE_SHIFTERS               = "pwChkPhaseShifters";
  public static final String SIMULATION_OPTION_CHECK_SVC                    = "pwChkShunts:1";
  public static final String SIMULATION_OPTION_CHECK_TAPS                   = "pwChkTaps";
  public static final String SIMULATION_OPTION_CHECK_AREA                   = "pwChkAreaInt";
  public static final String SIMULATION_OPTION_CHECK_VARS                   = "pwChkVars";
  public static final String SIMULATION_OPTION_CHECK_MVAR_LIMITS            = "pwDisableGenMVRCheck";
  public static final String SIMULATION_OPTION_CHECK_MW_LIMITS              = "pwEnforceGenMWLimits";
    
  public static final String SIMULATION_OPTION_YES                          = "yes";
  public static final String SIMULATION_OPTION_NO                           = "no";


  
  /**
   * Generic Macro for creating data
   * 
   * Example of what a string should look like
   * 
   * CreateData(GEN, [BusNum, GenID, GENMWMax, GENAGCAble, GenEnforceMWLimits],[4, 2, 35, YES, YES]);
   * 
   * @param fields
   * @param values
   * @return
   */
  public static String createCreateDataCommand(String type, String[] fields, Object values[]) {
  	String command = CREATE_DATA + "(" + type + ", ";
  	
  	command += "[";
  	for (int i = 0; i < fields.length; ++i) {
  		if (i > 0) {
  			command += ", ";
  		}
  		command += fields[i];
  	}
  	command += "],"; 
  	
  	command += "[";
  	for (int i = 0; i < values.length; ++i) {
  		if (i > 0) {
  			command += ", ";
  		}
  		
  		if (values[i] instanceof String) {
  			command += "\"";
  		}
  		
  		command += values[i];
  		
  		if (values[i] instanceof String) {
  			command += "\"";
  		}
  	}
  	command += "]);";
  	return command;  	
  }

  /**
   * Create a bus filter header
   * @param name
   * @return
   */
  public static String createRemoveHeader(String type, String name) {
    return "\"" + type + "\" \"" + name + "\" \"AND\" \"NO \" \"YES\"";
  }

  /**
   * Create a delete command
   * @param type
   * @param filtername
   * @return
   */
  public static String createDeleteCommand(String type, String filtername) {
    return DELETE + "(" + type + "," + filtername + ")";
  }
}
