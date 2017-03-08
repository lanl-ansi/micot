package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in resistor
 * @author Russell Bent
 */
public interface ResistorChangeListener {

  /**
   * Notification that the valve data changed
   * @param data
   */
  public void resistorDataChanged(Resistor data, Object attribute);
  
}
