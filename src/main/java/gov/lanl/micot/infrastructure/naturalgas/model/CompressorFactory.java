package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating compressors
 * @author Russell Bent
 */
public abstract class CompressorFactory {

  private AssetRegistry<Compressor> registry = null;
  
  protected CompressorFactory() {
    registry = new AssetRegistry<Compressor>();
  }

  /**
   * Creates a new compressor between junction
   * @param bus
   * @return
   */
  public Compressor createNewCompressor() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Compressor compressor = new CompressorImpl(assetId);
    registry.register(assetId, compressor);
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
  protected void registerLegacy(String legacyTag, Object key, Compressor asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Compressor getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
  
}
