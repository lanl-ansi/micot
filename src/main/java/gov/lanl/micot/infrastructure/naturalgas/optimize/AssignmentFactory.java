package gov.lanl.micot.infrastructure.naturalgas.optimize;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;

/**
 * Interface for factories that create assignment factories
 * @author Russell Bent
 */
public interface AssignmentFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory<NaturalGasNode, NaturalGasModel>{

 
}
