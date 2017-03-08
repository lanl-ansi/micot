package gov.lanl.micot.infrastructure.ep.model;

import gov.lanl.micot.infrastructure.model.Node;


/**
 * Interface for electric power nodes
 * @author Russell Bent
 */
public interface ElectricPowerNode extends Node {

	/**
	 * Gets any bus associated with the node
	 * @return
	 */
	public abstract Bus getBus();

	/**
	 * Gets any shunt associated with the node
	 * @return
	 */
	public abstract ShuntCapacitor getShunt();
	
	/**
	 * Gets an generator associated with the node
	 * @return
	 */
	public abstract Generator getGenerator();

	/**
	 * Get the load associated with the node
	 * @return
	 */
  public abstract Load getLoad();
	

}