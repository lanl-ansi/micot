package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.ConnectionImpl;

/**
 * Implementation of the electric power component impl
 * @author Russell Bent
 */
public abstract class ElectricPowerConnectionImpl extends ConnectionImpl implements ElectricPowerConnection {
  
  /**
   * Constructor
   * @param identifierKeys
   */
  public ElectricPowerConnectionImpl() {
    super();
  }
 
  /**
   * Constructor
   * @param component
   * @param identifierKeys
   * @param oredKeys
   * @param additiveKeys
   * @param subtractiveKeys
   */
//  public ElectricPowerConnectionImpl(ElectricPowerConnection edge) {
  //  super(edge);
  //}
  
  @Override
  public Object getCircuit() {
    return getAttribute(CIRCUIT_KEY);
  }

  @Override
  public abstract ElectricPowerConnectionImpl clone();

}
