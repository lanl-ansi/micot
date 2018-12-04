package gov.lanl.micot.deprecated.rdt.algorithm.ep.mip.variable.scenario;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerFlowConnection;
import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Scenario;
import gov.lanl.micot.application.lpnorm.io.LPNormIOConstants;
import gov.lanl.micot.application.rdt.algorithm.AlgorithmConstants;

/**
 * Some simple utility functions for creating variables
 * @author Russell Bent
 */
public class ScenarioVariableFactoryUtility {
  
  /**
   * Static members only
   */
  private ScenarioVariableFactoryUtility() {    
  }

  
  /**
   * Is an edge damaged in a scenario
   * @param edge
   * @param scenario
   * @return
   */
  public static boolean isDamaged(ElectricPowerFlowConnection edge, Scenario scenario) {
    boolean isDamaged = false;        
    Boolean b = scenario.getModification(edge, Asset.IS_FAILED_KEY, Boolean.class);
    if (b != null) {
      isDamaged = b;
    }
    return isDamaged;
  }
  
  /**
   * Is an edge hardened damaged
   * @param edge
   * @param scenario
   * @return
   */
  public static boolean isHardenedDamaged(ElectricPowerFlowConnection edge, Scenario scenario) {
    boolean isHardenedDamaged = false;        
    Boolean b = scenario.getModification(edge, AlgorithmConstants.HARDENED_DISABLED_KEY, Boolean.class);
    if (b != null) {
      isHardenedDamaged = b;      
    }
    return isHardenedDamaged;
  }
  
  /**
   * Figure out if we should create a variable or not for a particular edge
   * @param edge
   * @param scenario
   * @return
   */
  public static boolean doCreateLineExistScenarioVariable(ElectricPowerFlowConnection edge, Scenario scenario) {
    boolean canHarden = edge.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY) != null && edge.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY, Boolean.class);
    boolean exists = edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY) == null || edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY, Boolean.class) == false;
    boolean isHardenedDamaged = isHardenedDamaged(edge, scenario) ;//false;        
    boolean isDamaged = isDamaged(edge, scenario); //false;        
    
    // error checking. Can't be undamaged and hardened damaged
    if (!isDamaged && isHardenedDamaged) {
      isDamaged = true;
    }
    
    if (exists && isDamaged && !isHardenedDamaged && canHarden) {
      return true;
    }
    
    if (!exists && !isDamaged) {
      return true;
    }
                
    return false;
  }
    
  /**
   * Figure out if we should create a variable or not for a particular edge
   * @param edge
   * @param scenario
   * @return
   */
  public static boolean doCreateSwitchScenarioVariable(ElectricPowerFlowConnection edge, Scenario scenario) {
    boolean hasSwitch = edge.getAttribute(AlgorithmConstants.HAS_SWITCH_KEY) != null && edge.getAttribute(AlgorithmConstants.HAS_SWITCH_KEY, Boolean.class);
    boolean isDisabled = !edge.getStatus();
    boolean hasCost = edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY) == null ? false : true;
    boolean buildSwitch = edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH) == null ? false : edge.getAttribute(LPNormIOConstants.LINE_CAN_ADD_SWITCH, Boolean.class);
    boolean canBuild  = (isDisabled || (!hasCost && !buildSwitch)) ? false : true; 

    boolean hasLineExistsVariable = doCreateLineExistScenarioVariable(edge, scenario);
    Integer lineConstant = getLineExistScenarioConstant(edge, scenario);
    
    if (!hasSwitch && !canBuild) {
      return false;
    }
    
    if ((hasSwitch || canBuild) && hasLineExistsVariable) {
      return true;
    }
    
    if ((hasSwitch || canBuild) && lineConstant == 1) {
      return true;
    }
    
    return false;
  }

  /**
   * Determine if we should crate a line use scenario variable
   * @param edge
   * @param scenario
   * @return
   */
  public static boolean doCreateLineUseScenarioVariable(ElectricPowerFlowConnection edge, Scenario scenario) {
    boolean hasLineExistsVariable = doCreateLineExistScenarioVariable(edge, scenario);
    boolean hasSwitchVariable = doCreateSwitchScenarioVariable(edge, scenario);
    
    Integer lineExistsConstant = getLineExistScenarioConstant(edge, scenario);
    Integer switchConstant = getSwitchScenarioConstant(edge, scenario);
    
    
    if (!hasLineExistsVariable && !hasSwitchVariable) {
      return false;
    }
      
    if (hasLineExistsVariable && hasSwitchVariable) {
      return true;
    } 
    
    if (hasLineExistsVariable && switchConstant == 1) {
      return true;
    }

    if (hasSwitchVariable && lineExistsConstant == 1) {
      return true;
    }
    return false;    
  }
  
  
  /**
   * Determine the constant for a line exist variable
   * @param edge
   * @param scenario
   * @return
   */
  public static Integer getLineExistScenarioConstant(ElectricPowerFlowConnection edge, Scenario scenario) {
    boolean canHarden = edge.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY) != null && edge.getAttribute(AlgorithmConstants.CAN_HARDEN_KEY, Boolean.class);
    boolean exists = edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY) == null || edge.getAttribute(AlgorithmConstants.IS_NEW_LINE_KEY, Boolean.class) == false;
    boolean isHardenedDamaged = isHardenedDamaged(edge, scenario) ;//false;        
    boolean isDamaged = isDamaged(edge, scenario); //false;        


