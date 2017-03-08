package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating zones
 * @author Russell Bent
 */
public abstract class ZoneFactory {
  
  private AssetRegistry<Zone> registry = null;

  /**
   * Constructor
   */
  public ZoneFactory() {
    registry = new AssetRegistry<Zone>();
  }
  
  /**
   * Create a new zone
   * @param bus
   * @return
   */
  public Zone createNewZone() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Zone zone = new ZoneImpl(assetId);
    registry.register(assetId, zone);
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
  protected void registerLegacy(String legacyTag, Object key, Zone asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Zone getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }


  
}
