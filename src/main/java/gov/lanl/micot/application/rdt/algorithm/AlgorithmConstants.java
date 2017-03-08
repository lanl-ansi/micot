package gov.lanl.micot.application.rdt.algorithm;

/**
 * Put some constants in one place
 * @author Russell Bent
 * Slightly modified by Conrado, Nov. 2014
 *
 */
public class AlgorithmConstants {
  
  public static final String LINE_CONSTRUCTION_COST_KEY            = "LINE_CONSTRUCTION_COST";
  public static final String PIPE_CONSTRUCTION_COST_KEY            = "PIPE_CONSTRUCTION_COST";
  public static final String COMPRESSOR_CONSTRUCTION_COST_KEY      = "COMPRESSOR_CONSTRUCTION_COST";
  public static final String GENERATOR_CONSTRUCTION_COST_KEY       = "GENERATOR_CONSTRUCTION_COST";
  public static final String LINKED_PIPE_KEY       					       = "LINKED_PIPE"; 
  public static final String COUPLED_PIPE_KEY                      = "COUPLED_PIPE"; 
  
  public static final String HARDENED_DISABLED_KEY                 = "HARDENED_DISABLED";
  public static final String LINE_SWITCH_COST_KEY                  = "LINE_SWITCH_COST";
  public static final String MICROGRID_COST_KEY                    = "MICROGRID_COST";
  public static final String MICROGRID_FIXED_COST_KEY              = "MICROGRID_FIXED_COST";
  public static final String LINE_HARDEN_COST_KEY                  = "HARDEN_COST";

  public static final String IS_NEW_MICROGRID_KEY                  = "IS_NEW_MICROGRID";
  public static final String IS_NEW_LINE_KEY                       = "IS_NEW_LINE";
  public static final String HAS_SWITCH_KEY                        = "HAS_SWITCH";
  public static final String CAN_HARDEN_KEY                        = "CAN_HARDEN";
    
  public static final String IS_CRITICAL_LOAD_KEY                  = "IS_CRITICAL_LOAD";
  public static final String MAX_MICROGRID_KEY                     = "MAX_MICROGRID";

  public static final String IS_SWITCH_OPEN_KEY                    = "IS_SWITCH_OPEN";
  public static final String IS_SWITCH_CONSTRUCTED_KEY             = "IS_SWITCH_CONSTRUCTED";
  public static final String IS_CONSTRUCTED_KEY                    = "IS_CONSTRUCTED";
  public static final String IS_HARDENED_KEY                       = "IS_HARDENED";
  public static final String IS_USED_KEY                           = "IS_USED";

  public static final String MICROGRID_CAPACITY_PHASE_A_KEY        = "MICROGRID_CAPACITY_PHASE_A";
  public static final String MICROGRID_CAPACITY_PHASE_B_KEY        = "MICROGRID_CAPACITY_PHASE_B";
  public static final String MICROGRID_CAPACITY_PHASE_C_KEY        = "MICROGRID_CAPACITY_PHASE_C";
  
  public static final String LOAD_MET_KEY                          = "LoadMet";
  public static final String CRITICAL_LOAD_MET_KEY                 = "CriticalLoadMet";
  public static final String PHASE_VARIATION_KEY                   = "PhaseVariation";
  public static final String CHANCE_CONSTRAINT_EPSILON_KEY         = "ChanceConstraintEpsilon";
  public static final String IS_DISCRETE_GENERATION_KEY            = "IsDiscreteGeneration";
  public static final String IS_CHANCE_CONSTRAINT_KEY              = "IsChanceConstraint";
  public static final String USE_CYCLE_ENUMERATION_CONSTRAINT_KEY  = "UseCycleEnumerationConstraint";

  public static final double DEFAULT_LOAD_MET = .5;
  public static final double DEFAULT_CRITICAL_LOAD_MET = .98;
  public static final double DEFAULT_CHANCE_CONSTRAINT_EPSILON = 0.0;
  public static final boolean DEFAULT_IS_DISCRETE_GENERATION = false;
  public static final boolean DEFAULT_IS_CHANCE_CONSTRAINT = false;
  public static final boolean DEFAULT_USE_CYCLE_ENUMERATION_CONSTRAINT = true;
  
  
  /**
   * No constructor
   */
  private AlgorithmConstants() {    
  }
  

}
