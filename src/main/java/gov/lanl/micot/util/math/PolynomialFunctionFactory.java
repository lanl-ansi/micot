package gov.lanl.micot.util.math;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;


/**
 * Factory for creating polynomial functions
 * @author Russell Bent
 */
public class PolynomialFunctionFactory {

  public static final String POLYNOMIAL_CLASS = "gov.lanl.micot.util.math.colt.ColtPolynomialFunction";
  
  
	private static PolynomialFunctionFactory INSTANCE = null;
	
	public static PolynomialFunctionFactory getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new PolynomialFunctionFactory();
		}
		return INSTANCE;
	}
	
	/**
	 * Singleton constructor
	 */
	private PolynomialFunctionFactory() {		
	}
	
	
	/**
	 * Create the default polynomial function
	 * @param coefficients
	 * @return
	 */
	public PolynomialFunction createDefaultPolynomialFunction(Collection<Double> coefficients) {
    try {
      return  (PolynomialFunction) Class.forName(POLYNOMIAL_CLASS).getConstructor(Collection.class).newInstance(coefficients);
    }
    catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
      e.printStackTrace();
    }
    return null;
	}
	
}
