package gov.lanl.micot.infrastructure.transportation.road.model;

/**
 * Listener for changes in bus
 * @author Russell Bent
 */
public interface IntersectionChangeListener {

  /**
   * Notification that the junction data changed
   * @param data
   */
  public void intersectionDataChanged(Intersection data, Object attribute);
  
}
