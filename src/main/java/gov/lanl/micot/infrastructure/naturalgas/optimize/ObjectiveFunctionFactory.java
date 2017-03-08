package gov.lanl.micot.infrastructure.naturalgas.optimize;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasNode;

/**
 * Interface for factories that create objective functions
 * @author Russell Bent
 */
public interface ObjectiveFunctionFactory extends gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory<NaturalGasNode, NaturalGasModel>{
  

}
