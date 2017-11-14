package gov.lanl.micot.infrastructure.model;

import java.util.Set;

import gov.lanl.micot.util.geometry.Geometry;

/**
 * An interface for conveniently identifying infrastructure assets. key features
 * of assets is that they can have desired and actual status
 * 
 * @author 236322
 */
public interface Asset extends Comparable<Asset>, Cloneable {

  public static final String STATUS_KEY                  = "STATUS";
  public static final String COORDINATE_KEY              = "COORDINATE";
  public static final String ASSET_ID_KEY                = "ASSET_ID";
  public static final String IS_FAILED_KEY               = "DISABLED";

  /**
   * Get the on/off status of the asset
   * 
   * @return
   */
  public boolean getStatus();

  /**
   * Set the on/off status of the asset
   * 
   * @param b
   */
  public void setStatus(boolean b);

  /**
   * Routine for setting a custom attribute in the component
   * @param object
   */
  public void setAttribute(Object key, Object object);
  
  /**
   * Routine for getting a custom attribute
   * @param key
   * @return
   */
  public <E> E getAttribute(Object key, Class<E> cls);
  
  /**
   * Routine for getting a custom attribute
   * @param key
   * @return
   */
  public Object getAttribute(Object key);
  
  /**
   * Get the attribute key sets
   * @return
   */
  public Set<Object> getAttributeKeys();
    
  /**
   * Get the output keys
   * @return
   */
  public Set<Object> getOutputKeys();

  /**
   * Determine if and attribute is the same as a value
   * @param attribute
   * @param value
   * @return
   */
  public boolean isAttributeEqual(Object attribute, Object value);

  /**
   * Gets the geometry of an asset
   * @return
   */
  public Geometry getGeometry();

  /**
   * Add a key that is used for outputting information
   * @param key
   */
  public void addOutputKey(Object key);
  
  public Asset clone();
  
}
