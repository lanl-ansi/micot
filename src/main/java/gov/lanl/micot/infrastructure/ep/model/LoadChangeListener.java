package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in load
 * @author Russell Bent
 */
public interface LoadChangeListener {

  /**
   * Notification that the load data changed
   * @param data
   */
  public void loadDataChanged(Load data, Object attribute);
  
}
