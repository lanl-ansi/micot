package gov.lanl.micot.infrastructure.naturalgas.model;

/**
 * Interface for power lines --> Corrected: for natural gas pipelines
 * @author Russell Bent
 * Slightly modified by Conrado - Sep 2014.
 */
public interface Pipe extends NaturalGasConnection {	
	public static final String MINIMUM_PRESSURE_KEY          = "MINIMUM_PRESSURE";
	public static final String MAXIMUM_PRESSURE_KEY          = "MAXIMUM_PRESSURE";
	public static final String PRESSURE_KEY                  = "PRESSURE";
	public static final String NAME_KEY                      = "NAME";
  public static final String ROUGHNESS_KEY                 = "ROUGHNESS";
  public static final String PRESSURE_MAX_KEY              = "PRESSURE_MAX";
  public static final String HEAT_TRANSFER_COEFFICIENT_KEY = "HEAT_TRANSFER_COEFFICIENT";

  public static final String LENGTH_UNIT_KEY                    = "LENGTH_UNIT";
  public static final String ROUGHNESS_UNIT_KEY                 = "ROUGHNESS_UNIT";
  public static final String PRESSURE_MAX_UNIT_KEY              = "PRESSURE_MAX_UNIT";
  public static final String HEAT_TRANSFER_COEFFICIENT_UNIT_KEY = "HEAT_TRANSFER_COEFFICIENT_UNIT";

  
	// Adds the line data listener: @param listener
	public void addPipeChangeListener(PipeChangeListener listener);
  
	// Remove the line data listener: @param listener
	public void removePipeChangeListener(PipeChangeListener listener);

	// Set the minimum pressure: @param pressure
	public void setMinimiumPressure(double pressure);
  
	// Get the minimum pressure: @return
	public double getMinimumPressure();

	// Set the maximum pressure: @param pressure
	public void setMaximumPressure(double pressure);
  
	// Get the maximum pressure: @return
	public double getMaximumPressure();

	// Set the pressure: @param pressure
	public void setPressure(double pressure);
  
	// Get the pressure: @return
	public double getPressure();

	// Set the diameter: @param diameter
	public void setDiameter(double diameter);

	// Get the diameter: @return
	public double getDiameter();

	// Set the roughness: @param roughness
	public void setResistance(double resistance);
  
	// Get the roughness: @return
	public double getResistance();

	// Set the length of the pipe: @param length
	public void setLength(double length);
  
	// Get the length of the pipe: @return
	public double getLength();
  
	// Cling routine for pipes: @return
	public Pipe clone();
}
