package gov.lanl.micot.infrastructure.application;

import java.io.IOException;

import gov.lanl.micot.infrastructure.project.ProjectConfiguration;

/**
 * Generic interface for creating applications
 * @author Russell Bent
 */
public interface ApplicationFactory {

  /**
   * creates the application from the project
   * @param configuration
   * @return
   * @throws ClassNotFoundException 
   * @throws IOException 
   * @throws IllegalAccessException 
   * @throws InstantiationException 
   */
  public Application createApplication(ProjectConfiguration configuration) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException;
  
}
