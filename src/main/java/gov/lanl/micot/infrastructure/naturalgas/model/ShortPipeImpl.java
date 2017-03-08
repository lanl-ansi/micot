package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnectionImpl;

/**
 * Implementation of a short pipe
 * 
 * @author Russell Bent
 */
public class ShortPipeImpl extends FlowConnectionImpl implements ShortPipe {

  protected static final long       serialVersionUID                  = 1L;
  
  private Set<ShortPipeChangeListener> listeners                        = null;

  /**
   * Constructor
   */
  protected ShortPipeImpl(long assetId) {
    super();
    listeners = new HashSet<ShortPipeChangeListener>();
    setAttribute(Pipe.ASSET_ID_KEY, assetId);
  }
  
  /**
   * Constructor
   * @param battery
   */
//  public ShortPipeImpl(ShortPipe pipe) {
  //  super(pipe);
    //listeners = new HashSet<ShortPipeChangeListener>();    
  //}

  
  @Override
  public void addShortPipeChangeListener(ShortPipeChangeListener listener) {
    listeners.add(listener);
  }

  /**
   * Remove the line data listener
   * 
   * @param listener
   */
  public void removeShortPipeChangeListener(ShortPipeChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Notification that line data has changed
   */
  private void fireDataChangeEvent(Object attribute) {
    for (ShortPipeChangeListener listener : listeners) {
      listener.shortPipeDataChanged(this,attribute);
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
  public void setResistance(double resistance) {
    setAttribute(RESISTANCE_KEY, resistance);
  }

  @Override
  public double getResistance() {
    return getAttribute(RESISTANCE_KEY, Number.class).doubleValue();
  }

  @Override
  public ShortPipeImpl clone() {
    //ShortPipeImpl newPipe = new ShortPipeImpl((ShortPipe)getBaseData());
    ShortPipeImpl newPipe = new ShortPipeImpl(getAttribute(ASSET_ID_KEY,Long.class));
    
    try {
      deepCopy(newPipe);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newPipe;
  }

}
