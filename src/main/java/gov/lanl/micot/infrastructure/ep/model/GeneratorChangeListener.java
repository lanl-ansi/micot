package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in generator
 * @author Russell Bent
 */
public interface GeneratorChangeListener {

  /**
   * Notification that the generatr data changed
   * @param data
   */
  public void generatorDataChanged(Generator data, Object attribute);
  
}
