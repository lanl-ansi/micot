package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.Consumer;

/**
 * Abstract class for defining loads
 * @author Russell Bent
 *
 */
public interface Compressor extends NaturalGasConnection {	
  
  public static final String INITIAL_COMPRESSION_RATIO_KEY = "INITIAL_COMPRESSION_RATIO";
  public static final String COMPRESSION_RATIO_KEY         = "COMPRESSION_RATIO";
  public static final String MINIMUM_COMPRESSION_RATIO_KEY = "MINIMUM_COMPRESSION_RATIO";
  public static final String MAXIMUM_COMPRESSION_RATIO_KEY = "MAXIMUM_COMPRESSION_RATIO";
  public static final String ACTUAL_CONSUMPTION_KEY        = Consumer.ACTUAL_CONSUMPTION_KEY;
  public static final String DESIRED_CONSUMPTION_KEY       = Consumer.DESIRED_CONSUMPTION_KEY;  
  public static final String MINIMUM_PRESSURE_KEY          = Pipe.MINIMUM_PRESSURE_KEY;
  public static final String MAXIMUM_PRESSURE_KEY          = Pipe.MAXIMUM_PRESSURE_KEY;
  public static final String PRESSURE_KEY                  = Pipe.PRESSURE_KEY;
  public static final String DIAMETER_KEY                  = Pipe.DIAMETER_KEY;
  public static final String RESISTANCE_KEY                = Pipe.RESISTANCE_KEY;
  public static final String NAME_KEY                      = "NAME";
  public static final String DIAMETER_OUT_KEY              = "DIAMETER_OUT";
  public static final String DIAMETER_OUT_UNIT_KEY         = "DIAMETER_OUT_UNIT";
  public static final String DRAG_FACTOR_IN_KEY            = "DRAG_FACTOR_IN";
  public static final String DRAG_FACTOR_OUT_KEY           = "DRAG_FACTOR_OUT";
  public static final String GAS_COOLER_EXISTING_KEY       = "GAS_COOLER_EXISTING";
  public static final String INTERNAL_BYPASS_REQUIRED_KEY  = "INTERNAL_BYPASS_REQUIRED";

  /**
   * Add a listener for data changes
   * @param listener
   */
  public void addCompressorChangeListener(CompressorChangeListener listener);
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void removeCompressorChangeListener(CompressorChangeListener listener);
  
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
   * set the minimum pressure
   * @param pressure
   */
  public void setMinimiumPressure(double pressure);
  
  /**
   * get the minimum pressure
   * @return
   */
  public double getMinimumPressure();

  /**
   * set the maximum pressure
   * @param pressure
   */
  public void setMaximumPressure(double pressure);
  
  /**
   * get the maximum pressure
   * @return
   */
  public double getMaximumPressure();

  /**
   * Set the pressure
   * @param pressure
   */
  public void setPressure(double pressure);
  
  /**
   * Get the pressure
   * @return
   */
  public double getPressure();

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
   * Set the roughness
   * @param roughness
   */
  public void setResistance(double resistance);
  
  /**
   * Get the roughness
   * @return
   */
  public double getResistance();

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
   * Clone the compressor
   * @return
   */
  public Compressor clone();
}
