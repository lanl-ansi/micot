package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating short pipe factories
 * @author Russell Bent
 */
public abstract class ShortPipeFactory {
  
  private AssetRegistry<ShortPipe> registry = null;
  
  /**
   * Constructor
   */
  protected ShortPipeFactory() {
    registry = new AssetRegistry<ShortPipe>();
  }
	
  /**
   * Creates a new pipe between junction
   * @param bus
   * @return
   */
  public ShortPipe createNewShortPipe() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    ShortPipe pipe = new ShortPipeImpl(assetId);
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
  protected void registerLegacy(String legacyTag, Object key, ShortPipe asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected ShortPipe getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
