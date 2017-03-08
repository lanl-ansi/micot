package gov.lanl.micot.infrastructure.transportation.road.model;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Consumer;

/**
 * Abstract class for defining loads
 * @author Russell Bent
 *
 */
public interface Destination extends Consumer, Component {	
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void addDestinationChangeListener(DestinationChangeListener listener);
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void removeDestinationChangeListener(DestinationChangeListener listener);
    
  /**
   * Clone the destination
   * @return
   */
  public Destination clone();
}
