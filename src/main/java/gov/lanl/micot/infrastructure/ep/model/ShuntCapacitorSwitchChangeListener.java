package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in generator
 * @author Russell Bent
 */
public interface ShuntCapacitorSwitchChangeListener {

  /**
   * Notification that the generatr data changed
   * @param data
   */
  public void shuntCapacitorSwitchDataChanged(ShuntCapacitorSwitch data, Object attribute);
  
}
