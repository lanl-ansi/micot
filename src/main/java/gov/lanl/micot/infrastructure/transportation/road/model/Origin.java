package gov.lanl.micot.infrastructure.transportation.road.model;

import java.util.Set;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Producer;

/**
 * Base class for generator types
 * @author Russell Bent
 */
public interface Origin extends Producer, Component {
 
  /**
   * Fill from a set of wells
   * @param stateDataMap
   */
  public void setProduction(Set<Origin> origins);
	    
  /**
   * Add a listener to the producer data
   * @param listener
   */
  public void addOriginDataListener(OriginChangeListener listener);
  
  /**
   * Remove a listener to the generator data
   * @param listener
   */
  public void removeOriginDataListener(OriginChangeListener listener);

  /**
   * Clone the origin
   * @return
   */
  public Origin clone();
   
}
