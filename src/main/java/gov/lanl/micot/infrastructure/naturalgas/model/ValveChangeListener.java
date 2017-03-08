package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in valves
 * @author Russell Bent
 */
public interface ValveChangeListener {

  /**
   * Notification that the valve data changed
   * @param data
   */
  public void valveDataChanged(Valve data, Object attribute);
  
}
