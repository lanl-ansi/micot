package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnectionImpl;

/**
 * Some common resistor implementation information
 * 
 * @author Russell Bent
 */
public class ResistorImpl extends FlowConnectionImpl implements Resistor {

  protected static final long             serialVersionUID    = 1L;

  private Set<ResistorChangeListener> listeners           = null;

  /**
   * Constructor
   */
  protected ResistorImpl(long assetId) {
    super();
    listeners = new HashSet<ResistorChangeListener>();
    setAttribute(Valve.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param compressor
   */
//  public ResistorImpl(Resistor resistor) {
  //  super(resistor);
   // listeners = new HashSet<ResistorChangeListener>();    
 // }
  
  @Override
  public void addResistorChangeListener(ResistorChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeResistorChangeListener(ResistorChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires a change in bus data
   */
  private void fireResistorDataChangeEvent(Object attribute) {
    for (ResistorChangeListener listener : listeners) {
      listener.resistorDataChanged(this,attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireResistorDataChangeEvent(key);
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
  public ResistorImpl clone() {
//    ResistorImpl newResistor = new ResistorImpl((Resistor)getBaseData());
    ResistorImpl newResistor = new ResistorImpl(getAttribute(ASSET_ID_KEY,Long.class));

    try {
      deepCopy(newResistor);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newResistor;
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

  @Override
  public void setDragFactor(double drag) {
    setAttribute(DRAG_FACTOR_KEY, drag);       
  }

  @Override
  public double getDragFactor() {
    return getAttribute(DRAG_FACTOR_KEY, Number.class).doubleValue();
  }

}
