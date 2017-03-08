package gov.lanl.micot.util.geometry;

import java.util.Set;

/**
 * Interface for a a multi point
 * @author Russell Bent
 */
public interface MultiPoint extends Point {

	public Set<Point> getPoints();
	
}
