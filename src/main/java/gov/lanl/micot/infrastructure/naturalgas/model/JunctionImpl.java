package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.ComponentImpl;

/**
 * Some common bus implementation information
 * 
 * @author Russell Bent
 */
public class JunctionImpl extends ComponentImpl implements Junction {

  protected static final long      serialVersionUID    = 1L;

  private Set<JunctionChangeListener> listeners           = null;

  /**
   * Constructor
   */
  protected JunctionImpl(long assetId) {
    super();
    listeners = new HashSet<JunctionChangeListener>();
    setAttribute(CityGate.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param battery
   */
//  public JunctionImpl(Junction junction) {
  //  super(junction);
   // listeners = new HashSet<JunctionChangeListener>();    
  //}
  
  @Override
  public void addJunctionChangeListener(JunctionChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeJunctionChangeListener(JunctionChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires a change in bus data
   */
  private void fireJunctionDataChangeEvent(Object attribute) {
    for (JunctionChangeListener listener : listeners) {
      listener.junctionDataChanged(this, attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireJunctionDataChangeEvent(key);
  }

  @Override
  public String getName() {
    return getAttribute(NAME_KEY,String.class);
  }

  @Override
  public double getMinimumPressure() {
    return getAttribute(MINIMUM_PRESSURE_KEY,Double.class);
  }

  @Override
  public void setMinimumPressure(double pressure) {
    setAttribute(MINIMUM_PRESSURE_KEY, pressure);
  }

  @Override
  public double getMaximumPressure() {
    return getAttribute(MAXIMUM_PRESSURE_KEY,Double.class);
  }

  @Override
  public void setMaximumPressure(double pressure) {
    setAttribute(MAXIMUM_PRESSURE_KEY, pressure);
  }

  @Override
  public double getInitialPressure() {
    return getAttribute(INITIAL_PRESSURE_KEY,Double.class);
  }

  @Override
  public void setInitialPressure(double pressure) {
    setAttribute(INITIAL_PRESSURE_KEY, pressure);
  }

  @Override
  public Number getPressure() {
    return getAttribute(PRESSURE_KEY,Double.class);
  }

  @Override
  public void setPressure(Number pressure) {
    setAttribute(PRESSURE_KEY, pressure);
  }
  
  @Override
  public JunctionImpl clone() {
//    JunctionImpl newJunction = new JunctionImpl((Junction)getBaseData());
    JunctionImpl newJunction = new JunctionImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newJunction);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newJunction;
  }
}
