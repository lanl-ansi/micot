package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Factory for creating shunt capacitor switches
 * @author Russell Bent
 */
public abstract class ShuntCapacitorSwitchFactory {
    
  private AssetRegistry<ShuntCapacitorSwitch> registry = null;
  
  /**
   * Constructor
   */
  protected ShuntCapacitorSwitchFactory() {
    registry = new AssetRegistry<ShuntCapacitorSwitch>();
  }

  /**
   * Create a new generator
   * @param bus
   * @return
   */
  public ShuntCapacitorSwitch createNewSwitch() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    ShuntCapacitorSwitch capacitor = new ShuntCapacitorSwitchImpl(assetId);
    registry.register(assetId, capacitor);
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
  protected void registerLegacy(String legacyTag, Object key, ShuntCapacitorSwitch asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected ShuntCapacitorSwitch getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }


}
