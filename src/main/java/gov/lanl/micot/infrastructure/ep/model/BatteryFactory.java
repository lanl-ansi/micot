package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;
import gov.lanl.micot.util.geometry.Point;

/**
 * Interface for battery factories
 * @author Russell Bent
 */
public abstract class BatteryFactory {

  protected GeneratorTypeEnum DEFAULT_TYPE = GeneratorTypeEnum.HOLD_GENERATION_TYPE;    

  private AssetRegistry<Battery> registry = null;

  /**
   * Constructor
   */
  protected BatteryFactory() {
    registry = new AssetRegistry<Battery>();    
  }
  	
  /**
   * Create a new battery
   * @param bus
   * @return
   */
  public Battery createNewBattery() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Battery battery = new BatteryImpl(assetId);
    registry.register(assetId, battery);
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
  protected void initializeBattery(Battery battery, Bus bus, double maxMW, double minMW, Number mw, double capacity, double used, double cost, Point point) {
    battery.setAttribute(Generator.TYPE_KEY, DEFAULT_TYPE);
    battery.setDesiredRealGeneration(mw);
    battery.setDesiredReactiveGeneration(0.0);
    battery.setActualRealGeneration(mw);
    battery.setActualReactiveGeneration(0.0);
    battery.setDesiredReactiveMax(0.0);
    battery.setReactiveMin(0.0);
    battery.setStatus(true);
    battery.setDesiredRealGenerationMax(maxMW);
    battery.setRealGenerationMin(minMW);
    battery.setCoordinate(point);
    battery.setUsedEnergyCapacity(used);
    battery.setAttribute(Battery.ECONOMIC_COST_KEY, cost);
    battery.setEnergyCapacity(capacity);
    battery.setMaximumProduction(maxMW);
    battery.setMinimumProduction(minMW);
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
  protected void registerLegacy(String legacyTag, Object key, Battery asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Battery getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
  
}
