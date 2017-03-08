package gov.lanl.micot.util.math.solver.mathprogram;

import java.io.FileNotFoundException;

import gov.lanl.micot.util.math.solver.DiscreteVariable;
import gov.lanl.micot.util.math.solver.LinearConstraint;
import gov.lanl.micot.util.math.solver.MathModel;
import gov.lanl.micot.util.math.solver.QuadraticConstraint;
import gov.lanl.micot.util.math.solver.Solution;
import gov.lanl.micot.util.math.solver.Variable;
import gov.lanl.micot.util.math.solver.exception.InvalidConstraintException;
import gov.lanl.micot.util.math.solver.exception.InvalidObjectiveException;
import gov.lanl.micot.util.math.solver.exception.InvalidVariableException;
import gov.lanl.micot.util.math.solver.exception.VariableExistsException;

/**
 * An interface for a linear programming problem
 * @author Russell Bent
 */
public interface MathematicalProgram extends MathModel {

  // set the initial solution
  public void setInitialSolution(Solution solution);
  
	// Create a discrete variable: @return, @throws InvalidVariableException
	public DiscreteVariable makeDiscreteVariable(String name) throws VariableExistsException, InvalidVariableException;
	
	// Make a certain number of discrete variables: @param num, @return, @throws InvalidVariableException 
	public DiscreteVariable[] makeDiscreteVariable(int num) throws VariableExistsException, InvalidVariableException;
	
	// A generic method for adding bounds: @param var, @param lb, @param ub, 
	public void addBounds(Variable var, double lb, double ub);
	
	// Add a constraint to the LP: @param constraint
	public void addLinearConstraint(LinearConstraint constraint) throws InvalidConstraintException;
	
	// Remove a constraint from the LP: @param constraint
	public void removeLinearConstraint(LinearConstraint constraint);
    
	// Get a constraint by a string name: @param constraint, @return
	public QuadraticConstraint getQuadraticConstraint(String constraint);

	// Add a constraint to the QP: @param constraint
	public void addQuadraticConstraint(QuadraticConstraint constraint) throws InvalidConstraintException;
  
	// Remove a constraint from the QP: @param constraint
	public void removeQuadraticConstraint(QuadraticConstraint constraint);
    
	// Get a constraint by a string name: @param constraint, @return
	public LinearConstraint getLinearConstraint(String constraint);
	
	// Set the linear --> linear objective function: @param obj
	public void setLinearObjective(LinearObjective obj) throws InvalidObjectiveException;

	// Set the quadratic objective function: @param obj
	public void setQuadraticObjective(QuadraticObjective obj) throws InvalidObjectiveException;

	// Get the constraints: @return
	public Iterable<LinearConstraint> getLinearConstraints();

	// Get the constraints: @return
	public Iterable<QuadraticConstraint> getQuadraticConstraints();
	
	// Get the linear objective function: @return
	public LinearObjective getLinearObjective();

	// Get the quadratic objective function: @return
	public QuadraticObjective getQuadraticObjective();
	
	// Get the number of constraints: @return
	public int getNumberOfLinearConstraints();

	// Get the number of constraints: @return
	public int getNumberOfQuadraticConstraints();

	/** Multiplies --> Multiply each constraint in the program by a constant on both
	 *  sides such that the smallest coefficient in the constraint
	 *  is one. Fixes some numerical issues that can arise. Like for example...?
	 */
	public void normalizeConstraintsLocally();

	/** Multiplies --> Multiply each constraint in the program by a constant on both
	 *  sides such that the smallest coefficient in the constraint
	 *  is one. Fixes some numerical issues that can arise.  This uses a global metric
	 */
	public void normalizeConstraintsGlobally();
  
	/** Get the upper bound of a variable
	 *	Null means no bound
	 *	@param variable,  @return
	 */
	public Number getUpperBound(Variable variable);

	/**
	 * Get the lower bound of a variable: Null means no bound
	 * @param variable
	 * @return
	 */
	public Number getLowerBound(Variable variable);
  
	// Exports the model to a file: @param filename, @throws FileNotFoundException
	public void exportModel(String filename) throws FileNotFoundException;
	
	/**
	 * Get the initial solution
	 * @return
	 */
	public Solution getInitialSolution();
	
	/**
	 * Get a profile of the mathematical program solver
	 * @return
	 */
	public MathematicalProgramProfile getProfile();

	/**
	 * Function for adding an expression constraint to a mathematical program
	 * @param constraint
	 * @throws InvalidConstraintException 
	 */
//  public void addExpressionConstraint(ExpressionConstraint constraint) throws InvalidConstraintException;

}
