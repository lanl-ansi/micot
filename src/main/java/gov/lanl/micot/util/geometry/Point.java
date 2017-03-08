package gov.lanl.micot.util.geometry;

/**
 * Interface for a point
 * @author Russell Bent
 */
public interface Point extends Geometry {
	
	/**
	 * Get the x coordinate
	 * @return
	 */
	public double getX();
	
	/**
	 * Get the y coordinate
	 * @return
	 */
	public double getY();
	
	/**
	 * Get the distance to another point
	 * @param point
	 * @return
	 */
	public double distance(Point point);
	
}
