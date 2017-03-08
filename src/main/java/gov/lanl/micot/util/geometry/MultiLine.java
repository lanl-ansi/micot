package gov.lanl.micot.util.geometry;

import java.util.Collection;
import java.util.Set;

/**
 * Interface for a multi line
 * @author Russell Bent
 */
public interface MultiLine extends Line {

	/**
	 * Get all the points assoicated with a line
	 * @return
	 */
	public Set<Collection<Point>> getMultiPoints();
	
}
