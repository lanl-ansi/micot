package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in short pipelines
 * @author Russell Bent
 */
public interface ShortPipeChangeListener {

  /**
   * Notification that the short line data changed
   * @param data
   */
  public void shortPipeDataChanged(ShortPipe data, Object attribute);
  
}
