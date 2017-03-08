package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in city gate
 * @author Russell Bent
 */
public interface CityGateChangeListener {

  /**
   * Notification that the load data changed
   * @param data
   */
  public void cityGateDataChanged(CityGate data, Object attribute);
  
}
