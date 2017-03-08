package gov.lanl.micot.util.math;

import java.util.Collection;

/**
 * An interface for specific things associated with polynomials
 * @author Russell Bent
 */
public interface PolynomialFunction extends Function {

	/**
	 * Get all the coefficients of the polynomial
	 * @return
	 */
	public Collection<Double> getPolynomialCoefficients();

}
