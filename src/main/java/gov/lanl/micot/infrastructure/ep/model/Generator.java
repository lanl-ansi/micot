package gov.lanl.micot.infrastructure.ep.model;

import java.util.Set;

import gov.lanl.micot.infrastructure.model.Component;

/**
 * Base class for generator types
 * @author Russell Bent
 */
public interface Generator extends ElectricPowerProducer, Component {

  public static final String TYPE_KEY                              = "TYPE";
  public static final String FUEL_TYPE_KEY                         = "FUEL_TYPE";
  public static final String NAME_KEY                              = "NAME";
  public static final String MVA_BASE_KEY                          = "MVA_BASE";
  public static final String CAPACITY_FACTOR_KEY                   = "CAPACITY_FACTOR";
  public static final String STARTUP_COST_KEY                      = "STARTUP_COST";
  public static final String SHUTDOWN_COST_KEY                     = "SHUTDOWN_COST";
  
  public static final String NUM_PHASE_KEY                         = "NUMBER_OF_PHASES";
  //public static final String ACTUAL_REACTIVE_GENERATION_A_KEY      = "ACTUAL_REACTIVE_GENERATION_A";
  //public static final String ACTUAL_REAL_GENERATION_A_KEY          = "ACTUAL_REAL_GENERATION_A";
  public static final String REACTIVE_GENERATION_A_KEY     = "REACTIVE_GENERATION_A";
  public static final String REAL_GENERATION_A_KEY         = "REAL_GENERATION_A";
  //public static final String ACTUAL_REACTIVE_GENERATION_B_KEY      = "ACTUAL_REACTIVE_GENERATION_B";
  //public static final String ACTUAL_REAL_GENERATION_B_KEY          = "ACTUAL_REAL_GENERATION_B";
  public static final String REACTIVE_GENERATION_B_KEY     = "REACTIVE_GENERATION_B";
  public static final String REAL_GENERATION_B_KEY         = "REAL_GENERATION_B";
  //public static final String ACTUAL_REACTIVE_GENERATION_C_KEY      = "ACTUAL_REACTIVE_GENERATION_C";
  //public static final String ACTUAL_REAL_GENERATION_C_KEY          = "ACTUAL_REAL_GENERATION_C";
  public static final String REACTIVE_GENERATION_C_KEY     = "REACTIVE_GENERATION_C";
  public static final String REAL_GENERATION_C_KEY         = "REAL_GENERATION_C";
  
  public static final String HAS_PHASE_A_KEY                       = "HAS_PHASE_A";
  public static final String HAS_PHASE_B_KEY                       = "HAS_PHASE_B";
  public static final String HAS_PHASE_C_KEY                       = "HAS_PHASE_C";
  
	/**
	 * Get the type of generator
	 * @return
	 */
	public abstract GeneratorTypeEnum getType();	
	
	/**
	 * Set the type
	 * @param type
	 */
	public void setType(GeneratorTypeEnum type);
	
  /**
   * Get the capacity factor
   * @return
   */
  public Number getCapacityFactor();
  
  /**
   * Set the capacity factor
   * @param capacityFactor
   */
  public void setCapacityFactor(Number capacityFactor);
  
  /**
   * Add a listener to the generate data
   * @param listener
   */
  public void addGeneratorDataListener(GeneratorChangeListener listener);
  
  /**
   * Remove a listener to the generator data
   * @param listener
   */
  public void removeGeneratorDataListener(GeneratorChangeListener listener);
  
  /**
   * Compute the actual real generation max value
   * @return
   */
  public Number computeRealGenerationMax();
  
  /**
   * Compute the actual reactive generation max value
   * @return
   */
  public Number computeReactiveGenerationMax();
  
  /**
   * Fill from a set of generators based on ratios
   * @param stateDataMap
   */
  public void setGeneration(Set<Generator> generators);

  /**
   * This function updates the bounds in the generators to make them feasible
   */
  public abstract void makeGenerationBoundsFeasible();
  
  /**
   * Clone the generator
   * @return
   */
  public Generator clone();
}
