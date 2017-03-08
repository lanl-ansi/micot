package gov.lanl.micot.infrastructure.simulate;

/**
 * An exception for excecution of pflow
 * @author Russell Bent
 */
public class SimulationExecutionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   * @param message
   */
  public SimulationExecutionException(String message) {
    super(message);
  }
  
}
