package gov.lanl.micot.util.math;

import java.awt.geom.Point2D;
import java.util.Collection;

import gov.lanl.micot.util.collection.Pair;

/**
 * Factory for creating piecewise linear functions
 * @author Russell Bent
 */
public class PiecewiseLinearFunctionFactory {

	private static PiecewiseLinearFunctionFactory INSTANCE = null;
	
	public static PiecewiseLinearFunctionFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PiecewiseLinearFunctionFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Singleton constructor
	 */
	private PiecewiseLinearFunctionFactory() {		
	}
	
	/**
	 * Create a polynomial function based on the colt platform
	 * @param coefficients
	 * @return
	 */
	public Function createPiecewiseLinearFunction(Collection<Point2D> coefficients) {
		return new PiecewiseLinearFunction(coefficients);
	}
	
	/**
	 * Create the default polynomial function
	 * @param coefficients
	 * @return
	 */
	public Function createDefaultPiecewiseLinearFunction(Collection<Point2D> coefficients) {
		return createPiecewiseLinearFunction(coefficients);
	}
	
}
