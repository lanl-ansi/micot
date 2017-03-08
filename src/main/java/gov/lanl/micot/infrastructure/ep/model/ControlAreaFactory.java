package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating control areas
 * @author Russell Bent
 */
public abstract class ControlAreaFactory {
  
  private AssetRegistry<ControlArea> registry = null;

  /**
   * Constructor
   */
  protected ControlAreaFactory() {
    registry = new AssetRegistry<ControlArea>(); 
  }
  
  /**
   * Create a new control area
   * @param bus
   * @return
   */
  public ControlArea createNewControlArea() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    ControlArea area = new ControlAreaImpl(assetId);
    registry.register(assetId, area);
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
  protected void registerLegacy(String legacyTag, Object key, ControlArea asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected ControlArea getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
