package gov.lanl.micot.util.math;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

/**
 * Piecewise linear function implementation
 * @author Russell Bent
 */
public class PiecewiseLinearFunction implements Function {

	private TreeMap<Double,Double> points = null;
		
	/**
	 * Constructor
	 * @param coefficients
	 */
	protected PiecewiseLinearFunction(Collection<Point2D> coefficients) {
		points = new TreeMap<Double,Double>();
		for (Point2D pair : coefficients) {
			points.put(pair.getX(), pair.getY());
		}
	}

	@Override
	public double evaluate(double x) {
		double x1 = points.floorKey(x);
		double x2 = points.ceilingKey(x);
		double y1 = points.get(x1);
		double y2 = points.get(x2);
		double slope = (y2-y1) / (x2 - x1);
		return slope *(x-x1) + y1;
	}

	/**
	 * Get at all the data points associated with the linear function
	 * @return
	 */
	public Collection<Point2D> getDataPoints() {
		ArrayList<Point2D> array = new ArrayList<Point2D>();
		for (Double x : points.keySet()) {
			array.add(new Point2D.Double(x,points.get(x)));
		}
		return array;
	}

	@Override
	public int getNumOfDataPoints() {
		return points.size();
	}

}
