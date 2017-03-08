package gov.lanl.micot.infrastructure.model;

import gov.lanl.micot.infrastructure.model.Asset;

/**
 * Interface for an asset modifier
 * @author Russell Bent
 */
public interface AssetModifier {

  /**
   * Modify the asset
   * @param asset
   */
  public void modifyAsset(Asset asset);
  
  /**
   * Unmodify the asset
   * @param asset
   */
  public void unModifyAsset(Asset asset);
  
}
