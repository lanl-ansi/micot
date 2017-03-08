package gov.lanl.micot.util.geometry;

import java.util.Collection;

/**
 * Interface for a line
 * @author Russell Bent
 */
public interface Line extends Geometry {

	/**
	 * Get all the points associated with a line
	 * @return
	 */
	public Collection<Point> getPoints();
	
}
