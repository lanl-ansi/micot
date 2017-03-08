package gov.lanl.micot.infrastructure.coupled.optimize;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.coupled.model.CoupledNode;


/**
 * Interface for factories that create assignment factories
 * @author Russell Bent
 */
public interface AssignmentFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory<CoupledNode, CoupledModel>{
  
}
