package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in bus
 * @author Russell Bent
 */
public interface BusChangeListener {

  /**
   * Notification that the bus data changed
   * @param data
   */
  public void busDataChanged(Bus data, Object attribute);
  
}
