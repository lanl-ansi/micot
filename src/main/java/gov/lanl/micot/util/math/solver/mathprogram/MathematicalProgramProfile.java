package gov.lanl.micot.util.math.solver.mathprogram;

import java.util.HashMap;

/**
 * A class for profiling the performance of the math program solver
 * @author Russell Bent
 *
 */
public class MathematicalProgramProfile extends HashMap<String, Object> {

  private static final long serialVersionUID = 1L;

  public static final String CPU_TIME_KEY = "CPU TIME";
  public static final String OBJECTIVE_VALUE_KEY = "OBJECTIVE VALUE";

  public static final String NUMBER_OF_COLUMNS_KEY = "NUMBER OF COLUMNS";
  public static final String NUMBER_OF_ROWS_KEY = "NUMBER OF ROWS";
  public static final String NUMBER_OF_BINARY_VARIABLES_KEY = "NUMBER OF BINARY VARIABLES";
  public static final String NUMBER_OF_INTEGER_VARIABLES_KEY = "NUMBER OF INTEGER VARIABLES";
  public static final String NUMBER_OF_CONTINIOUS_VARIABLES_KEY = "NUMBER OF CONTINIOUS VARIABLES";
  public static final String NUMBER_OF_SOS_CONSTRAINTS_KEY = "NUMBER OF SOS CONSTRAINTS";
  public static final String NUMBER_OF_NON_ZERO_ELEMENTS_KEY = "NUMBER OF NON ZERO ELEMENTS";
  public static final String NUMBER_OF_SEMI_INTEGER_VARIABLES_KEY = "NUMBER OF SEMI INTEGER VARIABLES";
  public static final String NUMBER_OF_SEMI_CONTINIOUS_VARIABLES_KEY = "NUMBER OF SEMI CONTINIOUS VARIABLES";
  public static final String NUMBER_OF_QUADRATIC_CONSTRAINTS_KEY = "NUMBER OF QUADRATIC CONSTRAINTS";
  public static final String SOLVER_STATUS_KEY = "SOLVER STATUS";
    
  /**
   * Constructor
   */
  public MathematicalProgramProfile() {
    super();
  }

  /**
   * Get a parameter as a double
   * @param key
   * @return
   */
  public double getDouble(String key) {
    return (Double)get(key);
  }
  
  /**
   * Get a parameter as an integer
   * @param key
   * @return
   */
  public int getInteger(String key) {
    return (Integer)get(key);
  }

  /**
   * get a parameter as a boolean
   * @param key
   * @return
   */
  public boolean getBoolean(String key) {
    return (Boolean)get(key);
  }

  /**
   * returns a parameter as a string
   * @param key
   * @return
   */
  public String getString(String key) {
    return (String)get(key);
  }
  
  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setCPUTime(double cpuTime) {
    put(CPU_TIME_KEY, cpuTime);
  }
  
  /**
   * set the objective value
   * @param value
   */
  public void setObjectiveValue(double value) {
    put(OBJECTIVE_VALUE_KEY, value);
  }
  
  /**
   * Get the cpu time
   * @return
   */
  public double getCPUTime() {
    return getDouble(CPU_TIME_KEY);
  }

  /**
   * Get the objective value
   * @return
   */
  public double getObjectiveValue() {
    return getDouble(OBJECTIVE_VALUE_KEY);
  }
  
  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfColumns(int value) {
    put(NUMBER_OF_COLUMNS_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfColumns() {
    return getInteger(NUMBER_OF_COLUMNS_KEY);
  }


  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfRows(int value) {
    put(NUMBER_OF_ROWS_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfRows() {
    return getInteger(NUMBER_OF_ROWS_KEY);
  }


  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfBinaryVars(int value) {
    put(NUMBER_OF_BINARY_VARIABLES_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfBinaryVars() {
    return getInteger(NUMBER_OF_BINARY_VARIABLES_KEY);
  }

  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfIntegerVars(int value) {
    put(NUMBER_OF_INTEGER_VARIABLES_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfIntegerVars() {
    return getInteger(NUMBER_OF_INTEGER_VARIABLES_KEY);
  }

  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfContiniousVars(int value) {
    put(NUMBER_OF_CONTINIOUS_VARIABLES_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfContiniousVars() {
    return getInteger(NUMBER_OF_CONTINIOUS_VARIABLES_KEY);
  }

  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfSOSConstraints(int value) {
    put(NUMBER_OF_SOS_CONSTRAINTS_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfSOSConstraints() {
    return getInteger(NUMBER_OF_SOS_CONSTRAINTS_KEY);
  }

  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfNonZeroElements(int value) {
    put(NUMBER_OF_NON_ZERO_ELEMENTS_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfNonZeroElements() {
    return getInteger(NUMBER_OF_NON_ZERO_ELEMENTS_KEY);
  }

  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfSemiIntegerVariables(int value) {
    put(NUMBER_OF_SEMI_INTEGER_VARIABLES_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfSemiIntegerVariablesElements() {
    return getInteger(NUMBER_OF_SEMI_INTEGER_VARIABLES_KEY);
  }

  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfSemiContiniousVariables(int value) {
    put(NUMBER_OF_SEMI_CONTINIOUS_VARIABLES_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfSemiContiniousVariablesElements() {
    return getInteger(NUMBER_OF_SEMI_CONTINIOUS_VARIABLES_KEY);
  }

  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setNumberOfQuadraticConstraints(int value) {
    put(NUMBER_OF_QUADRATIC_CONSTRAINTS_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public int getNumberOfQuadraticConstraints() {
    return getInteger(NUMBER_OF_QUADRATIC_CONSTRAINTS_KEY);
  }

  /**
   * Set the cpu time
   * @param cpuTime
   */
  public void setSolverStatus(String value) {
    put(SOLVER_STATUS_KEY, value);
  }
    
  /**
   * Get the cpu time
   * @return
   */
  public String getSolverStatus() {
    return getString(SOLVER_STATUS_KEY);
  }
}
