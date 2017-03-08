package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Switchable shunt capacitors
 * @author Russell Bent
 */
public interface ShuntCapacitorSwitch extends Component {

  public static final String DESIRED_VOLTAGE_KEY             = "DESIRED_VOLTAGE";
  public static final String INITIAL_SUSCEPTANCE_KEY         = "INITIAL_SUSCEPTANCE";
  public static final String REMOTED_DESIRED_VOLTAGE_KEY     = "REMOTED_DESIRED_VOLTAGE";
  public static final String SCHEDULED_HIGH_VOLTAGE_KEY      = "SCHEDULED_HIGH_VOLTAGE";
  public static final String SCHEDULED_LOW_VOLTAGE_KEY       = "SCHEDULED_LOW_VOLTAGE";
  public static final String SUSCEPTANCE_KEY                 = "SUSCEPTANCE";
  public static final String NAME_KEY                        = "NAME";

  public static final String MAX_MW_KEY                      = "MAX_MW";
  public static final String MIN_MW_KEY                      = "MIN_MW";
  public static final String MAX_MVAR_KEY                    = "MAX_MVAR";
  public static final String MIN_MVAR_KEY                    = "MIN_MVAR";
  
  public static final String REACTIVE_COMPENSATION_KEY       = "REACTIVE_COMPENSATION";
  public static final String REAL_COMPENSATION_KEY           = "REAL_COMPENSATION";
    
  /**
   * @return the scheduleHighVoltage
   */
  public double getScheduleHighVoltage();

  /**
   * @param scheduleHighVoltage the scheduleHighVoltage to set
   */
  public void setScheduleHighVoltage(double scheduleHighVoltage);

  /**
   * @return the scheduledLowVoltage
   */
  public double getScheduledLowVoltage();

  /**
   * @param scheduledLowVoltage the scheduledLowVoltage to set
   */
  public void setScheduledLowVoltage(double scheduledLowVoltage);

  /**
   * @return the desiredVoltage
   */
  public double getDesiredVoltage();

  /**
   * @param desiredVoltage the desiredVoltage to set
   */
  public void setDesiredVoltage(double desiredVoltage);

  /**
   * @return the remotedDesiredVoltage
   */
  public double getRemoteDesiredVoltage();

  /**
   * @param remotedDesiredVoltage the remotedDesiredVoltage to set
   */
  public void setRemoteDesiredVoltage(double remotedDesiredVoltage);

  /**
   * @return the initialSusceptance
   */
  public double getInitialSusceptance();

  /**
   * @param initialSusceptance the initialSusceptance to set
   */
  public void setInitialSusceptance(double initialSusceptance);

  /**
   * @return the susceptance
   */
  public double getSusceptance();

  /**
   * @param susceptance the susceptance to set
   */
  public void setSusceptance(double susceptance);
  
  /**
   * Add a shunt capacitor switch listener
   * @param listener
   */
  public void addShuntCapacitorSwitchChangeListener(ShuntCapacitorSwitchChangeListener listener);
  
  /**
   * Remove a shunt capacitor switch listener
   * @param listener
   */
  public void removeShuntCapacitorSwitchChangeListener(ShuntCapacitorSwitchChangeListener listener);

  /**
   * Clone a switch
   * @return
   */
  public ShuntCapacitorSwitch clone();

  /**
   * Set the max MW
   * @param maxMW
   */
  public void setMaxMW(double maxMW);

  /**
   * Get the maximum mw
   * @return
   */
  public double getMaxMW();
  
  
  /**
   * Set the max MW
   * @param maxMW
   */
  public void setMinMW(double minMW);

  /**
   * Get the maximum mw
   * @return
   */
  public double getMinMW();
  
  /**
   * Set the max MW
   * @param maxMW
   */
  public void setMaxMVar(double maxMVar);

  /**
   * Get the maximum mw
   * @return
   */
  public double getMaxMVar();
  
  
  /**
   * Set the max MW
   * @param maxMW
   */
  public void setMinMVar(double minMVar);

  /**
   * Get the maximum mw
   * @return
   */
  public double getMinMVar();

  /**
   * Sets the reactive compensation
   * @param s
   */
  public void setReactiveCompensation(Number s);

  /**
   * Gets the reactive compensation
   * @return
   */
  public Number getReactiveCompensation();

  /**
   * Sets the real compensation
   * @param s
   */
  public void setRealCompensation(Number s);

  /**
   * Gets the real compensation
   * @return
   */
  public Number getRealCompensation();

  
}
