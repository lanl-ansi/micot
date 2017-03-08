package gov.lanl.micot.infrastructure.naturalgas.model;

import gov.lanl.micot.infrastructure.model.Node;


/**
 * Interface for electric power nodes
 * @author Russell Bent
 * Questions posed by Conrado. 
 */
public interface NaturalGasNode extends Node {

	/**
	 * Gets any Junction associated with the node --> (Conrado) QUESTION: Does 'Junction' extend to sources and terminals? Probably yes. 
	 * @return
	 */
	public abstract Junction getJunction();

	/**
   * Gets the first (if any) well associated with the node
	 * @return
	 */
	public abstract Well getWell(); // (Conrado) Obviously a source point/node

	/**
   * Gets the first (if any) city gate associated with the node
	 * @return
	 */
  public abstract CityGate getCityGate(); // (Conrado) I am assuming this is a terminal (demand) point/node

  /**
   * Gets the first (if any) reservoir associated with the node
   * @return
   */
  public abstract Reservoir getReservoir(); // (Conrado) Not sure about this (perhaps also another type of source point/node)


}