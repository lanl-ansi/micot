package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Component;


/**
 * Abstract class for buses
 * @author Russell Bent
 */
public interface Bus extends Cloneable, Component {
	
  public static final String VOLTAGE_PU_KEY         = "VOLTAGE_PU";
  public static final String PHASE_ANGLE_KEY        = "PHASE_ANGLE";
  //if this bus controls another bus, this is the target voltage
  public static final String REMOTE_VOLTAGE_PU_KEY  = "REMOTE_VOLTAGE_PU";  
  public static final String SYSTEM_VOLTAGE_KV_KEY  = "SYSTEM_VOLTAGE_KV";
  public static final String NAME_KEY               = "NAME";
  public static final String MINIMUM_VOLTAGE_PU_KEY = "MINIMUM_VOLTAGE_PU";
  public static final String MAXIMUM_VOLTAGE_PU_KEY = "MAXIMUM_VOLTAGE_PU";
  public static final String VOLTAGE_PU_A_KEY       = "VOLTAGE_PU_A";
  public static final String VOLTAGE_PU_B_KEY       = "VOLTAGE_PU_B";
  public static final String VOLTAGE_PU_C_KEY       = "VOLTAGE_PU_C";
  public static final String PHASE_ANGLE_A_KEY      = "PHASE_ANGLE_A";
  public static final String PHASE_ANGLE_B_KEY      = "PHASE_ANGLE_B";
  public static final String PHASE_ANGLE_C_KEY      = "PHASE_ANGLE_C";
  
  public static double DEFAULT_MINIMUM_VOLTAGE             = .9;
  public static double DEFAULT_MAXIMUM_VOLTAGE             = 1.1;
    
	/**
	 * Make sure equals is implemented
	 */
	public boolean equals(Object obj);
	
	/**
	 * Make sure hashCode is implemented
	 */
	public int hashCode();
		
	/**
   * Get the desired voltage of a bus that is controlled by this bus
   * @return
   */
  public Double getRemoteVoltagePU();

  /**
   * Set the desired remote voltage if this bus controls another bus
   * @param pu
   */
  public void setRemoteVoltagePU(double pu);
    
  /**
   * Get the system voltage in kilovolts
   * @return
   */
  public double getSystemVoltageKV(); 

  /**
   * Set the system voltage
   * @param kv
   */
  public void setSystemVoltageKV(double kv);
  
  /**
   * Get the desired voltage of the bus
   * @return
   */
  public double getMaximumVoltagePU();
  
  /**
   * Sets the desired voltage
   * @param pu
   */
  public void setMaximumVoltagePU(double pu);

  /**
   * Get the desired voltage of the bus
   * @return
   */
  public double getMinimumVoltagePU();
  
  /**
   * Sets the desired voltage
   * @param pu
   */
  public void setMinimumVoltagePU(double pu);
  
  /**
   * Get the voltage of a bus in PUs
   * @return
   */
  public Number getVoltagePU();
  
  /**
   * Set the voltage of the bus
   * @param pu
   */
  public void setVoltagePU(Number pu);
  
  /**
   * Add a bus data listener
   * @param listener
   */
  public void addBusChangeListener(BusChangeListener listener);
  
  /**
   * Remove a bus data listener
   * @param listener
   */
  public void removeBusChangeListener(BusChangeListener listener);

  /**
   * set the phase angle
   * @param angle
   */
  public void setPhaseAngle(Number angle);
  
  /**
   * Get the phase angle
   * @return
   */
  public Number getPhaseAngle();

  /**
   * Clone a bus
   * @return
   */
	public Bus clone();
  
}
