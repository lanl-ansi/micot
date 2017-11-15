package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.Set;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Producer;

/**
 * Base class for generator types
 * @author Russell Bent
 */
public interface Reservoir extends Producer, Component {

  public static final String CAPACITY_KEY           = "CAPACITY";
  public static final String RESERVOIR_NAME_KEY     = "NAME";
    
  /**
   * Fill from a set of wells
   * @param stateDataMap
   */
  public void setProduction(Set<Reservoir> reservoir);
	
  /**
   * Set the capacity
   * @param capacity
   */
  public void setCapacity(double capacity);
  
  /**
   * Get the capacity
   * @return
   */
  public double getCapacity();
  
  /**
   * Add a listener to the producer data
   * @param listener
   */
  public void addReservoirChangeListener(ReservoirChangeListener listener);
  
  /**
   * Remove a listener to the generator data
   * @param listener
   */
  public void removeReservoirChangeListener(ReservoirChangeListener listener);
  
  /**
   * Clone the reservoir
   * @return
   */
  public Reservoir clone();
  
}
