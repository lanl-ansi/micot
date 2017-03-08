package gov.lanl.micot.util.io;

/**
 * This class reads parameters from a command line for unix style input
 * parameters and provides
 * various default behavior
 * @author Russell Bent
 */
public class ParameterReader {

  /**
   * Get the parameter
   * @param args
   * @param param
   * @return
   */
  private static String getParameter(String args[], String param) {
    for (int i = 0; i < args.length; ++i) {
      if (args[i].equals(param)) {
        return args[i+1];
      }
    }
    return null;
  }
  
  /**
   * Get a required String parameter
   * @param args
   * @param param
   * @return
   */
  public static String getRequiredStringParameter(String args[], String param, String errMsg) {
    String result = getParameter(args, param);
    if (result == null) {
      System.err.println("Error: missing required parameter " + errMsg + " " + param);
      System.exit(-1);
    } 
    return result;
  }
   
  /**
   * Get a required integer parameter
   * @param args
   * @param param
   * @return
   */
  public static int getRequiredIntegerParameter(String args[], String param, String errMsg) {
    String result = getParameter(args, param);
    if (result == null) {
      System.err.println("Error: missing required parameter " + errMsg + " " + param);
      System.exit(-1);
    } 
    return new Integer(result);
  }
  
  /**
   * Get a default double value
   * @param args
   * @param param
   * @return
   */
  public static double getDefaultDoubleParameter(String args[], String param, double def) {
    String result = getParameter(args, param);
    if (result == null) {
      return def;
    } 
    return new Double(result);
  }
  
  /**
   * Get a default double value
   * @param args
   * @param param
   * @return
   */
  public static int getDefaultIntegerParameter(String args[], String param, int def) {
    String result = getParameter(args, param);
    if (result == null) {
      return def;
    } 
    return new Integer(result);
  }
  
  /**
   * Get a default string value
   * @param args
   * @param param
   * @return
   */
  public static String getDefaultStringParameter(String args[], String param, String def) {
    String result = getParameter(args, param);
    if (result == null) {
      return def;
    } 
    return result;
  }
  
  public static String[] getRequiredMultiStringParameter(String[] args, String param, String errMsg, int sources) {
    String[] output = new String[sources];
    for (int i = 0; i < args.length; ++i) {
      if (args[i].equals(param)) {
        ++i;
        for (int j = 0; j < sources; ++j) {
          output[j] = args[i];
          ++i;
        }        
        return output;
      }
    }
    System.err.println("Error: missing required parameter " + errMsg + " " + param);
    System.exit(-1);
    return null;
  }


}
