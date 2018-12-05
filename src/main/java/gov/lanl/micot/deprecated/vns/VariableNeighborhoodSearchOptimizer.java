package gov.lanl.micot.deprecated.vns;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.Optimizer;
import gov.lanl.micot.infrastructure.optimize.mathprogram.assignment.AssignmentFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.constraint.ConstraintFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.initialsolution.InitialSolutionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.objective.ObjectiveFunctionFactory;
import gov.lanl.micot.infrastructure.optimize.mathprogram.variable.VariableFactory;

/**
 * This class contains basic interfaces about an optimization routine that uses 
 * variable neighborhood search
 * 
 * @author Russell Bent
 */
public interface VariableNeighborhoodSearchOptimizer<N extends Node, M extends Model> extends Optimizer<N,M> {
  
  /**
   * Add the variable factories. This maps the lpfactory to the full mixed interger factory
   * @param factory
   */
  public void addVariableFactory(VariableFactory<N,M> lpFactory, VariableFactory<N,M> fullFactory, boolean isDiscrete);

  /**
   * Add the initialization routines
   * @param factory
   */
  public void addInitialSolutionFactory(InitialSolutionFactory<M> factory);
  
  /**
   * Add an assignment factory 
   * @param factory
   */
  public void addAssignmentFactory(AssignmentFactory<N,M> factory);
    
  /**
   * Add the objective function factory
   * @param factory
   */
  public void addObjectiveFunctionFactory(ObjectiveFunctionFactory<N,M> factory);

  /**
   * Add constraint factories
   * @param constraint
   */
  public void addConstraint(ConstraintFactory<N,M> constraint);
    
}
