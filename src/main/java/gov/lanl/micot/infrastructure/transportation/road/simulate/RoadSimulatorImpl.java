package gov.lanl.micot.infrastructure.transportation.road.simulate;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.simulate.SimulationExecutionException;
import gov.lanl.micot.infrastructure.transportation.road.model.RoadModel;

/**
 * Abstract class for interacting with the simulator
 * 
 * @author Russell Bent
 */
public abstract class RoadSimulatorImpl implements RoadSimulator {

	/**
	 * Constructor
	 */
	protected RoadSimulatorImpl() {
	}
		
	@Override
	public SimulatorSolveState executeSimulation(RoadModel m) {
		SimulatorSolveState solution = simulateModel(m);
		  switch (solution) {
			  case NON_CONVERGED_SOLUTION:
			  case ERROR_SOLUTION:
			  case RESTART_SOLUTION:
			    m.setIsSolved(false);
			    break;
			  case CONVERGED_SOLUTION: 
			    m.setIsSolved(true);
			    break;
			  default:
			    System.err.println("Exiting, cannot recover from solver");
			    throw new SimulationExecutionException("Exiting, cannot recover from solver");
		  }
		
		  return solution;
	}

	/**
	 * Simulates the model
	 * 
	 * @param model
	 * @return
	 */
	protected abstract SimulatorSolveState simulateModel(RoadModel model);
		
	@Override
	public SimulatorSolveState executeSimulation(Model modelState) {
		return executeSimulation((RoadModel)modelState);
	}
	
	@Override
  public void close() {  
  }
}
