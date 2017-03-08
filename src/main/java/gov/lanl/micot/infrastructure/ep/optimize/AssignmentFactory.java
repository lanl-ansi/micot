package gov.lanl.micot.infrastructure.ep.optimize;


import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.ep.model.ElectricPowerNode;

/**
 * Interface for factories that create assignment factories
 * @author Russell Bent
 */
public interface AssignmentFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory<ElectricPowerNode, ElectricPowerModel>{
  
}
