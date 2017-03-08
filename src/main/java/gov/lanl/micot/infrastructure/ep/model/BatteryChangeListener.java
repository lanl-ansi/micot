package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in a battery
 * @author Russell Bent
 */
public interface BatteryChangeListener {

  /**
   * Notification that the battery data changed
   * @param data
   */
  public void batteryDataChanged(Battery battery, Object attribute);
  
}
