package gov.lanl.micot.infrastructure.coupled.simulate;

import gov.lanl.micot.infrastructure.coupled.model.CoupledModel;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.simulate.SimulationExecutionException;

/**
 * Abstract class for interacting with the simulator
 * 
 * @author Russell Bent
 */
public abstract class CoupledSimulatorImpl implements CoupledSimulator {

	/**
	 * Constructor
	 */
	protected CoupledSimulatorImpl() {
	}
		
	@Override
	public SimulatorSolveState executeSimulation(CoupledModel m) {
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
	protected abstract SimulatorSolveState simulateModel(CoupledModel modelState);
		
	@Override
	public SimulatorSolveState executeSimulation(Model model) {
		return executeSimulation((CoupledModel)model);
	}
	
	@Override
  public void close() {  
  }
}
