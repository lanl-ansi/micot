package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in lines
 * @author Russell Bent
 */
public interface LineChangeListener {

  /**
   * Notification that the line data changed
   * @param data
   */
  public void lineDataChanged(Line data, Object attribute);
  
}
