package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Consumer;

/**
 * Abstract class for defining loads
 * @author Russell Bent
 *
 */
public interface Load extends Consumer, Component {	

  public static final String ACTUAL_REACTIVE_LOAD_KEY        = "ACTUAL_REACTIVE_LOAD";
  public static final String ACTUAL_REAL_LOAD_KEY            = "ACTUAL_REAL_LOAD";
  public static final String DESIRED_REACTIVE_LOAD_KEY       = "DESIRED_REACTIVE_LOAD";
  public static final String DESIRED_REAL_LOAD_KEY           = "DESIRED_REAL_LOAD";
  public static final String LOAD_NAME_KEY                   = "NAME";

  public static final String NUM_PHASE_KEY                   = "NUMBER_OF_PHASES";
  public static final String ACTUAL_REACTIVE_LOAD_A_KEY      = "ACTUAL_REACTIVE_LOAD_A";
  public static final String ACTUAL_REAL_LOAD_A_KEY          = "ACTUAL_REAL_LOAD_A";
  public static final String DESIRED_REACTIVE_LOAD_A_KEY     = "DESIRED_REACTIVE_LOAD_A";
  public static final String DESIRED_REAL_LOAD_A_KEY         = "DESIRED_REAL_LOAD_A";
  public static final String TARGET_REACTIVE_LOAD_A_KEY      = "TARGET_REACTIVE_LOAD_A";
  public static final String TARGET_REAL_LOAD_A_KEY          = "TARGET_REAL_LOAD_A";
  
  public static final String ACTUAL_REACTIVE_LOAD_B_KEY      = "ACTUAL_REACTIVE_LOAD_B";
  public static final String ACTUAL_REAL_LOAD_B_KEY          = "ACTUAL_REAL_LOAD_B";
  public static final String DESIRED_REACTIVE_LOAD_B_KEY     = "DESIRED_REACTIVE_LOAD_B";
  public static final String DESIRED_REAL_LOAD_B_KEY         = "DESIRED_REAL_LOAD_B";
  public static final String TARGET_REACTIVE_LOAD_B_KEY      = "TARGET_REACTIVE_LOAD_B";
  public static final String TARGET_REAL_LOAD_B_KEY          = "TARGET_REAL_LOAD_B";
  
  public static final String ACTUAL_REACTIVE_LOAD_C_KEY      = "ACTUAL_REACTIVE_LOAD_C";
  public static final String ACTUAL_REAL_LOAD_C_KEY          = "ACTUAL_REAL_LOAD_C";
  public static final String DESIRED_REACTIVE_LOAD_C_KEY     = "DESIRED_REACTIVE_LOAD_C";
  public static final String DESIRED_REAL_LOAD_C_KEY         = "DESIRED_REAL_LOAD_C";
  public static final String TARGET_REACTIVE_LOAD_C_KEY      = "TARGET_REACTIVE_LOAD_C";
  public static final String TARGET_REAL_LOAD_C_KEY          = "TARGET_REAL_LOAD_C";

  public static final String HAS_PHASE_A_KEY                   = "HAS_PHASE_A";
  public static final String HAS_PHASE_B_KEY                   = "HAS_PHASE_B";
  public static final String HAS_PHASE_C_KEY                   = "HAS_PHASE_C";
  
  /**
   * Get the real load
   * @return
   */
  public Number getDesiredRealLoad();
  
  /**
   * Set the real load
   * @param load
   */
  public void setDesiredRealLoad(Number load);
  
  /**
   * Get the reactive load
   * @return
   */
  public Number getDesiredReactiveLoad();
  
  /**
   * Set the reactive load
   * @param load
   */
  public void setDesiredReactiveLoad(Number load);
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void addLoadChangeListener(LoadChangeListener listener);
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void removeLoadChangeListener(LoadChangeListener listener);

  /**
   * Get the real load
   * @return
   */
  public Number getActualRealLoad();
  
  /**
   * Set the real load
   * @param load
   */
  public void setActualRealLoad(Number load);
  
  /**
   * Get the reactive load
   * @return
   */
  public Number getActualReactiveLoad();
  
  /**
   * Set the reactive load
   * @param load
   */
  public void setActualReactiveLoad(Number load);
  
  /**
   * Get the norm of the load
   * @return
   */
  public Number getDesiredLoadNorm();
  
  /**
   * Clone a load
   * @return
   */
  public Load clone();


}
