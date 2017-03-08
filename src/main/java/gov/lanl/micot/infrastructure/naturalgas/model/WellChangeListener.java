package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in Well
 * @author Russell Bent
 */
public interface WellChangeListener {

  /**
   * Notification that the generatr data changed
   * @param data
   */
  public void wellDataChanged(Well data, Object attribute);
  
}
