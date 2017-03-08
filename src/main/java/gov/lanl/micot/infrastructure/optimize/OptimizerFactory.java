package gov.lanl.micot.infrastructure.optimize;

import java.io.IOException;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.model.Node;
import gov.lanl.micot.infrastructure.project.AlgorithmConfiguration;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;

/**
 * Interface to optimizer factories
 * @author Russell Bent
 */
public interface OptimizerFactory<N extends Node, M extends Model> {

 
  /**
   * Create a simulator
   * @param flags
   * @return
   * @throws IOException
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Optimizer<N, M> createOptimizer(OptimizerFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException;
    
  /**
   * Construct the algorithm from a factory
   * @param configuration
   * @param simulator
   * @param model
   * @return
   */
  public Optimizer<N, M> constructOptimizer(ProjectConfiguration projectConfiguration, AlgorithmConfiguration configuration, M model);
  
  
}
