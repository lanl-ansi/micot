package gov.lanl.micot.util.math;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * This is a time dependent function that simply returns a constant
 * @author Russell Bent
 */
public class ConstantTimeDependentFunction extends TimeDependentFunction {

	private static final long	serialVersionUID	= 1L;
	private Number constant = null;

	/**
	 * Constructor
	 * @param map
	 */
	public ConstantTimeDependentFunction(Number constant) {
	  if (constant == null) {
	    System.err.println("Error: creating a constant time dependent function with a null constant");
	  }
		this.constant = constant;
	}
		
	@Override
	public Number getValue(double time) {
		return constant;
	}

	@Override
	public Number getValue(Date time) {
		return getValue(time.getTime());
	}

	@Override
	public double doubleValue() {
		return getValue(0).doubleValue();
	}

	@Override
	public float floatValue() {
		return getValue(0).floatValue();
	}

	@Override
	public int intValue() {
		return getValue(0).intValue();
	}

	@Override
	public long longValue() {
		return getValue(0).longValue();
	}

	@Override
	public Number computeSignedNorm(Number number) {
		double num2 = number.doubleValue();
    double num1 = doubleValue();
    double square = (num2 * num2) + (num1 * num1);
    double value = Math.sqrt(square);
    if (num1 < 0) {
      return -value;
    }
    return value;
	}

	@Override
	public Number sum() {
		return new ConstantTimeDependentFunction(constant);
	}

	@Override
	public Number max() {
		return constant;
	}

	@Override
	public Number min() {
		return constant;
	}
	
	@Override
	public TimeDependentFunction abs() {
		return new ConstantTimeDependentFunction(Math.abs(constant.doubleValue()));
	}
	
	@Override
	public boolean isEverGreaterThan(Number number) {
		return MathUtils.DOUBLE_GREATER_THAN(doubleValue(), number.doubleValue());
	}

	@Override
	public boolean isEverGreaterEqualThan(Number number) {
		return MathUtils.DOUBLE_GREATER_THAN(doubleValue(), number.doubleValue());
	}

	@Override
	public boolean isEverLessThan(Number number) {
		return MathUtils.DOUBLE_LESS_THAN(doubleValue(), number.doubleValue());
	}

	@Override
	public boolean isEverLessEqualThan(Number number) {
		return MathUtils.DOUBLE_LESS_THAN(doubleValue(), number.doubleValue());
	}

	
	@Override
	public Number divideBy(Number number) {
		return doubleValue() / number.doubleValue();
	}

	@Override
	public Number inverseDivideBy(Number number) {
	  return number.doubleValue() / doubleValue();
	}
	
	@Override
	public Number multiplyBy(Number number) {
		return doubleValue() * number.doubleValue();
	}
	
	@Override
	public Number addBy(Number number) {
	  return doubleValue() + number.doubleValue();
	}
	  
	@Override
	public Number subtractBy(Number number) {
	   return doubleValue() - number.doubleValue();
	}

	@Override
	public Number inverseSubtractBy(Number number) {
	  return number.doubleValue() - doubleValue();
	}

  @Override
  public Number max(Number num) {
    return Math.max(constant.doubleValue(),num.doubleValue());
  }

  @Override
  public Number min(Number num) {
    return Math.min(constant.doubleValue(),num.doubleValue());
  }

	@Override
	public Number toDegrees() {
		return Math.toDegrees(doubleValue());
	}

	@Override
	public int getNumOfDataPoints() {
		return 1;
	}

	@Override
	public Collection<Point2D> getDataPoints() {
		Collection<Point2D> points = new ArrayList<Point2D>();
		points.add(new Point2D.Double(0.0,constant.doubleValue()));
		return points;
	}

  @Override
  public double lastDoubleValue() {
    return doubleValue();
  }

}
