package gov.lanl.micot.infrastructure.ep.io.opendss;

import java.util.StringTokenizer;


/**
 * Some constants associated with interacting with com objects
 * @author 210117
 *
 */
public class OpenDSSIOConstants {

  protected static final String OPENDSS = "OpenDSSEngine.DSS";
  protected static final String OPENDSS_START = "Start";
  public static final String TEXT  = "Text";
  public static final String COMMAND = "Command";
  protected static final String COMPILE = "Compile";
  public static final String ELEMENT = "ActiveElement";
  public static final String MODEL = "ActiveCircuit";
  public static final String SOLUTION = "Solution";
  public static final String SOLVE = "Solve";
  public static final String PROPERTIES = "Properties";
  public static final String PROPERTY_VALUE = "Val";

  protected static final String SAVE = "Save Circuit Dir=";

  // bus commands
  public static final String BUS_NAME = "Name";
  public static final String BUS_XCOORD = "x";
  public static final String BUS_YCOORD = "y";
  public static final String BUS_KVBASE = "kVBase";
  public static final String BUS_NAMES = "AllBusNames";
  public static final String BUSES = "Buses";
  public static final String NUMBER_OF_BUSES = "NumBuses";
  public static final String BUS_STATUS = "Enabled";
  public static final String BUS_PU_VOLTAGES = "puVoltages";

  // load commands
  public static final String LOADS = "Loads";
  public static final String NUMBER_OF_LOADS = "Count";
  public static final String LOAD_IDX = "idx";
  public static final String LOAD_BUS = "bus1";  
  public static final String LOAD_KW = "kW";
  public static final String LOAD_KVAR = "kvar";
  public static final String LOAD_STATUS = "Enabled";
  public static final String LOAD_PHASES = "Phases";
  public static final String LOAD_NAME = "Name";
  public static final String LOAD_MIN_PU = "Vminpu";
  public static final String LOAD_MAX_PU = "Vmaxpu";
  
  // generator commands
  public static final String GENERATORS = "Generators";
  public static final String NUMBER_OF_GENERATORS = "Count";
  public static final String GENERATOR_IDX = "idx";
  public static final String GENERATOR_BUS = "Bus1";  
  public static final String GENERATOR_KW = "kW";
  public static final String GENERATOR_KVAR = "kvar";
  public static final String GENERATOR_STATUS = "Enabled";
  public static final String GENERATOR_PHASES = "Phases";
  public static final String GENERATOR_NAME = "Name";
  public static final String FIRST_GENERATOR = "First";
  public static final String NEXT_GENERATOR = "Next";
  public static final String GENERATOR_MAX_KVAR = "Maxkvar";
  public static final String GENERATOR_MIN_KVAR = "Minkvar";

  // line commands 
  public static final String LINES = "Lines";
  public static final String NUMBER_OF_LINES = "Count";
  public static final String FIRST_LINE = "First";
  public static final String NEXT_LINE = "Next";
  public static final String LINE_FROM_BUS = "Bus1";
  public static final String LINE_TO_BUS = "Bus2";
  public static final String LINE_NAME = "Name";
  public static final String LINE_STATUS = "Enabled";
  public static final String LINE_CAPACITY = "NormAMPs";
  public static final String LINE_EMERGENCY_CAPACITY = "EmergAmps";
  public static final String LINE_LENGTH = "Length";
  public static final String POSITIVE_SEQUENCE_RESISTANCE = "R1";
  public static final String POSITIVE_SEQUENCE_REACTANCE = "X1";
  public static final String LINE_PHASES = "Phases";
  public static final String LINE_RMATRIX = "RMatrix";
  public static final String LINE_XMATRIX = "XMatrix";
  
  // transformer commands 
  public static final String TRANSFORMERS = "Transformers";
  public static final String NUMBER_OF_TRANSFORMERS = "Count";
  public static final String FIRST_TRANSFORMER = "First";
  public static final String NEXT_TRANSFORMER = "Next";
  public static final String TRANSFORMER_NAME = "Name";
  public static final String TRANSFORMER_BUSES = "buses";
  public static final String TRANSFORMER_CAPACITY = "kva";
  public static final String TRANSFORMER_RESISTANCE = "R";
  public static final String TRANSFORMER_STATUS = "Enabled";

  // reaactor commands
  public static final String REACTORS = "PDElements";
  public static final String NUMBER_OF_REACTORS = "Count";
  public static final String FIRST_REACTOR = "First";
  public static final String NEXT_REACTOR = "Next";
  public static final String REACTOR_BUSES = "buses";

  
  
  // file related constants
  public static final String REDIRECT = "Redirect";
  public static final String BUSCOORDS = "Buscoords";
  public static final String NEW = "New";
  public static final String CAPACITOR = "Capacitor";
  public static final String LINE = "Line";
  public static final String LINE_CODE = "LineCode";
  public static final String TRANSFORMER = "Transformer";
  public static final String VSOURCE = "Vsource";
  public static final String GROWTH_SHAPE = "GrowthShape";
  public static final String LOAD = "Load";
  public static final String GENERATOR = "Generator";
  public static final String LOAD_SHAPE = "LoadShape";
  public static final String REG_CONTROL = "RegControl";
  public static final String SPECTRUM    = "Spectrum";
  public static final String TCC_CURVE   = "TCC_Curve";
  public static final String EDIT   = "Edit";
  
  /**
   * Create the load model string
   * @param filename
   * @return
   */
  protected static String getLoadModelString(String filename) {
    return COMPILE + " (" + filename + ")";
  }
 
  /**
   * Create the load model string
   * @param filename
   * @return
   */
  protected static String getSaveModelString(String directory) {
    return SAVE + directory;
  }
 
  /**
	 * Get a piece of open dss data
	 * @param data
	 * @param tag
	 * @return
	 */
  public static String getData(String data, String tag, String breaker) {
	  StringTokenizer tokenizer = new StringTokenizer(data, " ");
	  while (tokenizer.hasMoreTokens()) {
	    String token = tokenizer.nextToken();
	    if (token.toLowerCase().startsWith(tag.toLowerCase())) {
	      StringTokenizer tokenizer2 = new StringTokenizer(token, breaker);
	      tokenizer2.nextToken();
	      return tokenizer2.nextToken();
	    }
	  }
	  return null;
	}
 
}
