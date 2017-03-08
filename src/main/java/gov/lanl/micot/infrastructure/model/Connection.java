package gov.lanl.micot.infrastructure.model;

import gov.lanl.micot.util.geometry.Line;

/**
 * Interface for lines
 * @author Russell Bent
 *
 */
public interface Connection extends Asset {
	    
	/**
	 * Get the collection of the coordinates associated the link
	 * @return
	 */
	public Line getCoordinates();
	
	/**
	 * Sets the coordinates
	 * @param points
	 */
	public void setCoordinates(Line points);
		
}
