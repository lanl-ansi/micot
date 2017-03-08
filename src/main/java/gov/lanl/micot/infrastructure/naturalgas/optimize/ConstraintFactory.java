package gov.lanl.micot.infrastructure.naturalgas.optimize;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;

/**
 * This is an interface for optimal gas flow constraints
 * @author Russell Bent
 *
 */
public interface ConstraintFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory<NaturalGasNode, NaturalGasModel>{

  
 
}
