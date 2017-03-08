package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in generator
 * @author Russell Bent
 */
public interface ShuntCapacitorChangeListener {

  /**
   * Notification that the generatr data changed
   * @param data
   */
  public void shuntCapacitorDataChanged(ShuntCapacitor data, Object attribute);
  
}
