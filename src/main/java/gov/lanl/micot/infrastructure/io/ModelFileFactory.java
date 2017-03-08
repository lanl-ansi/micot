package gov.lanl.micot.infrastructure.io;

import gov.lanl.micot.infrastructure.model.Model;

/**
 * Interface for model file factory
 * @author Russell Bent
 */
@SuppressWarnings("rawtypes")
public interface ModelFileFactory<V1 extends Model, V2 extends ModelFile> {

  /**
   * Create a model file
   * @param model
   * @return
   */
  public V2 createModelFile(V1 model);

  /**
   * Create a model file from an extension
   * @param fileExtension
   * @return
   */
  public V2 createModelFileFromExtension(String fileExtension);
  
  /**
   * Create a model file
   * @param filename
   * @return
   */
  public V2 createModelFile(String filename);


  
}
