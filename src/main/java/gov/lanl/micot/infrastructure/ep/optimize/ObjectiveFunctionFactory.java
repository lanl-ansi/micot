package gov.lanl.micot.infrastructure.ep.optimize;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;

/**
 * Interface for factories that create objective functions
 * @author Russell Bent
 */
public interface ObjectiveFunctionFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory<ElectricPowerNode, ElectricPowerModel>{
  

}
