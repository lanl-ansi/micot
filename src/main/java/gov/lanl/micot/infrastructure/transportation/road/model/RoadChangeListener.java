package gov.lanl.micot.infrastructure.transportation.road.model;

/**
 * Listener for changes in roads
 * @author Russell Bent
 */
public interface RoadChangeListener {

  /**
   * Notification that the line data changed
   * @param data
   */
  public void roadDataChanged(Road data, Object attribute);
  
}
