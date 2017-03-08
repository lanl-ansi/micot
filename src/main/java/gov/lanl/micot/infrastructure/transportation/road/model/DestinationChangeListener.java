package gov.lanl.micot.infrastructure.transportation.road.model;

/**
 * Listener for changes in city gate
 * @author Russell Bent
 */
public interface DestinationChangeListener {

  /**
   * Notification that the load data changed
   * @param data
   */
  public void destinationDataChanged(Destination data, Object attribute);
  
}
