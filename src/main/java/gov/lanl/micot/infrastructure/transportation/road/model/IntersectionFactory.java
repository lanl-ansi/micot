package gov.lanl.micot.infrastructure.transportation.road.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating intersections
 * @author Russell Bent
 */
public abstract class IntersectionFactory {

  private AssetRegistry<Intersection> registry = null;

  /**
   * Constructor
   */
  protected IntersectionFactory() {
    registry = new AssetRegistry<Intersection>();    
  }
  
  /**
   * Create a new load
   * @param bus
   * @return
   */
  public Intersection createNewIntersection() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Intersection intersection = new IntersectionImpl(assetId);
    registry.register(assetId, intersection);
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
  protected void registerLegacy(String legacyTag, Object key, Intersection asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Intersection getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
