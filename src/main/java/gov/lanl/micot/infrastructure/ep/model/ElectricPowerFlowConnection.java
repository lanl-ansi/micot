package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.FlowConnection;

/**
 * Abstract class for power lines
 * @author Russell Bent
 */

public interface ElectricPowerFlowConnection extends ElectricPowerConnection, FlowConnection {

  public static final String CAPACITY_RATING_KEY                      = "CAPACITY_RATING";
  public static final String LINE_CHARGING_KEY                        = "LINE_CHARGING";
  public static final String LONG_TERM_EMERGENCY_CAPACITY_RATING_KEY  = "LONG_TERM_EMERGENCY_CAPACITY_RATING";
  public static final String SHORT_TERM_EMERGENCY_CAPACITY_RATING_KEY = "SHORT_TERM_EMERGENCY_CAPACITY_RATING";
  public static final String MVAR_FLOW_KEY                            = "MVAR_FLOW";
  public static final String MW_FLOW_KEY                              = "MW_FLOW";
  public static final String REACTANCE_KEY                            = "REACTANCE";
  public static final String RESISTANCE_KEY                           = "RESISTANCE";
  public static final String REAL_LOSS_KEY                            = "REAL_LOSS";
  public static final String REACTIVE_LOSS_KEY                        = "REACTIVE_LOSS";
  public static final String HAS_PHASE_A_KEY                           = "HAS_PHASE_A";
  public static final String HAS_PHASE_B_KEY                           = "HAS_PHASE_B";
  public static final String HAS_PHASE_C_KEY                           = "HAS_PHASE_C";
  public static final String NUMBER_OF_PHASES_KEY                     = "NUMBER_OF_PHASES";
  public static final String PHASE_ANGLE_DIFFERENCE_LIMIT_KEY         = "PHASE_ANGLE_DIFFERENCE_LIMIT";

  public static final String MVAR_FLOW_PHASE_A_KEY                            = "MVAR_FLOW_PHASE_A";
  public static final String MW_FLOW_PHASE_A_KEY                              = "MW_FLOW_PHASE_A";
  public static final String MVAR_FLOW_PHASE_B_KEY                            = "MVAR_FLOW_PHASE_B";
  public static final String MW_FLOW_PHASE_B_KEY                              = "MW_FLOW_PHASE_B";
  public static final String MVAR_FLOW_PHASE_C_KEY                            = "MVAR_FLOW_PHASE_C";
  public static final String MW_FLOW_PHASE_C_KEY                              = "MW_FLOW_PHASE_C";

  public static final String CAPACITY_RATING_A_KEY                      = "CAPACITY_RATING_A";
  public static final String CAPACITY_RATING_B_KEY                      = "CAPACITY_RATING_B";
  public static final String CAPACITY_RATING_C_KEY                      = "CAPACITY_RATING_C";

  public static final String RESISTANCE_PHASE_A_KEY                      = "RESISTANCE_PHASE_A";
  public static final String RESISTANCE_PHASE_B_KEY                      = "RESISTANCE_PHASE_B";
  public static final String RESISTANCE_PHASE_C_KEY                      = "RESISTANCE_PHASE_C";
  
  public static final String RESISTANCE_PHASE_AB_KEY							= "RESISTANCE_AB";
  public static final String RESISTANCE_PHASE_BA_KEY              = "RESISTANCE_BA";  
  public static final String RESISTANCE_PHASE_BC_KEY							= "RESISTANCE_BC";  
  public static final String RESISTANCE_PHASE_CB_KEY              = "RESISTANCE_CB";
  public static final String RESISTANCE_PHASE_CA_KEY							= "RESISTANCE_CA";
  public static final String RESISTANCE_PHASE_AC_KEY              = "RESISTANCE_AC";
  
  public static final String REACTANCE_PHASE_A_KEY                      = "REACTANCE_PHASE_A";
  public static final String REACTANCE_PHASE_B_KEY                      = "REACTANCE_PHASE_B";
  public static final String REACTANCE_PHASE_C_KEY                      = "REACTANCE_PHASE_C";
  
  public static final String REACTANCE_PHASE_AB_KEY				= "REACTANCE_AB";
  public static final String REACTANCE_PHASE_BA_KEY             = "REACTANCE_BA";
  public static final String REACTANCE_PHASE_BC_KEY				= "REACTANCE_BC";
  public static final String REACTANCE_PHASE_CB_KEY             = "REACTANCE_CB";  
  public static final String REACTANCE_PHASE_CA_KEY				= "REACTANCE_CA";
  public static final String REACTANCE_PHASE_AC_KEY             = "REACTANCE_AC";
  
