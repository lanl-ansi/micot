package gov.lanl.micot.infrastructure.optimize;

import java.util.Collection;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * Class for optimizing models
 * @author Russell Bent
 */
public interface Optimizer<N extends Node, M extends Model> {
  
  /**
   * Solve the optimization problem
   * @param model
   * @return
   */
  public boolean solve(M model);

  /**
   * Get the best model file
   * @return
   */
//  public String getBestModelFile();
  
  /**
   * Set the est mode model file
   * @param bestModelFile
   */
 // public void setBestModelFile(String bestModelFile);
  
  /**
   * Adds an algorithm listener
   * @param listener
   */
  public void addOptimizerListener(OptimizerListener listenr);
  
  /**
   * Adds a set of algorithm listeners
   * @param listeners
   */
  public void addOptimizerListeners(Collection<OptimizerListener> listeners);
  
  /**
   * Remove an algorithm listener
   * @param listener
   */
  public void removeOptimizerListener(OptimizerListener listener);

  /**
   * Get the last objective value
   * @return
   */
  public double getObjectiveValue();
  
  /**
   * Is the result feasible or not
   * @return
   */
  public boolean isFeasible();

  
  /**
   * Get the cpu time
   * @return
   */
  public double getCPUTime();
  
  
  // TODO The below 4 functions should be moved down in the call heirarchy since not all optimizers are structured like this 
  // any more
    
  /**
   * Add the variable factories
   * @param factory
   */
  public void addAssignmentFactory(AssignmentFactory<N,M> factory);  
  
  /**
   * Add a constraint to the system
   * @param constraint
   */
  public void addConstraint(ConstraintFactory<N,M> constraint);

  /**
   * Add the variable factories
   * @param factory
   */
  public void addVariableFactory(VariableFactory<N,M> factory);
  
  /**
   * Add the objective function
   * @param factory
   */
  public void addObjectiveFunctionFactory(ObjectiveFunctionFactory<N,M> factory);
  
  
}


