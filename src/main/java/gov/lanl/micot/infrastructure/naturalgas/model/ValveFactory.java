package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating valves
 * @author Russell Bent
 */
public abstract class ValveFactory {

  private AssetRegistry<Valve> registry = null;
  
  /**
   * Constructor
   */
  protected ValveFactory() {
    registry = new AssetRegistry<Valve>();
  }

  /**
   * Creates a new valve between junction
   * @param bus
   * @return
   */
  public Valve createNewValve() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Valve valve = new ValveImpl(assetId);
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
  protected void registerLegacy(String legacyTag, Object key, Valve asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Valve getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
  
}
