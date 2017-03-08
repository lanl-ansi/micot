package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in pipelines
 * @author Russell Bent
 */
public interface PipeChangeListener {

  /**
   * Notification that the line data changed
   * @param data
   */
  public void pipeDataChanged(Pipe data, Object attribute);
  
}
