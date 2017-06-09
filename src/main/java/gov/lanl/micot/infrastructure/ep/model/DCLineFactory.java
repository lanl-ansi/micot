package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;

/**
 * Interface for creating DC Line factories
 * @author Russell Bent
 */
public abstract class DCLineFactory {

	private AssetRegistry<DCLine> registry = null;

	protected DCLineFactory() {
		registry = new AssetRegistry<DCLine>();
	}
	
	/**
	 * Creates a new line between buses
	 * @param bus
	 * @return
	 */
	public DCLine createNewTwoTerminalLine() {
		AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
		long assetId = modifier.getNextId();
		DCLine line = new DCTwoTerminalLine(assetId);
		registry.register(assetId, line);
		return registry.getCopy(assetId);
	}

	 /**
   * Creates a new line between buses
   * @param bus
   * @return
   */
  public DCLine createNewVoltageSourceLine() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    DCLine line = new DCVoltageSourceLine(assetId);
    registry.register(assetId, line);
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
	protected void registerLegacy(String legacyTag, Object key, DCLine asset) {
		registry.registerLegacy(legacyTag, key, asset);
	} 

	/**
	 * Get the legacy
	 * @param legacyTag
	 * @param key
	 * @return
	 */
	protected DCLine getLegacy(String legacyTag, Object key) {
		return registry.getLegacyCopy(legacyTag, key);
	}
}