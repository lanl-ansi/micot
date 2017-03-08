package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in bus
 * @author Russell Bent
 */
public interface JunctionChangeListener {

  /**
   * Notification that the junction data changed
   * @param data
   */
  public void junctionDataChanged(Junction data, Object attribute);
  
}
