package gov.lanl.micot.infrastructure.simulate;

import java.io.IOException;

import gov.lanl.micot.infrastructure.model.Model;
import gov.lanl.micot.infrastructure.project.ProjectConfiguration;
import gov.lanl.micot.infrastructure.project.SimulatorConfiguration;
import gov.lanl.micot.infrastructure.simulate.SimulatorFlags;

/**
 * Interface to simulator factories
 * @author Russell Bent
 */
public interface SimulatorFactory<E extends Model> {

  /**
   * Create a simulator
   * @param flags
   * @return
   * @throws IOException
   * @throws ClassNotFoundException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Simulator<E> createSimulator(SimulatorFlags flags) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException;
  
  
  /**
   * Construct the algorithm from a factory
   * @param configuration
   * @param simulator
   * @param model
   * @return
   */
  public Simulator<E> constructSimulator(ProjectConfiguration projectConfiguration, SimulatorConfiguration configuration, E model);

}
