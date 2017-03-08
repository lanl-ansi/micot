package gov.lanl.micot.infrastructure.ep.simulate;

import gov.lanl.micot.infrastructure.ep.model.ElectricPowerModel;
import gov.lanl.micot.infrastructure.simulate.Simulator;

/**
 * Interface for simulator packages
 * @author Russell Bent
 */
public interface ElectricPowerSimulator extends Simulator<ElectricPowerModel> {

		
	/**
	 * Global function for launching the solve
	 * This function is supposed to return a new model that contains the solved
	 * state 
	 * @param model
	 */
  public SimulatorSolveState executeSimulation(ElectricPowerModel modelState);
		
}
