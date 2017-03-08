package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Abstract class for defining valves
 * @author Russell Bent
 *
 */
public interface Valve extends NaturalGasConnection {	
    
  public static final String NAME_KEY                       = "NAME";
  public static final String PRESSURE_DIFFERENTIAL_MAX_KEY  = "PRESSURE_DIFFERENTIAL_MAX";
  public static final String PRESSURE_DIFFERENTIAL_UNIT_KEY = "PRESSURE_DIFFERENTIAL_UNIT";
  
  public static final String PRESSURE_LOSS_IN_KEY           = "PRESSURE_LOSS_IN";
  public static final String PRESSURE_LOSS_OUT_KEY          = "PRESSURE_LOSS_OUT";
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void addValveChangeListener(ValveChangeListener listener);
  
  /**
   * Add a listener for data changes
   * @param listener
   */
  public void removeValveChangeListener(ValveChangeListener listener);
  
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
   * Clone the compressor
   * @return
   */
  public Valve clone();
}
