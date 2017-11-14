package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating shunt capacitor factories
 * @author Russell Bent
 *
 */
public abstract class ShuntCapacitorFactory {

  private AssetRegistry<ShuntCapacitor> registry = null;
  
  /**
   * Constructor
   */
  protected ShuntCapacitorFactory() {
    registry = new AssetRegistry<ShuntCapacitor>();
  }
  
  /**
   * Create a new generator
   * @param bus
   * @return
   */
  public ShuntCapacitor createNewShuntCapacitor() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    ShuntCapacitor capacitor = new ShuntCapacitorImpl(assetId);
    registry.register(assetId, capacitor);
    return registry.getCopy(assetId);
  }

  /**
   * Initialize a capacitor
   * @param generator
   * @param bus
   * @param maxMW
   * @param minMW
   * @param capacity
   */
  protected void initializeCapacitor(ShuntCapacitor capacitor, Bus bus, double bs, double gs) {
    capacitor.setCoordinate(bus.getCoordinate());
    capacitor.setStatus(true);
    capacitor.setRealCompensation(gs);
    capacitor.setReactiveCompensation(bs);
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
  protected void registerLegacy(String legacyTag, Object key, ShuntCapacitor asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected ShuntCapacitor getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
  
}
