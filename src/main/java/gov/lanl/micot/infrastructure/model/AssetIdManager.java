package gov.lanl.micot.infrastructure.model;

/**
 * This is an interface for managing asset ids to ensure that they are all unique
 * @author Russell Bent
 *
 */
public abstract class AssetIdManager {

  /**
   * Get the next available id number
   * @return
   */
  public abstract  long getNextId();
  
  public static AssetIdManager getDefaultAssetIdManager() {
    return IncrementalAssetIdManager.getInstance();
  }
  
}
