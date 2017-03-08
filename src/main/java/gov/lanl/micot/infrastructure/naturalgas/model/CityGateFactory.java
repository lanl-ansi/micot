package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for factories that create city gaes
 * @author Russell Bent
 */
public abstract class CityGateFactory {

  private static AssetRegistry<CityGate> registry = null;
    
  protected CityGateFactory() {
    registry = new AssetRegistry<CityGate>();
  }
    
  /**
   * Create a new city gate
   * @param bus
   * @return
   */
  public CityGate createNewCityGate() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    CityGate gate = new CityGateImpl(assetId);
    registry.register(assetId, gate);
    return registry.getCopy(assetId);
  }

  /**
   * Initialize a gate
   * @param generator
   * @param bus
   * @param maxMW
   * @param minMW
   * @param capacity
   */
  protected void initializeGate(CityGate gate, Junction junction, double cfd) {
    String name = junction.getAttribute(Junction.NAME_KEY, String.class);
    gate.setCoordinate(junction.getCoordinate());
    gate.setDesiredStatus(true);
    gate.setActualStatus(true);
    gate.setDesiredConsumption(cfd);
    gate.setActualConsumption(cfd);
    gate.setAttribute(CityGate.CITYGATE_NAME_KEY, name);
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
  protected void registerLegacy(String legacyTag, Object key, CityGate asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected CityGate getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
