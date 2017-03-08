package gov.lanl.micot.infrastructure.model;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * An implementation of asset with common functionality
 * @author Russell Bent
 */
public abstract class AssetImpl implements Asset {

  protected Set<Object>          identifierKeys      = null;  
  protected Set<Object>          outputKeys          = null;
  private Map<Object,Object>     attributes          = null;

//  private Asset baseData                             = null;

  /**
   * Constructor
   * @param identifierKeys
   */
  public AssetImpl() {
    attributes = new HashMap<Object,Object>();
    identifierKeys = new HashSet<Object>();
    identifierKeys.add(Asset.ASSET_ID_KEY);
    outputKeys = new LinkedHashSet<Object>();
  }
  
  /**
   * Constructor
   * @param asset
   * @param identifierKeys
   */
//  public AssetImpl(Asset asset) {
  //  this();
   // baseData = asset;
  //}
  
  @Override
  public void setAttribute(Object key, Object object) { 
   // if (baseData != null && baseData.getAttribute(key) == null) {
     // baseData.setAttribute(key,object);
    //}
    //else {
      attributes.put(key,object);
    //}
  }
  
  @SuppressWarnings("unchecked")
  @Override
  public <E> E getAttribute(Object key, Class<E> cls) {
    Object obj = attributes.get(key);
//    if (obj == null && baseData != null) {
  //    obj = baseData.getAttribute(key);
   // }    
    if (obj instanceof Collection && !cls.isAssignableFrom(Collection.class) &&  !cls.isAssignableFrom(Set.class)) {
      return ((Collection<E>)obj).iterator().next();
    }    
    return (E) obj;
  }

  @Override
  public Object getAttribute(Object key) {
    //if (attributes.get(key) != null || baseData == null) {
      return attributes.get(key);
    //}
    //return baseData.getAttribute(key);
  }
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    for (Object key : identifierKeys) {
      Object obj = getAttribute(key);
      result = prime * result + ((obj == null) ? 0 : obj.hashCode());
    }
    return result;
  }

  @Override
  public String toString() {
    Set<Object> keys = (getOutputKeys().size() == 0) ? identifierKeys : getOutputKeys();    
    String string = "";
    for (Object key : keys) {
      string += getAttribute(key).toString() + " ";
    }    
    string = string.trim();
    return string;
  }
  
  @Override
  public boolean getDesiredStatus() {
    return getAttribute(DESIRED_STATUS_KEY,Boolean.class);
  }

  @Override
  public void setDesiredStatus(boolean status) {
    setAttribute(DESIRED_STATUS_KEY,status);
  }
  
  @Override
  public boolean getActualStatus() {
    return getAttribute(ACTUAL_STATUS_KEY, Boolean.class);
  }

  @Override
  public void setActualStatus(boolean status) {
    setAttribute(ACTUAL_STATUS_KEY,status);
  }

  @Override
  public Set<Object> getAttributeKeys() {
    HashSet<Object> a = new HashSet<Object>();
    a.addAll(attributes.keySet());
//    if (baseData != null) {
  //    a.addAll(baseData.getAttributeKeys());
   // }
    return a;
  }
  
  @SuppressWarnings("all")
  @Override
  public int compareTo(Asset arg0) {
    if (arg0 instanceof AssetImpl) {
      for (Object key : identifierKeys) {
        Comparable thisObj = getAttribute(key,Comparable.class);
        Comparable thatObj = arg0.getAttribute(key,Comparable.class);
        if (thisObj.compareTo(thatObj) != 0) {
          return thisObj.compareTo(thatObj);
        }
      }
      return getClass().toString().compareTo(arg0.getClass().toString());
    }
    return new Integer(hashCode()).compareTo(arg0.hashCode());
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!getClass().equals(obj.getClass())) {
      return false;
    }
    if (obj instanceof AssetImpl) {
      AssetImpl assetImpl = (AssetImpl)obj;
      for (Object key : identifierKeys) {
        if (!getAttribute(key).equals(assetImpl.getAttribute(key))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean isAttributeEqual(Object attribute, Object value) {
    return getAttribute(attribute).equals(value);
  }

  @Override
  public void addOutputKey(Object key) {
//    if (baseData == null) {
      outputKeys.add(key);
 //   }
  //  else {
    //  baseData.addOutputKey(key);
    //}
  }

  /**
   * Get the output keys
   * @return
   */
  public Set<Object> getOutputKeys() {
   // if (baseData == null) {
      return outputKeys;
   // }
   // else {
     // return baseData.getOutputKeys();
    //}
  }

  /**
   * Do a deep copy of an asset
   * @param asset
   * @throws InvocationTargetException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   * @throws IllegalArgumentException 
   * @throws NoSuchMethodException 
   * @throws SecurityException 
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  protected void deepCopy(Asset asset) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
    for (Object key : attributes.keySet()) {
      Object value = attributes.get(key);
      
      if (value instanceof Collection) {
        Collection<Object> collection = (Collection)value;
        Constructor<? extends Collection> constructor = collection.getClass().getConstructor();       
        value = constructor.newInstance();
        for (Object obj : collection) {
          ((Collection) value).add(obj);
        }        
      }
      else if (value instanceof Map) {
        Map<Object,Object> map = (Map)value;
        Constructor<? extends Map> constructor = map.getClass().getConstructor();       
        value = constructor.newInstance();
        for (Object k : map.keySet()) {
          ((Map) value).put(k,map.get(k));
        }
      }
            
      asset.setAttribute(key, value);
    }
    
    for (Object key : outputKeys) {
      asset.addOutputKey(key);
    }
  }
  
  /**
   * Get the base data
   * @return
   */
//  protected Asset getBaseData() {
  //  return baseData;
  //}

  @Override
  public abstract AssetImpl clone();
  
}
