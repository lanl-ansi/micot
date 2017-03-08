package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for factories that create loads
 * @author Russell Bent
 */
public abstract class LoadFactory {

  private AssetRegistry<Load> registry = null;
  
  /**
   * Constructor
   */
  public LoadFactory() {
    registry = new AssetRegistry<Load>();
  }
  
  /**
   * Create a load for a bus
   * @param newBus
   * @param realLoad
   * @param reactiveLoad
   * @return
   */
  public Load createLoad(Bus newBus, double realLoad, double reactiveLoad) {
    Load load = createEmptyLoad(newBus);
    initializeLoad(load, newBus, realLoad, reactiveLoad);
    return load;        
  }

  /**
   * Creates an empty load to fill up with information
   * @return
   */
  protected abstract Load createEmptyLoad(Bus bus);
  
  /**
   * Create a new load
   * @param bus
   * @return
   */
  public Load createNewLoad() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Load load = new LoadImpl(assetId);
    registry.register(assetId, load);
    return registry.getCopy(assetId);
  }

  /**
   * Initialize a load
   * @param generator
   * @param bus
   * @param maxMW
   * @param minMW
   * @param capacity
   */
  protected void initializeLoad(Load load, Bus bus, double mw, double mvar) {
    String name = bus.getAttribute(Bus.NAME_KEY, String.class);
    load.setCoordinate(bus.getCoordinate());
    load.setDesiredStatus(true);
    load.setActualStatus(true);
    load.setDesiredRealLoad(mw);
    load.setDesiredReactiveLoad(mvar);
    load.setActualRealLoad(mw);
    load.setActualReactiveLoad(mvar);
    load.setAttribute(Load.LOAD_NAME_KEY, name);
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
  protected void registerLegacy(String legacyTag, Object key, Load asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Load getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
}
