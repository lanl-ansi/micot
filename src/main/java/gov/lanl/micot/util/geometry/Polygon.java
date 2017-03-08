package gov.lanl.micot.util.geometry;

import java.util.Collection;

/**
 * Interface for a polygon
 * @author Russell Bent
 */
public interface Polygon extends Geometry {

	/**
	 * Get all the points associated with a polygon
	 * @return
	 */
	public Collection<Point> getPoints();
	
}