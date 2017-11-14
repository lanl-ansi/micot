package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating junctions
 * @author Russell Bent
 */
public abstract class JunctionFactory {
  
  private AssetRegistry<Junction> registry = null;
  
  /**
   * Constructor
   */
  protected JunctionFactory() {
    registry = new AssetRegistry<Junction>();
  }
  
  /**
   * Create a new city gate
   * @param bus
   * @return
   */
  public Junction createNewJunction() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Junction junction = new JunctionImpl(assetId);
    registry.register(assetId, junction);
    return registry.getCopy(assetId);
  }

  /**
   * Initialize a junction
   * @param generator
   * @param bus
   * @param maxMW
   * @param minMW
   * @param capacity
   */
  protected void initializeJunction(Junction junction) {
    junction.setStatus(true);
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
  protected void registerLegacy(String legacyTag, Object key, Junction asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Junction getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

}
