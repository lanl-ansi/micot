package gov.lanl.micot.infrastructure.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple class for modeling an opf scenario
 * @author Russell Bent
 *
 */
public class Scenario implements Comparable<Scenario> {
 
  private int index                = 0; 
  private double weight            = 0; 
  private Map<Asset, Collection<AssetModifierImpl>> scenarioModifications = null;
  
  /**
   * Constructor
   * @param model
   * @param index
   * @param weight
   */
  public Scenario(int index, double weight) {
    super();
    this.index = index;
    this.weight = weight;
    scenarioModifications = new HashMap<Asset, Collection<AssetModifierImpl>>();
  }

  /**
   * Get the index
   * @return
   */
  public int getIndex() {
    return index;
  }

  /**
   * get the weight
   * @return
   */
  public double getWeight() {
    return weight;
  }

  @Override
  public int compareTo(Scenario arg0) {
    int value = -new Double(weight).compareTo(arg0.weight);
    if (value == 0) {
      return new Integer(index).compareTo(arg0.index);
    }
    return value;
  }

  /**
   * Sets the weight
   * @param weight
   */
  public void setWeight(double weight) {
    this.weight = weight;
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + index;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Scenario other = (Scenario) obj;
    if (index != other.index)
      return false;
    return true;
  }
  
  @Override
  public String toString() {
    return index + "";
  }
  
  /**
   * Determine if a scenario has a modification or not
   * @param asset
   * @param attribute
   * @return
   */
  public boolean hasModification(Asset asset, String attribute) {
    if (scenarioModifications.get(asset) == null) {
      return false;
    }
    
    for (AssetModifierImpl impl : scenarioModifications.get(asset)) {
      if (impl.getAttribute().equals(attribute)) {
        return true;
      }
    }
    return false;
  }
  
  /**
   * Get the modification of an asset for an attribute
   * @param asset
   * @param attribute
   * @param cls
   * @return
   */
  @SuppressWarnings("unchecked")
  public <E> E getModification(Asset asset, String attribute, Class<E> cls) {
    if (scenarioModifications.get(asset) == null) {
      return null;
    }
    
    for (AssetModifierImpl impl : scenarioModifications.get(asset)) {
      if (impl.getAttribute().equals(attribute)) {
        return (E)impl.getNewValue();
      }
    }
    return null;    
  }

  /**
   * Custom function for computing the status of an asset
   * @param asset
   * @param currentStatus
   * @return
   */
  public boolean computeActualStatus(Asset asset, boolean currentStatus) {
    if (hasModification(asset, Asset.STATUS_KEY)) {
      currentStatus &= getModification(asset, Asset.STATUS_KEY,Boolean.class);
    }
  	
    if (hasModification(asset, Asset.IS_FAILED_KEY)) {
      currentStatus &= !getModification(asset, Asset.IS_FAILED_KEY,Boolean.class);
    }
    
    return currentStatus;
  }
  
  /**
   * Add an asset modifier
   * @param asset
   * @param impl
   */
  public void addModification(Asset asset, AssetModifierImpl impl) {
    if (scenarioModifications.get(asset) == null) {
      scenarioModifications.put(asset, new ArrayList<AssetModifierImpl>());
    }    
    scenarioModifications.get(asset).add(impl);
  }
  
  /**
   * Modify a model
   * @param model
   */
  public void modifyModel(Model model) {
    for (Asset a : scenarioModifications.keySet()) {
      Asset asset = model.getAsset(a);
      for (AssetModifierImpl impl : scenarioModifications.get(asset)) {
        impl.modifyAsset(asset);
      }
    }
  } 
}