  public static final String VOLTAGE_PRIMARY_KEY 				= "VOLTAGE_PRIMARY";
  public static final String VOLTAGE_SECONDARY_KEY 				= "VOLTAGE_SECONDARY";  
  public static final String NOMINAL_POWER_RATING_KEY			= "NOMINAL_POWER_RATING";
   
  public static final String NAME_KEY                                   = "NAME";

  public static final String INSTALLATION_TYPE_KEY                      = "INSTALLATION_TYPE";
  public static final String LINE_TYPE_KEY                              = "LINE_TYPE";
  public static final String LINE_DESCRIPTION_KEY                       = "LINE_DESCRIPTION_TYPE";
  public static final String HAS_SWITCH_KEY                             = "HAS_SWITCH";
  
  public static final String CURRENT_KEY                                = "CURRENT";
  public static final String MAXIMUM_CURRENT_KEY                        = "MAXIMUM_CURRENT";
  
  public static final String MVAR_FLOW_SIDE1_KEY                   = "MVAR_FLOW_SIDE1";
  public static final String MVAR_FLOW_SIDE2_KEY                   = "MVAR_FLOW_SIDE2";
  public static final String MW_FLOW_SIDE1_KEY                     = "MW_FLOW_SIDE1";
  public static final String MW_FLOW_SIDE2_KEY                     = "MW_FLOW_SIDE2";
  public static final String LINE_PHASE_CONDUCTOR_KEY                   = "PHASE_CONDUCTOR";
  public static final String LINE_NEUTRAL_CONDUCTOR_KEY                 = "NEUTRAL_CONDUCTOR";

  public static final String NUMBER_OF_POLES_KEY                        = "NUMBER_OF_POLES";
  
	 /**
   * Get the resistance of a power line
   * @return
   */
  public Double getResistance();

  /**
   * Sets the resistance
   * @param resistance
   */
  public void setResistance(Number resistance);
  
  /**
   * Get the reactance of a power line
   * @return
   */
  public Double getReactance();
  
  /**
   * Set the reactance of a power line
   * @param reactance
   */
  public void setReactance(Number reactance);
  
  /**
   * Get the line charging of a power line
   * @return
   */
  public Double getLineCharging();
    
  /**
   * Set the line chargining of a power line
   * @param lineCharging
   */
  public void setLineCharging(Number lineCharging);
  
  /**
   * Gets the capacity rating
   * @return
   */
  public Double getCapacityRating();

  /**
   * Set the capacity rating
   * @param capacityRating
   */
  public void setCapacityRating(Number capacityRating);
  
  /**
   * Get the short term emergency short term capacity rating
   * @return
   */
  public Double getShortTermEmergencyCapacityRating();

  /**
   * Set the short term capacity rating
   * @param capacityRating
   */
  public void setShortTermEmergencyCapacityRating(Number capacityRating);
  
  /**
   * Get the short term emergency shrot term capacity rating
   * @return
   */
  public Double getLongTermEmergencyCapacityRating();
  
  /**
   * Set the long term capacity rating
   * @param capacityRating
   */
  public void setLongTermEmergencyCapacityRating(Number capacityRating);
  
  /**
   * Sets the mw flow
   * @param mw
   */
  public void setMWFlow(Number mw);
  
  /**
   * Sets the mvar flow
   * @param mvar
   */
  public void setMVarFlow(Number mvar);
  
  /**
   * Gets the mw flow
   * @return
   */
  public Number getMWFlow();
  
  /**
   * Gets the mvar flow
   * @return
   */
  public Number getMVarFlow(); 

  /**
   * Get the susceptance
   * @return
   */
  public Double getSusceptance();
  
  /**
   * Get the conductance
   * @return
   */
  public Double getConductance();

  /**
   * Get the real loss
   * @return
   */
  public Double getRealLoss();
  
  /**
   * Set the real loss
   * @param loss
   * @return
   */
  public void setRealLoss(double loss);
  
  /**
   * Get the reactive loss
   * @return
   */
  public Double getReactiveLoss();
  
  /**
   * Set the reactive loss
   * @param loss
   * @return
   */
  public void setReactiveLoss(double loss);
}
