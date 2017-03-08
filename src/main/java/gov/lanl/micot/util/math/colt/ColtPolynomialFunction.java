package gov.lanl.micot.util.math.colt;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;

import cern.jet.math.Polynomial;
import gov.lanl.micot.util.math.PolynomialFunction;

/**
 * This is polynomial function based on the colt platform
 * @author Russell Bent
 */
public class ColtPolynomialFunction implements PolynomialFunction {

	private double[] coefficients;
	
	/**
	 * Constructor	
	 * @param coefficients
	 */
	public ColtPolynomialFunction(Collection<Double> coefficients) {
		Double[] temp = coefficients.toArray(new Double[0]);
		this.coefficients = new double[temp.length];
		for (int i = 0; i < temp.length; ++i) {
			this.coefficients[i] = temp[i];
		}
	}
		
	@Override
	public double evaluate(double x) {
		return Polynomial.polevl(x, coefficients, coefficients.length);		
	}

	@Override
	public int getNumOfDataPoints() {
		return coefficients.length;
	}

	@Override
	public Collection<Double> getPolynomialCoefficients() {
		ArrayList<Double> points= new ArrayList<Double>();
		for (int i = 0; i < coefficients.length; ++i) {
			points.add(coefficients[i]);
		}
		return points;
	}

	@Override
	public Collection<Point2D> getDataPoints() {
		throw new RuntimeException("Error: Number of data points for polynomial function is indeterminant");
	}

}
