package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating valves
 * @author Russell Bent
 */
public abstract class ControlValveFactory {

  private AssetRegistry<ControlValve> registry = null;
  
  /**
   * Constuctor
   */
  protected ControlValveFactory() {
    registry = new AssetRegistry<ControlValve>();
  }

  /**
   * Creates a new valve between junction
   * @param bus
   * @return
   */
  public ControlValve createNewControlValve() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    ControlValve valve = new ControlValveImpl(assetId);
    registry.register(assetId, valve);
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
  protected void registerLegacy(String legacyTag, Object key, ControlValve asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected ControlValve getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
  
}
