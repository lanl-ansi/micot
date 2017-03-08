package gov.lanl.micot.util.math.solver;

import gov.lanl.micot.util.math.solver.exception.SolverException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;

/**
 * Interface for math models
 * @author Russell Bent
 */
public interface MathModel {

	/**
	 * Create a continous variable
	 * @return
	 * @throws VariableExistsException 
	 */
	public ContinuousVariable makeContinuousVariable(String name) throws VariableExistsException;

	/**
	 * Make a certain number of continous variables
	 * @param num
	 * @return
	 * @throws VariableExistsException 
	 */
	public ContinuousVariable[] makeContinuousVariable(int num) throws VariableExistsException;
	
	/**
	 * Solve the linear program
	 * @return
	 * @throws SolverException 
	 */
	public Solution solve() throws SolverException;

	/**
	 * Get a variable by a string name
	 * @param loadVariableName
	 * @return
	 */
	public Variable getVariable(String variable);

	/**
	 * Get the variables
	 * @return
	 */
	public Iterable<Variable> getVariables();

	/**
	 * Get the number of variables
	 * @return
	 */
	public int getNumberOfVariables();

	
}
