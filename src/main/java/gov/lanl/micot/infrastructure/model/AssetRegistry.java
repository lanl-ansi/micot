package gov.lanl.micot.infrastructure.model;

import java.lang.reflect.Constructor;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * A general class that manages the registry of assets and legacy ids
 * @author Russell Bent
 *
 */
public class AssetRegistry<E extends Asset> {

  private Map<Long, E> globalRegistry                = null;
  private Map<String, Map<Object, E>> legacyRegistry = null;
  
  public AssetRegistry() {
    globalRegistry = new LinkedHashMap<Long, E>();
    legacyRegistry = new LinkedHashMap<String, Map<Object, E>>();
  }

  /**
   * Register and asset with an id
   * @param assetId
   * @param asset
   */
  public void register(long assetId, E asset) {
    globalRegistry.put(assetId, asset);
  }

  /**
   * get a copy of the asset using reflection
   * @param assetId
   * @return
   */
  @SuppressWarnings("unchecked")
  public E getCopy(long assetId) {
    E asset = globalRegistry.get(assetId);
    return (E)((Asset)asset).clone();
//    return wrapAsset(asset);
  }

  /**
   * Does a legacy id exist in the table
   * @param legacyTag
   * @param key
   * @return
   */
  public boolean doesLegacyExist(String legacyTag, Object key) {
    if (legacyRegistry.get(legacyTag) == null) {
      return false;
    }
    if (legacyRegistry.get(legacyTag).get(key) == null) {
      return false;
    }
    return true;
  }

  /**
   * Register a legacy id
   * @param legacyTag
   * @param key
   * @param asset
   */
  public void registerLegacy(String legacyTag, Object key, E asset) {
    if (legacyRegistry.get(legacyTag) == null) {
      legacyRegistry.put(legacyTag, new LinkedHashMap<Object, E>());
    }
    legacyRegistry.get(legacyTag).put(key, globalRegistry.get(asset.getAttribute(Asset.ASSET_ID_KEY)));
  }

  /**
   * get the legacy id
   * @param legacyTag
   * @param key
   * @return
   */
  @SuppressWarnings("unchecked")
  public E getLegacyCopy(String legacyTag, Object key) {
    if (legacyRegistry.get(legacyTag) == null) {
      return null;
    }
    
    E asset = legacyRegistry.get(legacyTag).get(key);
    if (asset == null) {
      return null;
    }
//    return wrapAsset(asset);
    return (E)((Asset)asset).clone();
  }

  /**
   * Wrap the asset
   * @param asset
   * @return
   */
  @SuppressWarnings("unchecked")
  private E wrapAsset(E asset) {
    try {
      Constructor<? extends Asset> constructor = asset.getClass().getConstructor(asset.getClass().getInterfaces()[0]);
      return (E)constructor.newInstance(asset);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
  
}
