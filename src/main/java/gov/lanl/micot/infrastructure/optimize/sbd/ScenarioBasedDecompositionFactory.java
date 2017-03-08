package gov.lanl.micot.infrastructure.optimize.sbd;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * A factory interface for creating scenario based decomposition algorithms
 * @author Russell Bent
 */
public interface ScenarioBasedDecompositionFactory<N extends Node, M extends Model> {

  /**
   * Add outer variable factories to the problem
   * @param flags
   * @param simulator
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void addOuterVariableFactories(OptimizerFlags flags, ScenarioBasedDecompositionOptimizer<N, M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException;
	
  /**
   * Add inner variable factories to the problem
   * @param flags
   * @param simulator
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void addInnerVariableFactories(OptimizerFlags flags, ScenarioBasedDecompositionOptimizer<N, M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException;

  /**
   * Add an outer constraints
   * @param flags
   * @param simulator
   */
  public void addOuterConstraints(OptimizerFlags flags, ScenarioBasedDecompositionOptimizer<N, M> simulator);

  /**
   * Add an inner constraints
   * @param flags
   * @param simulator
   */
  public void addInnerConstraints(OptimizerFlags flags, ScenarioBasedDecompositionOptimizer<N, M> simulator);

  /**
   * Add an outer objective function
   * @param flags
   * @param simulator
   */
  public void addOuterObjectiveFunctions(OptimizerFlags flags, ScenarioBasedDecompositionOptimizer<N, M> simulator);

  /**
   * Add an inner objective function
   * @param flags
   * @param simulator
   */
  public void addInnerObjectiveFunctions(OptimizerFlags flags, ScenarioBasedDecompositionOptimizer<N, M> simulator);

  /**
   * Add an outer variable assignment factory
   * @param flags
   * @param simulator
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void addOuterVariableAssignments(OptimizerFlags flags, ScenarioBasedDecompositionOptimizer<N, M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException;

  /**
   * Add an inner variable assignment factory
   * @param flags
   * @param simulator
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void addInnerVariableAssignments(OptimizerFlags flags, ScenarioBasedDecompositionOptimizer<N, M> simulator) throws InstantiationException, IllegalAccessException, ClassNotFoundException;

  
}
