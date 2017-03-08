package gov.lanl.micot.util.math;

import java.awt.geom.Point2D;
import java.util.Collection;

/**
 * Just a generic interface for representing a function
 * @author Russell Bent
 */
public interface Function {

	/**
	 * Evaluate the function at x
	 * @param x
	 * @return
	 */
	public double evaluate(double x);
	
	/**
	 * Get the number of data points associated with this function
	 * @return
	 */
	public int getNumOfDataPoints();
	
	/**
	 * Get the data points associated with this function
	 * @return
	 */
	public Collection<Point2D> getDataPoints();
	
}
