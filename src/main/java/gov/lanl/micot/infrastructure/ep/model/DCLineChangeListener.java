package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in DC lines
 * @author Russell Bent
 */
public interface DCLineChangeListener {

  /**
   * Notification that the dc line data changed
   * @param data
   */
  public void dcLineDataChanged(DCLine data, Object attribute);
  
}
