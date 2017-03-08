package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnectionImpl;

/**
 * Implementation of a line
 * 
 * @author Russell Bent
 */
public class PipeImpl extends FlowConnectionImpl implements Pipe {

  protected static final long       serialVersionUID                  = 1L;
  
  private Set<PipeChangeListener> listeners                        = null;

  /**
   * Constructor
   */
  protected PipeImpl(long assetId) {
    super();
    listeners = new HashSet<PipeChangeListener>();
    setAttribute(Pipe.ASSET_ID_KEY, assetId);
  }
  
  /**
   * Constructor
   * @param battery
   */
//  public PipeImpl(Pipe pipe) {
  //  super(pipe);
   // listeners = new HashSet<PipeChangeListener>();    
  //}

  
  @Override
  public void addPipeChangeListener(PipeChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Remove the line data listener
   * 
   * @param listener
   */
  public void removePipeChangeListener(PipeChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notification that line data has changed
   */
  private void fireDataChangeEvent(Object attribute) {
    for (PipeChangeListener listener : listeners) {
      listener.pipeDataChanged(this,attribute);
    }
  }

  @Override
  public void setAttribute(Object key, Object object) {
    super.setAttribute(key,object);
    fireDataChangeEvent(key);
  }

  @Override
  public void setMinimiumPressure(double pressure) {
    setAttribute(MINIMUM_PRESSURE_KEY, pressure);
  }

  @Override
  public double getMinimumPressure() {
    return getAttribute(MINIMUM_PRESSURE_KEY, Double.class);
  }

  @Override
  public void setMaximumPressure(double pressure) {
    setAttribute(MAXIMUM_PRESSURE_KEY, pressure);
  }

  @Override
  public double getMaximumPressure() {
    return getAttribute(MAXIMUM_PRESSURE_KEY, Double.class);
  }

  @Override
  public void setPressure(double pressure) {
    setAttribute(PRESSURE_KEY, pressure);
  }

  @Override
  public double getPressure() {
    return getAttribute(PRESSURE_KEY, Double.class);
  }

  @Override
  public void setDiameter(double diameter) {
    setAttribute(DIAMETER_KEY, diameter);
  }

  @Override
  public double getDiameter() {
    return getAttribute(DIAMETER_KEY, Double.class);
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
  public void setLength(double length) {
    setAttribute(LENGTH_KEY, length);
  }

  @Override
  public double getLength() {
    return getAttribute(LENGTH_KEY, Number.class).doubleValue();
  }

  @Override
  public PipeImpl clone() {
    //PipeImpl newPipe = new PipeImpl((Pipe)getBaseData());
    PipeImpl newPipe = new PipeImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newPipe);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newPipe;
  }
}
