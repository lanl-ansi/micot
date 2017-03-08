package gov.lanl.micot.infrastructure.ep.model;

/**
 * Listener for changes in transformers
 * @author Russell Bent
 */
public interface TransformerChangeListener {

  /**
   * Notification that the transformer data changed
   * @param data
   */
  public void transformerDataChanged(Transformer data, Object attribute);
  
}
