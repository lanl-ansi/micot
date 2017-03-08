package gov.lanl.micot.infrastructure.transportation.road.model;

/**
 * Listener for changes in Well
 * @author Russell Bent
 */
public interface OriginChangeListener {

  /**
   * Notification that the origin data changed
   * @param data
   */
  public void originDataChanged(Origin data, Object attribute);
  
}
