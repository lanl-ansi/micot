package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in reservoir
 * @author Russell Bent
 */
public interface ReservoirChangeListener {

  /**
   * Notification that the generatr data changed
   * @param data
   */
  public void reservoirDataChanged(Reservoir data, Object attribute);
  
}