//    Object obj = edge.getAttribute(Asset.IS_FAILED_KEY);
  //  if (obj != null) {
    //  isDamaged = ((ScenarioAttribute)obj).getValue(scenario).intValue() == 1 ? true : false;
   // }
    
   // obj = edge.getAttribute(AlgorithmConstants.HARDENED_DISABLED_KEY);
   // if (obj != null) {
     // isHardenedDamaged = ((ScenarioAttribute)obj).getValue(scenario).intValue() == 1 ? true : false;
   // }
    
    if (!isDamaged && isHardenedDamaged) {
      isDamaged = true;
    }
    
    if (exists && !isDamaged) {
      return 1;
    }

    if (exists && isHardenedDamaged) {
      return 0;
    }
    
    if (!exists && isDamaged) {
      return 0;
    }
    
    if (exists && !canHarden) {
      return isDamaged ? 0 : 1;
    }
    
    return null;    
  }  

  
  /**
   * get the constant for a scenario switch variable
   * @param edge
   * @param scenario
   * @return
   */
  public static Integer getSwitchScenarioConstant(ElectricPowerFlowConnection edge, Scenario scenario) {
    boolean hasSwitch = edge.getAttribute(AlgorithmConstants.HAS_SWITCH_KEY) != null && edge.getAttribute(AlgorithmConstants.HAS_SWITCH_KEY, Boolean.class);
    boolean canBuild  = edge.getAttribute(AlgorithmConstants.LINE_SWITCH_COST_KEY) != null;
    
    boolean hasLineExistsVariable = doCreateLineExistScenarioVariable(edge, scenario);
    Integer lineConstant = getLineExistScenarioConstant(edge, scenario);
    
    if (!hasSwitch && !canBuild) {
      return 1; // the line is always there.... from a switch perspective
    }
    
    if ((hasSwitch || canBuild) && hasLineExistsVariable) {
      return null; // not a constant
    }
    
    if ((hasSwitch || canBuild) && lineConstant == 1) {
      return null; // not a constant
    }
    
    return 1; // the line is always there.... from a switch perspective
  }


  
  /**
   * Determine the constant for line use variables
   * @param edge
   * @param scenario
   * @return
   */
  public static Integer getLineUseScenarioConstant(ElectricPowerFlowConnection edge, Scenario scenario) {
    boolean hasLineExistsVariable = doCreateLineExistScenarioVariable(edge, scenario);
    boolean hasSwitchVariable = doCreateSwitchScenarioVariable(edge, scenario);
    
    Integer lineExistsConstant = getLineExistScenarioConstant(edge, scenario);
    Integer switchConstant = getSwitchScenarioConstant(edge, scenario);
    
    
    if (!hasLineExistsVariable && !hasSwitchVariable) {
      return lineExistsConstant; // takes the same status as the existance variable
    }
      
    if (hasLineExistsVariable && hasSwitchVariable) {
      return null; // not a constant
    } 
    
    if (hasLineExistsVariable && switchConstant == 1) {
      return null; // not a constant
    }

    if (hasSwitchVariable && lineExistsConstant == 1) {
      return null; // not a constant
    }
    
    if (hasLineExistsVariable && switchConstant == 0) {
      return 0; // this should never happen, but just in case....
    }
    
    if (hasSwitchVariable && lineExistsConstant == 0) {
      return 0; // the line does not exist, switching does matter, we can't use the line
    }
    
    return null;    
  }

  
  
}
