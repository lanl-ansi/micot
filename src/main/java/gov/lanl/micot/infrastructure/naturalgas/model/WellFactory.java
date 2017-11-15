package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for generator factories
 * @author Russell Bent
 */
public abstract class WellFactory {
  
  private static AssetRegistry<Well> registry = null;
  
  /**
   * Constructor
   */
  protected WellFactory() {
    registry = new AssetRegistry<Well>();
  }
  
  /**
   * Create a well
   * @param bus
   * @return
   */
  public Well createNewWell() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Well well = new WellImpl(assetId);
    registry.register(assetId, well);
    return registry.getCopy(assetId);
  }

  /**
   * Initialize a well
   * @param generator
   * @param bus
   * @param maxMW
   * @param minMW
   * @param capacity
   */
  protected void initializeWell(Well well, Junction junction, double cfd) {
    String name = junction.getAttribute(Junction.NAME_KEY, String.class);
    well.setCoordinate(junction.getCoordinate());
    well.setStatus(true);
    well.setProduction(cfd);
    well.setAttribute(Well.WELL_NAME_KEY, name);
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
  protected void registerLegacy(String legacyTag, Object key, Well asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Well getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }
}
