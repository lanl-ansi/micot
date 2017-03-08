package gov.lanl.micot.util.math;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

/**
 * This is a time dependent function that simply uses a look up table
 * 
 * @author Russell Bent
 */
public class LookupTableTimeDependentFunction extends TimeDependentFunction {

	private static final long serialVersionUID = 1L;
	private TreeMap<Double, Number> lookupTable = null;

	/**
	 * Constructor
	 */
	public LookupTableTimeDependentFunction() {
		lookupTable = new TreeMap<Double, Number>();
	}

	/**
	 * Constructor
	 * 
	 * @param map
	 */
	public LookupTableTimeDependentFunction(Map<Double, Number> map) {
		lookupTable = new TreeMap<Double, Number>();
		lookupTable.putAll(map);
	}

	@Override
	public Number getValue(double time) {
		if (lookupTable.get(time) != null) {
			return lookupTable.get(time);
		}

		Double firstEntry = lookupTable.floorKey(time);
		Double secondEntry = lookupTable.ceilingKey(time);

		double firstValue = lookupTable.get(firstEntry).doubleValue();

		if (secondEntry == null) {
			return firstValue;
		}

		double secondValue = lookupTable.get(secondEntry).doubleValue();

		// do a linear interperlation
		return firstValue + ((secondValue - firstValue) / 2.0);
	}

	@Override
	public Number getValue(Date time) {
		return getValue(time.getTime());
	}

	/**
	 * Add an entry to the table
	 * 
	 * @param time
	 * @param value
	 */
	public void addEntry(Double time, Number value) {
		lookupTable.put(time, value);
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
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();

		for (double key : lookupTable.keySet()) {

			double num2 = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			double num1 = getValue(key).doubleValue();
			double square = (num2 * num2) + (num1 * num1);
			double value = Math.sqrt(square);
			if (num1 < 0) {
				function.addEntry(key, -value);
			} else {
				function.addEntry(key, value);
			}
		}
		return function;
	}

	@Override
	public Number sum() {
		double sum = 0;
		for (Number key : lookupTable.keySet()) {
			sum += lookupTable.get(key).doubleValue();
		}
		return sum;
	}

	@Override
	public Number max() {
		Number max = null;
		for (Number key : lookupTable.keySet()) {
			if (max == null) {
				max = lookupTable.get(key);
			}
			max = Math.max(max.doubleValue(), lookupTable.get(key)
					.doubleValue());
		}
		return max;
	}

	@Override
	public Number min() {
		Number min = null;
		for (Number key : lookupTable.keySet()) {
			if (min == null) {
				min = lookupTable.get(key);
			}
			min = Math.min(min.doubleValue(), lookupTable.get(key)
					.doubleValue());
		}
		return min;
	}

	@Override
	public TimeDependentFunction abs() {
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			function.addEntry(key, getValue(key));
		}
		return function;
	}

	@Override
	public boolean isEverGreaterThan(Number number) {
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			if (MathUtils.DOUBLE_GREATER_THAN(getValue(key).doubleValue(),
					value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEverGreaterEqualThan(Number number) {
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			if (MathUtils.DOUBLE_GREATER_EQUAL_THAN(
					getValue(key).doubleValue(), value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEverLessThan(Number number) {
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			if (MathUtils.DOUBLE_LESS_THAN(getValue(key).doubleValue(), value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isEverLessEqualThan(Number number) {
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			if (MathUtils.DOUBLE_LESS_EQUAL_THAN(getValue(key).doubleValue(),
					value)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Number divideBy(Number number) {
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			function.addEntry(key, getValue(key).doubleValue() / value);
		}
		return function;
	}

	@Override
	public Number inverseDivideBy(Number number) {
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			function.addEntry(key, value / getValue(key).doubleValue());
		}
		return function;
	}

	@Override
	public Number multiplyBy(Number number) {
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			function.addEntry(key, getValue(key).doubleValue() * value);
		}
		return function;
	}

	@Override
	public Number addBy(Number number) {
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			function.addEntry(key, getValue(key).doubleValue() + value);
		}
		return function;
	}

	@Override
	public Number subtractBy(Number number) {
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			function.addEntry(key, getValue(key).doubleValue() - value);
		}
		return function;
	}

	@Override
	public Number inverseSubtractBy(Number number) {
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			double value = number instanceof TimeDependentFunction ? ((TimeDependentFunction) number)
					.getValue(key).doubleValue() : number.doubleValue();
			function.addEntry(key, value - getValue(key).doubleValue());
		}
		return function;
	}

	@Override
	public Number max(Number num) {
		TimeDependentFunction fcn = null;
		if (num instanceof TimeDependentFunction) {
			fcn = (TimeDependentFunction) fcn;
		} else {
			fcn = new ConstantTimeDependentFunction(num.doubleValue());
		}
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			function.addEntry(
					key,
					Math.max(lookupTable.get(key).doubleValue(),
							fcn.getValue(key).doubleValue()));
		}
		return function;

		// double max = doubleValue();
		// for (Double key : lookupTable.keySet()) {
		// max = Math.max(lookupTable.get(key).doubleValue(),max);
		// }
		// return max;
	}

	@Override
	public Number min(Number num) {
		TimeDependentFunction fcn = null;
		if (num instanceof TimeDependentFunction) {
			fcn = (TimeDependentFunction) fcn;
		} else {
			fcn = new ConstantTimeDependentFunction(num.doubleValue());
		}
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			function.addEntry(
					key,
					Math.min(lookupTable.get(key).doubleValue(),
							fcn.getValue(key).doubleValue()));
		}
		return function;

		// double min = doubleValue();
		// for (Double key : lookupTable.keySet()) {
		// min = Math.min(lookupTable.get(key).doubleValue(),min);
		// }
		// return min;
	}

	@Override
	public Number toDegrees() {
		LookupTableTimeDependentFunction function = new LookupTableTimeDependentFunction();
		for (Double key : lookupTable.keySet()) {
			double value = Math.toDegrees(getValue(key).doubleValue());
			function.addEntry(key, value);
		}
		return function;
	}

	@Override
	public int getNumOfDataPoints() {
		return lookupTable.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		for (Double key : lookupTable.keySet()) {
			result = prime * result + key.hashCode();
			result = prime * result + lookupTable.get(key).hashCode();

		}
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof LookupTableTimeDependentFunction)) {
			return false;
		}

		LookupTableTimeDependentFunction fcn = (LookupTableTimeDependentFunction) obj;

		for (Double key : lookupTable.keySet()) {
			if (fcn.lookupTable.get(key) == null) {
				return false;
			}
			if (!lookupTable.get(key).equals(fcn.lookupTable.get(key))) {
				return false;
			}
		}

		// just check to make sure there are no other keys floating around
		for (Double key : fcn.lookupTable.keySet()) {
			if (lookupTable.get(key) == null) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return lookupTable.toString();
	}

	@Override
	public Collection<Point2D> getDataPoints() {
		Collection<Point2D> points = new ArrayList<Point2D>();
		for (double key : lookupTable.keySet()) {
			points.add(new Point2D.Double(key, lookupTable.get(key)
					.doubleValue()));
		}
		return points;
	}

	@Override
	public double lastDoubleValue() {
		return lookupTable.lastEntry().getValue().doubleValue();
	}

}
