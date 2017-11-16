package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.HashSet;
import java.util.Set;

import gov.lanl.micot.infrastructure.model.FlowConnectionImpl;

/**
 * Some common compressor implementation information
 * 
 * @author Russell Bent
 */
public class CompressorImpl extends FlowConnectionImpl implements Compressor {

  protected static final long             serialVersionUID    = 1L;

  private Set<CompressorChangeListener> listeners           = null;

  /**
   * Constructor
   */
  protected CompressorImpl(long assetId) {
    super();
    listeners = new HashSet<CompressorChangeListener>();
    setAttribute(Compressor.ASSET_ID_KEY, assetId);
  }

  @Override
  public void addCompressorChangeListener(CompressorChangeListener listener) {
    listeners.add(listener);
  }

  @Override
  public void removeCompressorChangeListener(CompressorChangeListener listener) {
    listeners.remove(listener);
  }

  /**
   * Fires a change in bus data
   */
  private void fireCompressorDataChangeEvent(Object attribute) {
    for (CompressorChangeListener listener : listeners) {
      listener.compressorDataChanged(this,attribute);
    }
  }
  
  @Override
  public void setAttribute(Object key, Object object) {
    if (key.equals("DESIRED_CONSUMPTION")) {
      System.err.println("Warning: DESIRED_CONSUMPTION is a deprecated attribute.  Using CONSUMPTION instead");
      key = CONSUMPTION_KEY;
    }
    if (key.equals("ACTUAL_CONSUMPTION")) {
      System.err.println("Warning: ACTUAL_CONSUMPTION is a deprecated attribute.  Using CONSUMPTION instead");
      key = CONSUMPTION_KEY;
    }
    
    super.setAttribute(key,object);
    fireCompressorDataChangeEvent(key);
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

  @Override
  public void setMinimiumPressure(double pressure) {
    setAttribute(MINIMUM_PRESSURE_KEY, pressure);    
  }

  @Override
  public double getMinimumPressure() {
    return getAttribute(MINIMUM_PRESSURE_KEY, Number.class).doubleValue();
  }

  @Override
  public void setMaximumPressure(double pressure) {
    setAttribute(MAXIMUM_PRESSURE_KEY, pressure);
  }

  @Override
  public double getMaximumPressure() {
    return getAttribute(MAXIMUM_PRESSURE_KEY, Number.class).doubleValue();
  }

  @Override
  public void setPressure(double pressure) {
    setAttribute(PRESSURE_KEY, pressure);
  }

  @Override
  public double getPressure() {
    return getAttribute(PRESSURE_KEY, Number.class).doubleValue();
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
  public CompressorImpl clone() {
    CompressorImpl newCompressor = new CompressorImpl(getAttribute(ASSET_ID_KEY,Long.class));
    try {
      deepCopy(newCompressor);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return newCompressor;
  }
  
  @Override
  public Object getAttribute(Object key) {
    if (key.equals("DESIRED_CONSUMPTION")) {
      System.err.println("Warning: DESIRED_CONSUMPTION is a deprecated attribute.  Using CONSUMPTION instead");
      key = CONSUMPTION_KEY;
    }
    if (key.equals("ACTUAL_CONSUMPTION")) {
      System.err.println("Warning: ACTUAL_CONSUMPTION is a deprecated attribute.  Using CONSUMPTION instead");
      key = CONSUMPTION_KEY;
    }    
    return super.getAttribute(key);
  }

  
}
