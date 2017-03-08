package gov.lanl.micot.infrastructure.coupled.model;

import gov.lanl.micot.infrastructure.model.Asset;
import gov.lanl.micot.infrastructure.model.Component;

/**
 * This is an interface for listening to changes in a coupled model's
 * structure
 * @author Russell Bent
 */
public interface CoupledModelListener {

	/**
	 * Notification that a link has been added to the model
	 * @param link
	 */
	public void linkAdded(Coupling link);
	
	/**
	 * Notification that a link has been removed from the model
	 * @param link
	 */
	public void linkRemoved(Coupling link);

	/**
	 * Notification that a component has been added
	 * @param component
	 */
  public void componentAdded(CoupledComponent component);

  /**
   * Notification that a coupled compnent has been removed
   * @param component
   */
  public void componentRemoved(CoupledComponent component);

  /**
   * Notification that a coupling data has changed
   * @param coupling
   */
  public void couplingDataChange(Coupling coupling);
		
}
