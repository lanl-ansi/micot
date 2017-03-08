package gov.lanl.micot.util.math;

import java.util.Date;

/**
 * This is an interface for defining a function for an attribute that evolves over time
 * An example would include demand functions, where demand for a commodity changes over time
 * @author Russell Bent
 */
public abstract class TimeDependentFunction extends Number implements Comparable<TimeDependentFunction>, Function {

	private static final long	serialVersionUID	= 1L;

	/**
	 * Get the value for a time defined as a double
	 * @param time
	 * @return
	 */
	public abstract Number getValue(double time);
	
	/**
	 * Get the value for a time define as a specific date
	 * @param time
	 * @return
	 */
	public abstract Number getValue(Date time);
	
	/**
	 * A function for computing the signed norm of two functions
	 * @param number
	 * @return
	 */
	public abstract Number computeSignedNorm(Number number);
	
	/**
	 * Calculate the sum (derivative of the function).
	 * @return
	 */
	public abstract Number sum();

	/**
	 * Calculate the sum (derivative of the function).
	 * @return
	 */
	public abstract Number max();

	/**
	 * Calculate the sum (derivative of the function).
	 * @return
	 */
	public abstract Number min();
	
	/**
	 * Determine if this function is ever greater than another number
	 * @param number
	 * @return
	 */
	public abstract boolean isEverGreaterThan(Number number);
	
	/**
	 * Determine if this function is ever greater than or equal to another number
	 * @param number
	 * @return
	 */
	public abstract boolean isEverGreaterEqualThan(Number number);

	/**
	 * Determine if this function is ever greater than another number
	 * @param number
	 * @return
	 */
	public abstract boolean isEverLessThan(Number number);
	
	/**
	 * Determine if this function is ever greater than or equal to another number
	 * @param number
	 * @return
	 */
	public abstract boolean isEverLessEqualThan(Number number);
	
	/**
	 * Divide this function by a number
	 * @param number
	 * @return
	 */
	public abstract Number divideBy(Number number);

	 /**
   * Divide this function by a number
   * @param number
   * @return
   */
  public abstract Number inverseDivideBy(Number number);

	
	/**
   * Addition this function by a number
   * @param number
   * @return
   */
  public abstract Number addBy(Number number);

  /**
   * Addition this function by a number
   * @param number
   * @return
   */
  public abstract Number subtractBy(Number number);

  /**
   * Addition this function by a number
   * @param number
   * @return
   */
  public abstract Number inverseSubtractBy(Number number);

  
	/**
	 * Take the absolute value of a function
	 * @return
	 */
	public abstract TimeDependentFunction abs();
	
	/**
	 * Multiply by the function by a number
	 * @param number
	 * @return
	 */
	public abstract Number multiplyBy(Number number);
	
	@Override
	public int compareTo(TimeDependentFunction fcn) {
		return new Double(sum().doubleValue()).compareTo(fcn.sum().doubleValue());
	}
	
	/**
	 * Compute a max function
	 * @param num
	 * @return
	 */
	public abstract Number max(Number num);

	/**
	 * Compute a min function
	 * @param num
	 * @return
	 */
  public abstract Number min(Number num);

  /**
   * Convert 
   * @return
   */
  public abstract Number toDegrees();
  
  @Override
  public double evaluate(double x) {
  	return getValue(x).doubleValue();
  }

  /**
   * Get the last value of a time dependent function
   * @return
   */
  public abstract double lastDoubleValue();

	
}
