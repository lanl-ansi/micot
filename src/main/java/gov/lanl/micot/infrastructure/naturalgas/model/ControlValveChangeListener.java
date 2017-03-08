package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in valves
 * @author Russell Bent
 */
public interface ControlValveChangeListener {

  /**
   * Notification that the valve data changed
   * @param data
   */
  public void controlValveDataChanged(ControlValve data, Object attribute);
  
}
