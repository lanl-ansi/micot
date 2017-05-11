package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;
import gov.lanl.micot.util.geometry.PointImpl;

/**
 * Interface for creating buses
 * @author Russell Bent
 */
public abstract class BusFactory {
  
  protected static final double DEFAULT_VOLTAGE = 1.0;
  protected static final double DEFAULT_MIN_VOLTAGE = 0.95;
  protected static final double DEFAULT_MAX_VOLTAGE = 1.05;
    
  private AssetRegistry<Bus> registry = null;

  /**
   * Constructor
   */
  protected BusFactory() {
    registry = new AssetRegistry<Bus>();
  }
  
  /**
   * This function creates a step down bus (in voltage) from another bus
   * @param stepDownBus
   * @return
   */
  public Bus createStepDownBus(Bus stepDownBus, double x, double y) {
    Bus bus = createEmptyBus();
    String name = bus.toString();
    double baseKV = getStepDown(stepDownBus);
    double voltageMagnitude = 0;
    double voltageAngle = 0;
    boolean status = true;
    
    bus.setAttribute(Bus.NAME_KEY,name);
    bus.setPhaseAngle(voltageAngle);
    bus.setSystemVoltageKV(baseKV);
    bus.setDesiredStatus(status);
    bus.setActualStatus(status);
    bus.setVoltagePU(voltageMagnitude);    
    bus.setCoordinate(new PointImpl(x,y));        
    return bus;
  }

  protected abstract Bus createEmptyBus();
  
  /**
   * This function determines a good step down for a bus
   * @return
   */
  protected double getStepDown(Bus stepDownBus) {
    double kv = stepDownBus.getSystemVoltageKV();

    if (kv > 765) {
      return 765;
    }
    
    if (kv > 500) {
      return 500;
    }

    
    if (kv > 345) {
      return 345;
    }
    
    if (kv > 230) {
      return 230;
    }
    
    if (kv > 161) {
      return 161;
    }
    
    if (kv > 138) {
      return 138;
    }
    
    if (kv > 115) {
      return 115;
    }
    
    
    if (kv > 69) {
      return 69;
    }

    if (kv > 34.5) {
      return 34.5;
    }

    if (kv > 24) {
      return 24;
    }

    if (kv > 12) {
      return 12;
    }

    return kv -1;        
  }
  
  /**
   * Create a new bus
   * @return
   */
  public Bus createNewBus() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Bus bus = new BusImpl(assetId);
    registry.register(assetId, bus);
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
  protected void registerLegacy(String legacyTag, Object key, Bus asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Bus getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }
}
