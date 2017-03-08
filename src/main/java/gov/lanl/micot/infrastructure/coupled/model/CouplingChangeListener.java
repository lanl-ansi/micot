package gov.lanl.micot.infrastructure.coupled.model;

/**
 * Listener for changes in coupling
 * @author Russell Bent
 */
public interface CouplingChangeListener {

  /**
   * Notification that the line data changed
   * @param data
   */
  public void couplingDataChanged(Coupling data, Object attribute);
  
}
