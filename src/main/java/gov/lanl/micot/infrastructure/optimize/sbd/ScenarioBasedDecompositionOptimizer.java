package gov.lanl.micot.infrastructure.optimize.sbd;

import java.util.Collection;

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
 * scenario based decomposition
 * 
 * @author Russell Bent
 */
public interface ScenarioBasedDecompositionOptimizer<N extends Node, M extends Model> extends Optimizer<N,M> {
  
  /**
   * Add outer variable factories
   * @param factory
   */
  public void addOuterVariableFactory(VariableFactory<N,M> factory);

  /**
   * Get all the outer variables factories
   * @return
   */
  public Collection<VariableFactory<N,M>> getOuterVariableFactories();

  /**
   * Add the initialization routines for the outer problem
   * @param factory
   */
  public void addOuterInitialSolutionFactory(InitialSolutionFactory<M> factory);

  /**
   * Get the initialization routines for the outer problem
   * @return
   */
  public Collection<InitialSolutionFactory<M>> getOuterInitialSolutionFactories();
  
  /**
   * Add an assignment factory for the outer problem
   * @param factory
   */
  public void addOuterAssignmentFactory(AssignmentFactory<N,M> factory);
  
  /**
   * Get all the assignment factories for outer problems
   * @return
   */
  public Collection<AssignmentFactory<N,M>> getOuterAssignmentFactories();
  
  /**
   * Add the outer objective function factories
   * @param factory
   */
  public void addOuterObjectiveFunctionFactory(ObjectiveFunctionFactory<N,M> factory);

  /**
   * Get all the outer objective function factories
   * @return
   */
  public Collection<ObjectiveFunctionFactory<N,M>> getOuterObjectiveFunctionFactories();

  /**
   * Get all the outer constraint factories
   * @return
   */
  public Collection<ConstraintFactory<N,M>> getOuterConstraints();

  /**
   * Add outer constraint factories
   * @param constraint
   */
  public void addOuterConstraint(ConstraintFactory<N,M> constraint);
    
  /**
   * Add all the inner variables factories
   * @param factory
   */
  public void addInnerVariableFactory(VariableFactory<N,M> factory);
  
  /**
   * Get all the inner variable factories
   * @return
   */
  public Collection<VariableFactory<N,M>> getInnerVariableFactories();
  
  /**
   * Add all the inner solution factories
   * @param factory
   */
  public void addInnerInitialSolutionFactory(InitialSolutionFactory<M> factory);

  /**
   * Get all the inner solution factories
   * @return
   */
  public Collection<InitialSolutionFactory<M>> getInnerInitialSolutionFactories();
  
  /**
   * Add all the inner assignment factories
   * @param factory
   */
  public void addInnerAssignmentFactory(AssignmentFactory<N,M> factory);
  
  /**
   * Get all the inner assignment factories
   * @return
   */
  public Collection<AssignmentFactory<N,M>> getInnerAssignmentFactories();
  
  /**
   * Add the inner objective function factories
   * @param factory
   */
  public void addInnerObjectiveFunctionFactory(ObjectiveFunctionFactory<N,M> factory);

  /**
   * Get the inner objective function factories
   * @return
   */
  public Collection<ObjectiveFunctionFactory<N,M>> getInnerObjectiveFunctionFactories();
  
  /**
   * Get the inner constraints associated with the problem
   * @return
   */
  public Collection<ConstraintFactory<N,M>> getInnerConstraints();

  /**
   * Add an inner constraint
   * @param constraint
   */
  public void addInnerConstraint(ConstraintFactory<N,M> constraint);
}
