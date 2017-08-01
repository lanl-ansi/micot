package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating three winding transformers
 * @author Russell Bent
 */
public abstract class ThreeWindingTransformerFactory {
  
  private AssetRegistry<ThreeWindingTransformer> registry = null;

  /**
   * Constructor
   */
  protected ThreeWindingTransformerFactory() {
    registry = new AssetRegistry<ThreeWindingTransformer>(); 
  }
  
  /**
   * Create a new control area
   * @param bus
   * @return
   */
  public ThreeWindingTransformer createNewThreeWindingTransformer() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    ThreeWindingTransformer transformer = new ThreeWindingTransformerImpl(assetId);
    registry.register(assetId, transformer);
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
  protected void registerLegacy(String legacyTag, Object key, ThreeWindingTransformer asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected ThreeWindingTransformer getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
