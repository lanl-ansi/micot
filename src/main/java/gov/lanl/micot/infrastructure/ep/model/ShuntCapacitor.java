package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Abstract class for shunts
 * @author Russell Bent
 */
public interface ShuntCapacitor extends Component {

  public static final String REACTIVE_COMPENSATION_KEY       = "REACTIVE_COMPENSATION";
  public static final String REAL_COMPENSATION_KEY           = "REAL_COMPENSATION";
  public static final String SHUNT_NAME_KEY                  = "NAME";
     
  /**
   * Sets the reactive compensation
   * @param s
   */
  public void setReactiveCompensation(double s);

  /**
   * Gets the reactive compensation
   * @return
   */
  public double getReactiveCompensation();

  /**
   * Sets the real compensation
   * @param s
   */
  public void setRealCompensation(double s);

  /**
   * Gets the real compensation
   * @return
   */
  public double getRealCompensation();

  /**
   * Add a shunt capacitor data listener
   * @param listener
   */
  public void addShuntCapacitorChangeListener(ShuntCapacitorChangeListener listener);
  
  /**
   * Remove a shunt capacitor data listener
   * @param listener
   */
  public void removeShuntCapacitorChangeListener(ShuntCapacitorChangeListener listener);
  
  /**
   * Clone a shunt capacitor
   * @return
   */
  public ShuntCapacitor clone();

  
}
