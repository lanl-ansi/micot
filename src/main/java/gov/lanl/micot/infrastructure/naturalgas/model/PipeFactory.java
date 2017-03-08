package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating line factories
 * @author Russell Bent
 */
public abstract class PipeFactory {
  
  private AssetRegistry<Pipe> registry = null;
  
  /**
   * Constructor
   */
  protected PipeFactory() {
    registry = new AssetRegistry<Pipe>();
  }
	
  /**
   * Creates a new pipe between junction
   * @param bus
   * @return
   */
  public Pipe createNewPipe() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Pipe pipe = new PipeImpl(assetId);
    registry.register(assetId, pipe);
    return registry.getCopy(assetId);
  }
  
  /**
   * Does the legacy id exist
   * @param legacyTag
   * @param key
   * @return
   */
  protected boolean doesLegacyExist(String legacyTag, Object key) {
    return registry.doesLegacyExist(legacyTag, key);    
  }

  /**
   * Register the legacy id
   * @param legacyTag
   * @param key
   * @param asset
   */
  protected void registerLegacy(String legacyTag, Object key, Pipe asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Pipe getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
