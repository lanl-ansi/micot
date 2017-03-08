package gov.lanl.micot.util.geometry;

import java.util.Collection;
import java.util.Set;

/**
 * Interface for a multi polygon
 * @author Russell Bent
 */
public interface MultiPolygon extends Polygon {

	/**
	 * Get all the points associated with a polygon
	 * @return
	 */
	public Set<Collection<Point>> getMultiPoints();
	
}