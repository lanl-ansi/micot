package gov.lanl.micot.infrastructure.coupled.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating coupling factories
 * @author Russell Bent
 */
public abstract class CouplingFactory {

  private AssetRegistry<Coupling> registry = null;
  
  /**
   * Constructor
   */
  public CouplingFactory() {
    registry = new AssetRegistry<Coupling>();
  }
  	
  /**
   * Creates a new coupling
   * @param bus
   * @return
   */
  public Coupling createNewCoupling() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Coupling coupling = new CouplingImpl(assetId);
    registry.register(assetId, coupling);
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
  protected void registerLegacy(String legacyTag, Object key, Coupling asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Coupling getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
