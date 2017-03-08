package gov.lanl.micot.infrastructure.model;

/**
 * This manages asset ids user a simple global counter
 * @author Russell Bent
 *
 */
public class IncrementalAssetIdManager extends AssetIdManager {

  private long nextId = 0;

  private static IncrementalAssetIdManager INSTANCE = null;
  
  public static synchronized IncrementalAssetIdManager getInstance() {
    if (INSTANCE == null) {
      INSTANCE = new IncrementalAssetIdManager();
    }
    return INSTANCE;
  }
  
  /**
   * Singleton constructor
   */
  private IncrementalAssetIdManager() {    
    nextId = 0;
  }
  
  @Override
  public synchronized long getNextId() {
    ++nextId;
    return nextId - 1;
  }
  
}
