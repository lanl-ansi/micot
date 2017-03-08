package gov.lanl.micot.infrastructure.coupled.optimize;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.coupled.model.CoupledNode;

/**
 * Interface for factories that create objective functions
 * @author Russell Bent
 */
public interface ObjectiveFunctionFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory<CoupledNode, CoupledModel>{
  
}
