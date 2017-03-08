package gov.lanl.micot.infrastructure.transportation.road.model;

import gov.lanl.micot.infrastructure.model.Node;


/**
 * Interface for road nodes
 * @author Russell Bent
 */
public interface RoadNode extends Node {

	/**
	 * Gets any intersection associated with the node
	 * @return
	 */
	public abstract Intersection getIntersection();

}