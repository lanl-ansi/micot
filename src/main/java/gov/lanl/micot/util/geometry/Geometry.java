package gov.lanl.micot.util.geometry;

import java.util.LinkedList;
import java.util.Set;

/**
 * Interface for a geometry
 * @author Russell Bent
 */
public interface Geometry extends Cloneable {

	/**
	 * Clone based on geometry
	 * @return
	 */
	public Geometry clone();

	/**
	 * Get the number of geometries in this geometry
	 * @return
	 */
	public int getNumGeometries();
	
	/**
	 * Get a set of boundaries for this geometry
	 * @return
	 */
	public Set<LinkedList<Point>> getGeometryBoundaries();
	
	/**
	 * Get points
	 * @return
	 */
	public Point[] getCoordinates();
}

