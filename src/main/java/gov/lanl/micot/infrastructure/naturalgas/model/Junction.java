package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Interface for natural gas junctions
 * @author Russell Bent
 */
public interface Junction extends Component {
	
  public static final String MINIMUM_PRESSURE_KEY = "MINIMUM_PRESSURE";
  public static final String MAXIMUM_PRESSURE_KEY = "MAXIMUM_PRESSURE";
  public static final String INITIAL_PRESSURE_KEY = "INITIAL_PRESSURE";
  public static final String PRESSURE_KEY         = "PRESSURE";
  public static final String NAME_KEY             = "NAME";
  public static final String HEIGHT_KEY           = "HEIGHT";
  public static final String PRESSURE_UNIT_KEY    = "PRESSURE_UNIT";
  
	/**
	 * Make sure equals is implemented
	 */
	public boolean equals(Object obj);
	
	/**
	 * Make sure hashCode is implemented
	 */
	public int hashCode();
		
	/**
	 * Get the name
	 * @return
	 */
	public String getName();
	
	/**
	 * get the minimum pressure
	 * @return
	 */
	public double getMinimumPressure();
	
	/**
	 * Set the minimum pressure
	 * @param pressure
	 */
	public void setMinimumPressure(double pressure);
	
	/**
	 * Get the maximum pressure
	 * @return
	 */
	public double getMaximumPressure();
	
	/**
	 * Set the maximum pressure
	 * @param pressure
	 */
	public void setMaximumPressure(double pressure);
  
	/**
	 * Get the initial pressure
	 * @return
	 */
	public double getInitialPressure();
	
	/**
	 * Set the initial pressure
	 * @param pressure
	 */
	public void setInitialPressure(double pressure);
	
	/**
	 * Get the pressure
	 * @return
	 */
	public Number getPressure();

	/**
	 * Set the pressure
	 * @param pressure
	 */
	public void setPressure(Number pressure);
	
  /**
   * Add a bus data listener
   * @param listener
   */
  public void addJunctionChangeListener(JunctionChangeListener listener);
  
  /**
   * Remove a bus data listener
   * @param listener
   */
  public void removeJunctionChangeListener(JunctionChangeListener listener);

  /**
   * Clone routine
   * @return
   */
  public Junction clone();
  
	
}
