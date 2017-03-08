package gov.lanl.micot.infrastructure.coupled.optimize;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.coupled.model.CoupledNode;

/**
 * Interface for factories that create variables
 * @author Russell Bent
 */
public interface VariableFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory<CoupledNode, CoupledModel> {


}
