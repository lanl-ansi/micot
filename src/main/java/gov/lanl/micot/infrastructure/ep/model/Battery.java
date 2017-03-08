package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Base class for batteries in an electric power network
 * @author Russell Bent
 */
public interface Battery extends ElectricPowerProducer, Component, Cloneable {

  public static final String ENERGY_CAPACITY_KEY             = "ENERGY_CAPACITY";
  public static final String USED_ENERGY_CAPACITY_KEY        = "USED_ENERGY_CAPACITY";
  public static final String NAME_KEY                        = "NAME";
  
  
  /**
   * Setter for the energy capacity of a battery
   * @param energy
   */
  public void setEnergyCapacity(Number energy);

  /**
   * Get the energy capacity of a battery
   * @return
   */
  public Number getEnergyCapacity();
  
  /**
   * Setter for the energy capacity of a battery
   * @param energy
   */
  public void setUsedEnergyCapacity(Number energy);

  /**
   * Get the energy capacity of a battery
   * @return
   */
  public Number getUsedEnergyCapacity();

  /**
   * Add a listener to the battery data
   * @param listener
   */
  public void addBatteryDataListener(BatteryChangeListener listener);
  
  /**
   * Remove a listener to the battery data
   * @param listener
   */
  public void removeBatteryDataListener(BatteryChangeListener listener);

  /**
   * Gets the maximum amount of production that can occur
   * based on capacity and maximum discharge rate
   * @return
   */
  public Number getAvailableMaximumRealProduction();

  /**
   * Gets the minimum amount of production that can occur
   * based on capacity and maximum discharge rate
   * @return
   */
  public Number getAvailableMinimumRealProduction();

  /**
   * Gets the maximum amount of production that can occur
   * based on capacity and maximum discharge rate
   * @return
   */
  public Number getAvailableMaximumReactiveProduction();

  /**
   * Gets the minimum amount of production that can occur
   * based on capacity and maximum discharge rate
   * @return
   */
  public Number getAvailableMinimumReactiveProduction();

  /**
   * Clone a battery
   * @return
   */
  public Battery clone();

  
}
