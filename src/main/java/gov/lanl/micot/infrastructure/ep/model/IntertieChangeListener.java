package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in intertie
 * @author Russell Bent
 */
public interface IntertieChangeListener {

  /**
   * Notification that the intertie data changed
   * @param data
   */
  public void intertieDataChanged(Intertie data, Object attribute);
  
}
