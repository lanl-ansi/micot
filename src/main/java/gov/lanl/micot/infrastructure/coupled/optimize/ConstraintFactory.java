package gov.lanl.micot.infrastructure.coupled.optimize;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.coupled.model.CoupledNode;

/**
 * This is an interface for augmenting OPFDC simulators with additional constraints
 * @author Russell Bent
 *
 */
public interface ConstraintFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory<CoupledNode, CoupledModel>{

 
}
