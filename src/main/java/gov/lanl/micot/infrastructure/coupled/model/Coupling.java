package gov.lanl.micot.infrastructure.coupled.model;

import gov.lanl.micot.infrastructure.model.FlowConnection;

/**
 * Interface for coupling
 * @author Russell Bent
 */
public interface Coupling extends FlowConnection {	
  
  /**
   * Adds the line data listener
   * @param listener
   */
  public void addCouplingChangeListener(CouplingChangeListener listener);
  
  /**
   * Remove the line data listener
   * @param listener
   */
  public void removeCouplingChangeListener(CouplingChangeListener listener);
  
  /**
   * Clone a line
   * @return
   */
  public Coupling clone();

}
