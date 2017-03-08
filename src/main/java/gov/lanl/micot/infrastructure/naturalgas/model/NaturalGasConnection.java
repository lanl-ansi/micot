package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.Connection;
import gov.lanl.micot.infrastructure.model.FlowConnection;

/**
 * Interface for lines
 * @author Russell Bent
 *
 */
public interface NaturalGasConnection extends Connection, FlowConnection {

  public static final String FLOW_MIN_KEY                  = "FLOW_MIN";
  public static final String FLOW_MAX_KEY                  = "FLOW_MAX";
  
  public static final String FLOW_UNIT_KEY                 = "FLOW_UNIT";
  public static final String DIAMETER_UNIT_KEY             = "DIAMETER_UNIT";

  public static final String DIAMETER_KEY                  = "DIAMETER";
  public static final String RESISTANCE_KEY                = "RESISTANCE";
  
  /**
   * Set the roughness
   * @param roughness
   */
  public void setResistance(double resistance);
  
  /**
   * Get the roughness
   * @return
   */
  public double getResistance();

}
