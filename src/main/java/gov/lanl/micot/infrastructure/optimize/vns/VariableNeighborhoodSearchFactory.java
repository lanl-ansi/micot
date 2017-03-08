package gov.lanl.micot.infrastructure.optimize.vns;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.optimize.OptimizerFlags;

/**
 * A factory interface for creating variable Neighborhood search algorithms
 * @author Russell Bent
 */
public interface VariableNeighborhoodSearchFactory<N extends Node, M extends Model> {

  /**
   * Add the variable factories to the problem
   * @param flags
   * @param simulator
   * @throws InstantiationException
   * @throws IllegalAccessException
   * @throws ClassNotFoundException
   */
  public void addVariableFactories(OptimizerFlags flags, VariableNeighborhoodSearchOptimizer<N, M> optimizer) throws InstantiationException, IllegalAccessException, ClassNotFoundException;

  /**
   * Add the constraints
   * @param flags
   * @param simulator
   */
  public void addConstraints(OptimizerFlags flags, VariableNeighborhoodSearchOptimizer<N, M> optimizer);


  /**
   * Add the objective function
   * @param flags
   * @param simulator
   */
  public void addObjectiveFunctions(OptimizerFlags flags, VariableNeighborhoodSearchOptimizer<N, M> optimizer);

  /**
   * Add variable assignment factory
   * @param flags
   * @param simulator
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public void addVariableAssignments(OptimizerFlags flags, VariableNeighborhoodSearchOptimizer<N, M> optimizer) throws InstantiationException, IllegalAccessException, ClassNotFoundException;

  
}
