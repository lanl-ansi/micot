package gov.lanl.micot.infrastructure.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Configuration for asset changes
 * @author Russell Bent
 */
public class AssetModification {

  private String componentClass = null;
  private Map<String,Object> attributes = null;
  private Map<String,Object> keys = null;
    
  /**
   * Constructor
   */
  public AssetModification() {
    attributes = new LinkedHashMap<String,Object>();
    keys = new LinkedHashMap<String,Object>();
  }
  
  /**
   * @return the componentClass
   */
  public String getComponentClass() {
    return componentClass;
  }

  /**
   * @param componentClass the componentClass to set
   */
  public void setComponentClass(String componentClass) {
    this.componentClass = componentClass;
  }
  
  /**
   * Add an attribute
   * @param str
   */
  public void addAttribute(String str, Object value) {
    attributes.put(str, value);
  }
  
  /**
   * Add an attribute
   * @param str
   */
  public void addKey(String str, Object value) {
    keys.put(str, value);
  }

  /**
   * @return the attributes
   */
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  /**
   * @param attributes the attributes to set
   */
  public void setAttributes(Map<String, Object> attributes) {
    this.attributes = attributes;
  }

  /**
   * @return the keys
   */
  public Map<String, Object> getKeys() {
    return keys;
  }

  /**
   * @param keys the keys to set
   */
  public void setKeys(Map<String, Object> keys) {
    this.keys = keys;
  }
    
}
