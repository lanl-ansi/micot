package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.ConsumerImpl;
import gov.lanl.micot.util.math.MathUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * An implementation of the load interface
 * @author Russell Bent
 */
public class LoadImpl extends ConsumerImpl implements Load {

  private Set<LoadChangeListener> listeners = null;
  
  /**
   * Constructor
   */
  protected LoadImpl(long assetId) {
    super();
    listeners = new HashSet<LoadChangeListener>();
    setAttribute(Load.ASSET_ID_KEY, assetId);
  }

  @Override
  public Number getConsumption() {
    return getLoadNorm();
  }

  @Override
  public void setConsumption(Number consumption) {
    setRealLoad(consumption.doubleValue());
    setReactiveLoad(0.0);    
  }
    
  @Override
  public Number getRealLoad() {
    return getAttribute(REAL_LOAD_KEY,Number.class);
  }

  @Override
  public void setRealLoad(Number realLoad) {
    setAttribute(REAL_LOAD_KEY,realLoad);
  }

  @Override
  public Number getReactiveLoad() {
    return getAttribute(REACTIVE_LOAD_KEY,Number.class);
  }

  @Override
  public void setReactiveLoad(Number reactiveLoad) {
    setAttribute(REACTIVE_LOAD_KEY,reactiveLoad);
  }
  
  @Override
  public void addLoadChangeListener(LoadChangeListener listener) {
    listeners.add(listener);
  }
  
  @Override
  public void removeLoadChangeListener(LoadChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fire the data change event
   */
  private void fireDataChangeEvent(Object attribute) {
    for (LoadChangeListener listener : listeners) {
      listener.loadDataChanged(this,attribute);
    }
  }

  @Override
  public Number getLoadNorm() {
    return MathUtils.SIGNED_NORM(getRealLoad(), getReactiveLoad());
  }

  @Override
  public void setAttribute(Object key, Object object) {
    if (key.equals("ACTUAL_REACTIVE_LOAD")) {
      System.err.println("Warning: ACTUAL_REACTIVE_LOAD is a deprecated attribute.  Using REACTIVE_LOAD instead");
      key = REACTIVE_LOAD_KEY;
    }
    if (key.equals("ACTUAL_REAL_LOAD")) {
      System.err.println("Warning: ACTUAL_REAL_LOAD is a deprecated attribute.  Using REAL_LOAD instead");
      key = REAL_LOAD_KEY;
    }
    if (key.equals("DESIRED_REACTIVE_LOAD")) {
      System.err.println("Warning: DESIRED_REACTIVE_LOAD is a deprecated attribute.  Using REACTIVE_LOAD instead");
      key = REACTIVE_LOAD_KEY;
    }
    if (key.equals("DESIRED_REAL_LOAD")) {
      System.err.println("Warning: DESIRED_REAL_LOAD is a deprecated attribute.  Using REAL_LOAD instead");
      key = REAL_LOAD_KEY;
    }
    
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  }
  
  @Override
  public LoadImpl clone() {
    LoadImpl newLoad = new LoadImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newLoad);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newLoad;
  }
    
  
  @Override
  public Object getAttribute(Object key) {
    if (key.equals("ACTUAL_REACTIVE_LOAD")) {
      System.err.println("Warning: ACTUAL_REACTIVE_LOAD is a deprecated attribute.  Using REACTIVE_LOAD instead");
      key = REACTIVE_LOAD_KEY;
    }
    if (key.equals("ACTUAL_REAL_LOAD")) {
      System.err.println("Warning: ACTUAL_REAL_LOAD is a deprecated attribute.  Using REAL_LOAD instead");
      key = REAL_LOAD_KEY;
    }
    if (key.equals("DESIRED_REACTIVE_LOAD")) {
      System.err.println("Warning: DESIRED_REACTIVE_LOAD is a deprecated attribute.  Using REACTIVE_LOAD instead");
      key = REACTIVE_LOAD_KEY;
    }
    if (key.equals("DESIRED_REAL_LOAD")) {
      System.err.println("Warning: DESIRED_REAL_LOAD is a deprecated attribute.  Using REAL_LOAD instead");
      key = REAL_LOAD_KEY;
    }
    return super.getAttribute(key);
  }
   
  @Override
  public double getReactiveLoadMax() {
    return getAttribute(REACTIVE_LOAD_MAX_KEY, Double.class);
  }
  
  @Override
  public double getReactiveLoadMin() {
    return getAttribute(REACTIVE_LOAD_MIN_KEY, Double.class);    
  }
  
  @Override
  public double getRealLoadMax() {
    return getAttribute(REAL_LOAD_MAX_KEY, Double.class);
  }
  
  @Override
  public double getRealLoadMin() {
    return getAttribute(REAL_LOAD_MIN_KEY, Double.class);
  }
    
  @Override
  public void setReactiveLoadMax(double reactive) {
    setAttribute(REACTIVE_LOAD_MAX_KEY, reactive);
  }
  
  @Override
  public void setReactiveLoadMin(double reactive) {
    setAttribute(REACTIVE_LOAD_MIN_KEY, reactive);
  }
  
  @Override
  public void setRealLoadMax(double real) {
    setAttribute(REAL_LOAD_MAX_KEY, real);
  }
  
  @Override
  public void setRealLoadMin(double real) {
    setAttribute(REAL_LOAD_MAX_KEY, real);
  }
}
