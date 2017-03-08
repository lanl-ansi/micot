package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnectionImpl;

/**
 * Some common compressor implementation information
 * 
 * @author Russell Bent
 */
public class ValveImpl extends FlowConnectionImpl implements Valve {

  protected static final long             serialVersionUID    = 1L;

  private Set<ValveChangeListener> listeners           = null;

  /**
   * Constructor
   */
  protected ValveImpl(long assetId) {
    super();
    listeners = new HashSet<ValveChangeListener>();
    setAttribute(Valve.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param compressor
   */
 // public ValveImpl(Valve valve) {
   // super(valve);
   // listeners = new HashSet<ValveChangeListener>();    
  //}
  
  @Override
  public void addValveChangeListener(ValveChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeValveChangeListener(ValveChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires a change in bus data
   */
  private void fireValveDataChangeEvent(Object attribute) {
    for (ValveChangeListener listener : listeners) {
      listener.valveDataChanged(this,attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireValveDataChangeEvent(key);
  }

  @Override
  public void setLength(double length) {
    setAttribute(LENGTH_KEY, length);
  }
  
  @Override
  public double getLength() {
    return getAttribute(LENGTH_KEY, Number.class).doubleValue();
  }

  @Override
  public ValveImpl clone() {
    //ValveImpl newValve = new ValveImpl((Valve)getBaseData());
    ValveImpl newValve = new ValveImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newValve);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newValve;
  }

  @Override
  public void setResistance(double resistance) {
    setAttribute(RESISTANCE_KEY, resistance);       
  }

  @Override
  public double getResistance() {
    return getAttribute(RESISTANCE_KEY, Number.class).doubleValue();
  }

  @Override
  public void setDiameter(double diameter) {
    setAttribute(DIAMETER_KEY, diameter);       
  }

  @Override
  public double getDiameter() {
    return getAttribute(DIAMETER_KEY, Number.class).doubleValue();
  }
  
}
