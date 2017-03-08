package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Connection;

/**
 * Interface for lines
 * @author Russell Bent
 *
 */
public interface ElectricPowerConnection extends Connection {

  public static final String CIRCUIT_KEY             = "CIRCUIT";

  /**
   * Get a circuit identifier for flow links
   * @return
   */
  public Object getCircuit();
 
}
