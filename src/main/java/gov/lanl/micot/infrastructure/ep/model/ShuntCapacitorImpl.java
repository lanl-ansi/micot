package gov.lanl.micot.infrastructure.ep.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ComponentImpl;

/**
 * Implementation of shunt capacitor
 * @author Russell Bent
 */
public class ShuntCapacitorImpl extends ComponentImpl implements ShuntCapacitor {

  protected static final long serialVersionUID = 1L;
  private Set<ShuntCapacitorChangeListener> listeners = null;

  /**
   * Constructor
   */
  protected ShuntCapacitorImpl(long assetId) {
    super();
    listeners = new HashSet<ShuntCapacitorChangeListener>();
    setAttribute(ShuntCapacitor.ASSET_ID_KEY, assetId);
  }
  
  /**
   * Constructor
   * @param bus
   */
//  public ShuntCapacitorImpl(ShuntCapacitor capacitor) {
  //  super(capacitor);
    //listeners = new HashSet<ShuntCapacitorChangeListener>();    
  //}
  
  @Override
  public double getReactiveCompensation() {
    return getAttribute(REACTIVE_COMPENSATION_KEY,Double.class);
  }

  @Override
  public double getRealCompensation() {
    return getAttribute(REAL_COMPENSATION_KEY,Double.class);
  }

  @Override
  public void setReactiveCompensation(double s) {
    setAttribute(REACTIVE_COMPENSATION_KEY,s);
  }

  @Override
  public void setRealCompensation(double s) {
    setAttribute(REAL_COMPENSATION_KEY,s);
  }
  
  @Override
  public void addShuntCapacitorChangeListener(ShuntCapacitorChangeListener listener) {
    listeners.add(listener);
  }
  
  @Override
  public void removeShuntCapacitorChangeListener(ShuntCapacitorChangeListener listener) {
    listeners.remove(listener);
  }
  
  /**
   * Notification that something in the data has changed
   */
  protected void fireShuntCapacitorDataChangeEvent(Object attribute) {
    for (ShuntCapacitorChangeListener listener : listeners) {
      listener.shuntCapacitorDataChanged(this,attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireShuntCapacitorDataChangeEvent(key);
  }

  @Override
  public ShuntCapacitorImpl clone() {
//    ShuntCapacitorImpl newCapacitor = new ShuntCapacitorImpl((ShuntCapacitor)getBaseData());
    ShuntCapacitorImpl newCapacitor = new ShuntCapacitorImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newCapacitor);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newCapacitor;
  }

  
}
