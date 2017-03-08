package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Abstract class for defining valves
 * @author Russell Bent
 *
 */
public interface ControlValve extends NaturalGasConnection {	
    
  public static final String NAME_KEY                       = "NAME";
  public static final String PRESSURE_DIFFERENTIAL_MAX_KEY  = "PRESSURE_DIFFERENTIAL_MAX";
  public static final String PRESSURE_DIFFERENTIAL_MIN_KEY  = "PRESSURE_DIFFERENTIAL_MIN";  
  public static final String PRESSURE_DIFFERENTIAL_UNIT_KEY = "PRESSURE_DIFFERENTIAL_UNIT";
  public static final String INTERNAL_BYPASS_REQUIRED_KEY   = "INTERNAL_BYPASS_REQUIRED";
  public static final String GAS_PREHEATER_EXISTING_KEY     = "GAS_PREHEATER_EXISTING";
  
  public static final String PRESSURE_LOSS_IN_KEY           = "PRESSURE_LOSS_IN";
  public static final String PRESSURE_LOSS_OUT_KEY          = "PRESSURE_LOSS_OUT";
  public static final String PRESSURE_LOSS_UNIT_KEY         = "PRESSURE_LOSS_UNIT";
  
  public static final String MINIMUM_COMPRESSION_RATIO_KEY = "MINIMIUM_COMPRESSION_RATIO";
  public static final String MAXIMUM_COMPRESSION_RATIO_KEY = "MAXIMIUM_COMPRESSION_RATIO";
  public static final String COMPRESSION_RATIO_KEY         = "COMPRESSION_RATIO";
  public static final String INITIAL_COMPRESSION_RATIO_KEY         = "INITIAL_COMPRESSION_RATIO";
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void addControlValveChangeListener(ControlValveChangeListener listener);
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void removeControlValveChangeListener(ControlValveChangeListener listener);
  
  /**
   * set the diameter
   * @param diameter
   */
  public void setDiameter(double diameter);
  
  /**
   * get the diameter
   * @return
   */
  public double getDiameter();

  /**
   * Set the length of the pipe
   * @param length
   */
  public void setLength(double length);
  
  /**
   * Get the length of the pipe
   * @return
   */
  public double getLength();
  
  /**
   * Get the initial compression ratio
   * @return
   */
  public double getInitialCompressionRatio();
  
  /**
   * Set the initial compression ratio
   */
  public void setInitialCompressionRatio(double ratio);
  
  /**
   * Get the minimum compression ratio
   * @return
   */
  public double getMinimumCompressionRatio();
  
  /**
   * set the minimum compression ratio
   */
  public void setMinimumCompressionRatio(double ratio);
  
  /**
   * Get the maximum compression ratio
   * @return
   */
  public double getMaximumCompressionRatio();
  
  /**
   * Set the maximum compression ratio
   */
  public void setMaximumCompressionRatio(double ratio);
  
  /**
   * Get the compression ratio
   * @return
   */
  public Number getCompressionRatio();
  
  /**
   * Set the compression ratio
   */
  public void setCompressionRatio(Number ratio);

  
  /**
   * Clone the compressor
   * @return
   */
  public ControlValve clone();
}
