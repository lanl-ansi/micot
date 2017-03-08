package gov.lanl.micot.infrastructure.application;

/**
 * Generic application class for combining capability into an application that solves
 * a problem of interest
 * @author Russell Bent
 *
 */
public interface Application {
  
  /**
   * Execute the application of interest
   */
  public ApplicationOutput execute();
  
}
