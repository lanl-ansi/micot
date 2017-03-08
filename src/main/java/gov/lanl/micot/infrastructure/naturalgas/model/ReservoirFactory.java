package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for reservoir factories
 * @author Russell Bent
 */
public abstract class ReservoirFactory {

  private AssetRegistry<Reservoir> registry = null;
  
  /**
   * Constructor
   */
  protected ReservoirFactory() {
    registry = new AssetRegistry<Reservoir>();
  }
  
  /**
   * Create a new city gate
   * @param bus
   * @return
   */
  public Reservoir createNewReservoir() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Reservoir reservoir = new ReservoirImpl(assetId);
    registry.register(assetId, reservoir);
    return registry.getCopy(assetId);
  }
  
  /**
   * Initialize a reservoir
   * @param generator
   * @param bus
   * @param maxMW
   * @param minMW
   * @param capacity
   */
  protected void initializeReservoir(Reservoir reservoir, Junction junction, double cfd) {
    String name = junction.getAttribute(Junction.NAME_KEY, String.class);
    reservoir.setCoordinate(junction.getCoordinate());
    reservoir.setDesiredStatus(true);
    reservoir.setActualStatus(true);
    reservoir.setDesiredProduction(cfd);
    reservoir.setActualProduction(cfd);
    reservoir.setAttribute(Reservoir.RESERVOIR_NAME_KEY, name);
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
  protected void registerLegacy(String legacyTag, Object key, Reservoir asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Reservoir getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

}
