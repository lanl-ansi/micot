package gov.lanl.micot.infrastructure.simulate;

import gov.lanl.micot.infrastructure.model.Model;

/**
 * Interface for simulator packages
 * @author Russell Bent
 */
public interface Simulator<M extends Model> {

//	public static String SHADOW_PRICE_KEY       = "SHADOW_PRICE";
//	public static String SHADOW_PRICE_RANGE_KEY = "SHADOW_PRICE_RANGE";
		
	/**
	 * Enumeration of possible simulator solve states
	 * @author Russell Bent
	 */
	public enum SimulatorSolveState {
		RESTART_SOLUTION, 
		CONVERGED_SOLUTION,
		NON_CONVERGED_SOLUTION,
		ERROR_SOLUTION;
	}
	
	/**
	 * Global function for launching the solve
	 * This function is supposed to return a new model that contains the solved
	 * state 
	 * @param model
	 */
	public SimulatorSolveState executeSimulation(Model model);
	
	/**
	 * Close out the simulator
	 */
	public void close();
}
