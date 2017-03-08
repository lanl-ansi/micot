package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating resistors
 * @author Russell Bent
 */
public abstract class ResistorFactory {

  private AssetRegistry<Resistor> registry = null;
  
  /**
   * Constructor
   */
  protected ResistorFactory() {
    registry = new AssetRegistry<Resistor>();
  }

  /**
   * Creates a new valve between junction
   * @param bus
   * @return
   */
  public Resistor createNewResistor() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Resistor resistor = new ResistorImpl(assetId);
    registry.register(assetId, resistor);
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
  protected void registerLegacy(String legacyTag, Object key, Resistor asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Resistor getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }

  
  
}
