package gov.lanl.micot.infrastructure.transportation.road.simulate;

import gov.lanl.micot.infrastructure.simulate.Simulator;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;

/**
 * Interface for simulator packages
 * @author Russell Bent
 */
public interface RoadSimulator extends Simulator {

		
	/**
	 * Global function for launching the solve
	 * This function is supposed to return a new model that contains the solved
	 * state 
	 * @param model
	 */
	public SimulatorSolveState executeSimulation(RoadModel model);
		
}
