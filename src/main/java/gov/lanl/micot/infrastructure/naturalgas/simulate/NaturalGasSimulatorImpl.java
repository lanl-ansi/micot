package gov.lanl.micot.infrastructure.naturalgas.simulate;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.naturalgas.model.NaturalGasModel;
import gov.lanl.micot.infrastructure.simulate.SimulationExecutionException;

/**
 * Abstract class for interacting with the simulator
 * 
 * @author Russell Bent
 */
public abstract class NaturalGasSimulatorImpl implements NaturalGasSimulator {

	/**
	 * Constructor
	 */
	protected NaturalGasSimulatorImpl() {
	}
		
	@Override
	public SimulatorSolveState executeSimulation(NaturalGasModel m) {
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
	protected abstract SimulatorSolveState simulateModel(NaturalGasModel modelState);
		
	@Override
	public SimulatorSolveState executeSimulation(Model model) {
		return executeSimulation((NaturalGasModel)model);
	}
	
  /**
   * Update the model status
   * @param model
   */
  protected void updateModelStatus(Model model) {
    for (Asset asset : model.getAssets()) {
      asset.setActualStatus(asset.getDesiredStatus());
    }
  }

  @Override
  public void close() {  
  }
}
