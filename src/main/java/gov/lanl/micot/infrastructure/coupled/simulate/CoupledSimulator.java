package gov.lanl.micot.infrastructure.coupled.simulate;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.simulate.Simulator;

/**
 * Interface for simulator packages
 * @author Russell Bent
 */
public interface CoupledSimulator extends Simulator<CoupledModel> {

		
	/**
	 * Global function for launching the solve
	 * This function is supposed to return a new model that contains the solved
	 * state 
	 * @param model
	 */
  public SimulatorSolveState executeSimulation(CoupledModel model);
		
}
