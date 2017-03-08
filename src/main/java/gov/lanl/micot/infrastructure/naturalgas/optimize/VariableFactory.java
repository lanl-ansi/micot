package gov.lanl.micot.infrastructure.naturalgas.optimize;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;

/**
 * Interface for factories that create variables
 * @author Russell Bent
 */
public interface VariableFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory<NaturalGasNode, NaturalGasModel> {
 

}
