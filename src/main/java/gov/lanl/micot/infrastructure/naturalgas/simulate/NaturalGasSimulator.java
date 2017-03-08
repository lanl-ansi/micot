package gov.lanl.micot.infrastructure.naturalgas.simulate;

import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.simulate.Simulator;

/**
 * Interface for simulator packages
 * @author Russell Bent
 */
public interface NaturalGasSimulator extends Simulator {

		
	/**
	 * Global function for launching the solve
	 * This function is supposed to return a new model that contains the solved
	 * state 
	 * @param model
	 */
	public SimulatorSolveState executeSimulation(NaturalGasModel model);
		
}
