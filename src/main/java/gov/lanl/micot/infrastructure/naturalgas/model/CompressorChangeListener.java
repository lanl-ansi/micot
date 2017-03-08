package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Listener for changes in compressors
 * @author Russell Bent
 */
public interface CompressorChangeListener {

  /**
   * Notification that the junction data changed
   * @param data
   */
  public void compressorDataChanged(Compressor data, Object attribute);
  
}
