package gov.lanl.micot.infrastructure.naturalgas.model;

import java.util.Set;

import gov.lanl.micot.infrastructure.model.Component;
import gov.lanl.micot.infrastructure.model.Producer;

/**
 * Base class for generator types
 * @author Russell Bent
 */
public interface Well extends Producer, Component {
 
  public static final String WELL_NAME_KEY                   = "NAME";
  public static final String TEMPERATURE_KEY                 = "TEMPERATURE";
  public static final String CALORIFIC_KEY                   = "CALORIFIC";
  public static final String MOLAR_MASS_KEY                  = "MOLAR_MASS";
  public static final String HEAT_CAPACITY_A_KEY             = "HEAT_CAPACITY_A";
  public static final String HEAT_CAPACITY_B_KEY             = "HEAT_CAPACITY_B";
  public static final String HEAT_CAPACITY_C_KEY             = "HEAT_CAPACITY_C";
  public static final String NORM_DENSITY_KEY                = "NORM_DENSITY";
  public static final String PSEUDO_CRITICAL_PRESSURE_KEY    = "PSEUDO_CRITICAL_PRESSURE";
  public static final String PSEUDO_CRITICAL_TEMPERATURE_KEY = "PSEUDO_CRITICAL_TEMPERATURE";
 
  public static final String FLOW_UNIT_KEY                        = "FLOW_UNIT";
  public static final String TEMPERATURE_UNIT_KEY                 = "TEMPERATURE_UNIT";
  public static final String CALORIFIC_UNIT_KEY                   = "CALORIFIC_UNIT";
  public static final String MOLAR_MASS_UNIT_KEY                  = "MOLAR_MASS_UNIT";
  public static final String NORM_DENSITY_UNIT_KEY                = "NORM_DENSITY_UNIT";
  public static final String PSEUDO_CRITICAL_PRESSURE_UNIT_KEY    = "PSEUDO_CRITICAL_PRESSURE_UNIT";
  public static final String PSEUDO_CRITICAL_TEMPERATURE_UNIT_KEY = "PSEUDO_CRITICAL_TEMPERATURE_UNIT";
  
  
  /**
   * Fill from a set of wells
   * @param stateDataMap
   */
  public void setActualProduction(Set<Well> wells);
	    
  /**
   * Add a listener to the producer data
   * @param listener
   */
  public void addWellDataListener(WellChangeListener listener);
  
  /**
   * Remove a listener to the generator data
   * @param listener
   */
  public void removeWellDataListener(WellChangeListener listener);
  
  /**
   * Clone a well
   * @return
   */
  public Well clone();
   
}
