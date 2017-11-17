package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Consumer;

/**
 * Abstract class for defining loads
 * @author Russell Bent
 *
 */
public interface Load extends Consumer, Component {	

  public static final String REACTIVE_LOAD_KEY                 = "REACTIVE_LOAD";
  public static final String REAL_LOAD_KEY                     = "REAL_LOAD";

  public static final String REACTIVE_LOAD_MAX_KEY             = "REACTIVE_LOAD_MAX";
  public static final String REAL_LOAD_MAX_KEY                 = "REAL_LOAD_MAX";
  
  public static final String REAL_LOAD_MIN_KEY                 = "REAL_LOAD_MIN";
  public static final String REACTIVE_LOAD_MIN_KEY             = "REACTIVE_LOAD_MIN";
  
  public static final String LOAD_NAME_KEY                   = "NAME";

  public static final String NUM_PHASE_KEY                   = "NUMBER_OF_PHASES";
  public static final String REACTIVE_LOAD_A_KEY             = "REACTIVE_LOAD_A";
  public static final String REAL_LOAD_A_KEY                 = "REAL_LOAD_A";
  public static final String REACTIVE_LOAD_A_MAX_KEY         = "REACTIVE_LOAD_A_MAX";
  public static final String REAL_LOAD_A_MAX_KEY             = "REAL_LOAD_A_MAX";
  public static final String REACTIVE_LOAD_A_MIN_KEY         = "REACTIVE_LOAD_A_MIN";
  public static final String REAL_LOAD_A_MIN_KEY             = "REAL_LOAD_A_MIN";
  public static final String TARGET_REACTIVE_LOAD_A_KEY      = "TARGET_REACTIVE_LOAD_A";
  public static final String TARGET_REAL_LOAD_A_KEY          = "TARGET_REAL_LOAD_A";
  
  public static final String REACTIVE_LOAD_B_KEY             = "REACTIVE_LOAD_B";
  public static final String REAL_LOAD_B_KEY                 = "REAL_LOAD_B";
  public static final String REACTIVE_LOAD_B_MAX_KEY         = "REACTIVE_LOAD_B_MAX";
  public static final String REAL_LOAD_B_MAX_KEY             = "REAL_LOAD_B_MAX";
  public static final String REACTIVE_LOAD_B_MIN_KEY         = "REACTIVE_LOAD_B_MIN";
  public static final String REAL_LOAD_B_MIN_KEY             = "REAL_LOAD_B_MIN";
  public static final String TARGET_REACTIVE_LOAD_B_KEY      = "TARGET_REACTIVE_LOAD_B";
  public static final String TARGET_REAL_LOAD_B_KEY          = "TARGET_REAL_LOAD_B";
  
  public static final String REACTIVE_LOAD_C_KEY             = "REACTIVE_LOAD_C";
  public static final String REAL_LOAD_C_KEY                 = "REAL_LOAD_C";
  public static final String REACTIVE_LOAD_C_MAX_KEY         = "REACTIVE_LOAD_C_MAX";
  public static final String REAL_LOAD_C_MAX_KEY             = "REAL_LOAD_C_MAX";
  public static final String REACTIVE_LOAD_C_MIN_KEY         = "REACTIVE_LOAD_C_MIN";
  public static final String REAL_LOAD_C_MIN_KEY             = "REAL_LOAD_C_MIN";
  public static final String TARGET_REACTIVE_LOAD_C_KEY      = "TARGET_REACTIVE_LOAD_C";
  public static final String TARGET_REAL_LOAD_C_KEY          = "TARGET_REAL_LOAD_C";

  public static final String HAS_PHASE_A_KEY                   = "HAS_PHASE_A";
  public static final String HAS_PHASE_B_KEY                   = "HAS_PHASE_B";
  public static final String HAS_PHASE_C_KEY                   = "HAS_PHASE_C";
  
  /**
   * Get the real load
   * @return
   */
  public Number getRealLoad();
  
  /**
   * Set the real load
   * @param load
   */
  public void setRealLoad(Number load);
  
  /**
   * Get the reactive load
   * @return
   */
  public Number getReactiveLoad();
  
  /**
   * Set the reactive load
   * @param load
   */
  public void setReactiveLoad(Number load);
  
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
   * Get the norm of the load
   * @return
   */
  public Number getLoadNorm();
  
  /**
   * Clone a load
   * @return
   */
  public Load clone();
  
  /**
   * Get the maximum reactive load
   * @return
   */
  public double getReactiveLoadMax();
  
  /**
   * Get the minimum reactive load
   * @return
   */
  public double getReactiveLoadMin();
  
  /**
   * Get the maximum real load
   * @return
   */
  public double getRealLoadMax();
  
  /**
   * Get the minimum real load
   * @return
   */
  public double getRealLoadMin();
  
  /**
   * Set the maximum reactive load
   * @return
   */
  public void setReactiveLoadMax(double reactive);
  
  /**
   * Set the minimum reactive load
   * @return
   */
  public void setReactiveLoadMin(double reactive);
  
  /**
   * Set the maximum real load
   * @return
   */
  public void setRealLoadMax(double real);
  
  /**
   * Set the minimum real load
   * @return
   */
  public void setRealLoadMin(double real);

}
