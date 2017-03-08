package gov.lanl.micot.infrastructure.model;

/**
 * Definition of a control
 * @author Russell Bent
 */
public interface Control extends Cloneable {

  /**
   * Define the asset that is controlled
   * @return
   */
  public Asset getControlledAsset();
  
  /**
   * Define the asset that is controlling
   * @return
   */
  public Asset getControllingAsset();

  /**
   * Define the asset that is controlled
   * @return
   */
  public void setControlledAsset(Asset asset);
  
  /**
   * Define the asset that is controlling
   * @return
   */
  public void setControllingAsset(Asset asset);

  /**
   * Override of the clone method
   * @return
   */
  public Control clone();
  
}
