package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.AssetIdManager;
import gov.lanl.micot.infrastructure.model.AssetRegistry;
import gov.lanl.micot.util.geometry.Point;

/**
 * Interface for generator factories
 * @author Russell Bent
 */
public abstract class GeneratorFactory {

  private AssetRegistry<Generator> registry = null;

  private static final double DEFAULT_MVA_BASE        = 100;
  protected static final double DEFAULT_STARTUP_COST = 0.0;
  protected static final double DEFAULT_SHUTDOWN_COST = 0.0;
  
  /**
   * Constructor
   */
  protected GeneratorFactory() {
    registry = new AssetRegistry<Generator>();
  }
  
  /**
   * Create a new generator
   * @param bus
   * @return
   */
  public Generator createNewGenerator() {
    AssetIdManager modifier = AssetIdManager.getDefaultAssetIdManager();
    long assetId = modifier.getNextId();
    Generator generator = new GeneratorImpl(assetId);
    registry.register(assetId, generator);
    return registry.getCopy(assetId);    
  }

  /**
   * Initialize a generator
   * @param generator
   * @param bus
   * @param maxMW
   * @param minMW
   * @param capacity
   */
  protected void initializeGenerator(Generator generator, Bus bus, double maxMW, double maxMVar, double minMW, double minMVar, Point point) {
    generator.setCoordinate(point);
    GeneratorTypeEnum type = GeneratorTypeEnum.HOLD_GENERATION_TYPE;    
    generator.setType(type);    
    generator.setRealGeneration(maxMW);
    generator.setReactiveGeneration(maxMVar);
    generator.setReactiveGenerationMax(maxMVar);
    generator.setReactiveGenerationMin(minMVar);
    generator.setStatus(true);
    generator.setRealGenerationMax(maxMW);
    generator.setRealGenerationMin(minMW);
    generator.setAttribute(Generator.MVA_BASE_KEY, DEFAULT_MVA_BASE);
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
  protected void registerLegacy(String legacyTag, Object key, Generator asset) {
    registry.registerLegacy(legacyTag, key, asset);
  } 
  
  /**
   * Get the legacy
   * @param legacyTag
   * @param key
   * @return
   */
  protected Generator getLegacy(String legacyTag, Object key) {
    return registry.getLegacyCopy(legacyTag, key);
  }


  
}
