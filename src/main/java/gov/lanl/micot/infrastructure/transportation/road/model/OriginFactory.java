package gov.lanl.micot.infrastructure.transportation.road.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for generator factories
 * @author Russell Bent
 */
public abstract class OriginFactory {

  private AssetRegistry<Origin> registry = null;
  
  /**
   * Constructor
   */
  protected OriginFactory() {
    registry = new AssetRegistry<Origin>();    
  }
  
  /**
   * Create a new load
   * @param bus
   * @return
   */
  protected Origin createNewOrigin() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Origin origin = new OriginImpl(assetId);
    registry.register(assetId, origin);
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
  protected void registerLegacy(String legacyTag, Object key, Origin asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Origin getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
