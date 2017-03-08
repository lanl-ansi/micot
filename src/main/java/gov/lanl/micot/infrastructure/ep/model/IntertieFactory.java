package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for methods for creating interties
 * @author Russell Bent
 */
public abstract class IntertieFactory {

  private AssetRegistry<Intertie> registry = null;
  
  /**
   * Constructor
   */
  protected IntertieFactory() {
    registry = new AssetRegistry<Intertie>();
  }
  
  /**
   * Creates a new line between buses
   * @param bus
   * @return
   */
  public Intertie createNewIntertie() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Intertie intertie = new IntertieImpl(assetId);
    registry.register(assetId, intertie);
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
  protected void registerLegacy(String legacyTag, Object key, Intertie asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Intertie getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

}
