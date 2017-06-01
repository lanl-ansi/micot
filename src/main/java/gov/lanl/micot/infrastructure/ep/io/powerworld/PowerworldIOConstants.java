package gov.lanl.micot.infrastructure.ep.io.powerworld;


/**
 * Some constants associated with interacting with com objects in powerworld
 * @author Russell Bent
 *
 */
public class PowerworldIOConstants {

  
  // General commands
  protected static final String POWERWORLD                         = "pwrworld.SimulatorAuto";
  protected static final String OPEN_CASE                          = "OpenCase";
  public    static final String CLOSE_CASE                         = "CloseCase";
  public    static final String LIST_OF_DEVICES                    = "ListOfDevices";
  public    static final String GET_FIELD_LIST                     = "GetFieldList";
  public    static final String LIST_OF_DEVICES_AS_VARIANT_STRINGS = "ListOfDevicesAsVariantStrings";
  public    static final String GET_PARAMETERS_SINGLE_ELEMENT      = "GetParametersSingleElement";
  public    static final String RUN_SCRIPT_COMMAND                 = "RunScriptCommand";
  public    static final String CHANGE_PARAMETERS_SINGLE_ELEMENT   = "ChangeParametersSingleElement";
    
  // object types
  public    static final String BUS             = "bus";
  public    static final String GENERATOR       = "gen";
  public    static final String BRANCH          = "branch";
  public    static final String AREA            = "area";
  public    static final String ZONE            = "zone";
  public    static final String LOAD            = "load";
  public    static final String SHUNT           = "shunt";

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
  public static final String BUS_KV           = "pwBusKVVolt";
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
  public static final String SHUNT_SS_MW           = "pwBusSSMW";
  public static final String SHUNT_SS_MVAR         = "pwBusSS";
  public static final String SHUNT_MVAR            = "pwBusB";
  public static final String SHUNT_MW              = "pwBusG";
  
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
  protected static final String SAVE_CASE      = "SaveCase";
  protected static final String PWB            = "PWB";
  protected static final String MATPOWER       = "MatPower";
  protected static final String PTI23          = "PTI23";
  protected static final String PTI33          = "PTI33";

  // powerworld modes
  public    static final String EDIT_MODE          = "EnterMode(EDIT)";
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
