package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnectionImpl;

/**
 * Some common control valve implementation information
 * 
 * @author Russell Bent
 */
public class ControlValveImpl extends FlowConnectionImpl implements ControlValve {

  protected static final long             serialVersionUID    = 1L;

  private Set<ControlValveChangeListener> listeners           = null;

  /**
   * Constructor
   */
  protected ControlValveImpl(long assetId) {
    super();
    listeners = new HashSet<ControlValveChangeListener>();
    setAttribute(ControlValve.ASSET_ID_KEY, assetId);
  }

  /**
   * Constructor
   * @param compressor
   */
//  public ControlValveImpl(ControlValve valve) {
 //   super(valve);
  //  listeners = new HashSet<ControlValveChangeListener>();    
  //}
  
  @Override
  public void addControlValveChangeListener(ControlValveChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeControlValveChangeListener(ControlValveChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires a change in bus data
   */
  private void fireControlValveDataChangeEvent(Object attribute) {
    for (ControlValveChangeListener listener : listeners) {
      listener.controlValveDataChanged(this,attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireControlValveDataChangeEvent(key);
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
  public ControlValveImpl clone() {
//    ControlValveImpl newValve = new ControlValveImpl((ControlValve)getBaseData());
    ControlValveImpl newValve = new ControlValveImpl(getAttribute(ASSET_ID_KEY,Long.class));

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
  
  @Override
  public double getInitialCompressionRatio() {
    return getAttribute(INITIAL_COMPRESSION_RATIO_KEY, Double.class);
  }

  @Override
  public void setInitialCompressionRatio(double ratio) {
    setAttribute(INITIAL_COMPRESSION_RATIO_KEY, ratio);
  }

  @Override
  public double getMinimumCompressionRatio() {
    return getAttribute(MINIMUM_COMPRESSION_RATIO_KEY, Double.class);
  }

  @Override
  public void setMinimumCompressionRatio(double ratio) {
    setAttribute(MINIMUM_COMPRESSION_RATIO_KEY, ratio);
  }

  @Override
  public double getMaximumCompressionRatio() {
    return getAttribute(MAXIMUM_COMPRESSION_RATIO_KEY, Double.class);
  }

  @Override
  public void setMaximumCompressionRatio(double ratio) {
    setAttribute(MAXIMUM_COMPRESSION_RATIO_KEY, ratio);
  }

  @Override
  public Number getCompressionRatio() {
    return getAttribute(COMPRESSION_RATIO_KEY, Double.class);
  }

  @Override
  public void setCompressionRatio(Number ratio) {
    setAttribute(COMPRESSION_RATIO_KEY, ratio);
  }

  
}
