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
    return getDesiredLoadNorm();
  }

  @Override
  public void setConsumption(Number consumption) {
    setDesiredRealLoad(consumption.doubleValue());
    setDesiredReactiveLoad(0.0);    
  }
    
  @Override
  public Number getDesiredRealLoad() {
    return getAttribute(DESIRED_REAL_LOAD_KEY,Number.class);
  }

  @Override
  public void setDesiredRealLoad(Number realLoad) {
    setAttribute(DESIRED_REAL_LOAD_KEY,realLoad);
  }

  @Override
  public Number getDesiredReactiveLoad() {
    return getAttribute(DESIRED_REACTIVE_LOAD_KEY,Number.class);
  }

  @Override
  public void setDesiredReactiveLoad(Number reactiveLoad) {
    setAttribute(DESIRED_REACTIVE_LOAD_KEY,reactiveLoad);
  }

  @Override
  public Number getActualRealLoad() {
    return getAttribute(ACTUAL_REAL_LOAD_KEY,Number.class);
  }

  @Override
  public void setActualRealLoad(Number realLoad) {
    setAttribute(ACTUAL_REAL_LOAD_KEY,realLoad);
  }

  @Override
  public Number getActualReactiveLoad() {
    return getAttribute(ACTUAL_REACTIVE_LOAD_KEY,Number.class);
  }

  @Override
  public void setActualReactiveLoad(Number reactiveLoad) {
    setAttribute(ACTUAL_REACTIVE_LOAD_KEY,reactiveLoad);
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
  public Number getDesiredLoadNorm() {
    return MathUtils.SIGNED_NORM(getDesiredRealLoad(), getDesiredReactiveLoad());
  }

  /**
   * Get the actual load norm
   * @return
   */
  public Number getActualLoadNorm() {
    return MathUtils.SIGNED_NORM(getActualRealLoad(), getActualReactiveLoad());
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
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
}
