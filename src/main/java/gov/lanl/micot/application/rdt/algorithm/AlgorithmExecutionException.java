package gov.lanl.micot.application.rdt.algorithm;

/**
 * An exception for algorithm execution
 * @author Russell Ben
 */
public class AlgorithmExecutionException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  /**
   * Constructor
   * @param message
   */
  public AlgorithmExecutionException(String message) {
    super(message);
  }
  
}
