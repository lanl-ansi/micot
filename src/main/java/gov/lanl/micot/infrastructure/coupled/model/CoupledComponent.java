package gov.lanl.micot.infrastructure.coupled.model;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.ComponentImpl;

/**
 * A wrapper for a component that is part of a coupled system
 * @author Russell Bent
 *
 */
public class CoupledComponent extends ComponentImpl {
  
  private Asset asset = null;
  
  /**
   * Constructor
   * @param asset
   */
  public CoupledComponent(Asset asset) {
    this.asset = asset;
    if (asset == null) {
      System.err.println("Warning: adding a coupled component with a null asset");
    }
  }
  

  @Override
  public int hashCode() {
    return asset.hashCode();
  }

  @Override
  public String toString() {
    return asset.toString();
  }

  @SuppressWarnings("all")
  @Override
  public int compareTo(Asset arg0) {
    if (arg0 instanceof CoupledComponent) {
      return asset.compareTo(((CoupledComponent)arg0).asset);
    }
    return asset.compareTo(arg0);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof CoupledComponent) {
      return asset.equals(((CoupledComponent)obj).asset);
    }
    return asset.equals(obj);
  }

  /**
   * Get the asset
   * @return
   */
  public Asset getAsset() {
    return asset;
  }
  
  @Override
  public CoupledComponent clone() {
    return new CoupledComponent(asset.clone());
  }

  
}
