package gov.lanl.micot.util.math;

/**
 * Some useful math utilities
 * @author Russell Bent
 */
public class MathUtils {
  
	public static double threshold = 1e-6;
	
  /**
   * Floating point equality sign
   * @param a first floating point
   * @param b second floating point
   * @return if a or b are equal
   */
  public static boolean DOUBLE_EQUAL(double a, double b) { 
    return a == b || Math.abs(a - b) <= threshold; 
  }
  
  /**
   * Floating point greater than
   * @param a first floating point
   * @param b second floating point
   * @return if a > b
   */
  public static boolean DOUBLE_GREATER_THAN(double a, double b) { 
    return !DOUBLE_EQUAL(a,b) && a - b > threshold; 
  }

  /**
   * Floating point greater than
   * @param a first floating point
   * @param b second floating point
   * @return if a > b
   */
  public static boolean NUMBER_GREATER_THAN(Number a, Number b) {
    if (a instanceof TimeDependentFunction) {
      return(((TimeDependentFunction) a).isEverGreaterThan(b));
    }
    return DOUBLE_GREATER_THAN(a.doubleValue(), b.doubleValue());
  }

  
  /**
   * Floating point less than
   * @param a first floating point
   * @param b second floating point
   * @return if a < b
   */
  public static boolean DOUBLE_LESS_THAN(double a, double b) { 
    return !DOUBLE_EQUAL(a,b) && a - b < -threshold; 
  }

  /**
   * Floating point less than
   * @param a first floating point
   * @param b second floating point
   * @return if a < b
   */
  public static boolean NUMBER_LESS_THAN(Number a, Number b) { 
    if (a instanceof TimeDependentFunction) {
      return(((TimeDependentFunction) a).isEverLessThan(b));
    }
    return DOUBLE_LESS_THAN(a.doubleValue(), b.doubleValue());
  }

  
  /**
   * Floating point less than or equal
   * @param a first floating point
   * @param b second floating point
   * @return if a <= b
   */
  public static boolean DOUBLE_LESS_EQUAL_THAN(double a, double b) { 
    return DOUBLE_EQUAL(a,b) || a - b < -threshold; 
  }
  
  /**
   * Floating point greater than or equal
   * @param a first floating point
   * @param b second floating point
   * @return if a >= b
   */
  public static boolean DOUBLE_GREATER_EQUAL_THAN(double a, double b) { 
    return DOUBLE_EQUAL(a,b) || a - b > threshold; 
  }
  
  /**
   * A little function to reduce the precision of a double value
   * @param value
   * @return
   */
  public static double REDUCE_PRECISION(double value) {
  	String str = new Double(value).toString();
  	str = str.substring(0,str.length()-1);
  	return new Double(str);
  }
  
  /**
   * This function produces the sum (area under a curve) for a number
   * @param number
   * @return
   */
  public static double SUM(Number number) {
    if (number instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number).sum().doubleValue();
    }
    return number.doubleValue();
  }
  
  /**
   * Computed the signed norm (based on first number) for two number
   * @param number1
   * @param number2
   * @return
   */
  public static Number SIGNED_NORM(Number number1, Number number2) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).computeSignedNorm(number2);
    }
    double value = Math.sqrt((number1.doubleValue() * number1.doubleValue()) + (number2.doubleValue() * number2.doubleValue()));
    if (number1.doubleValue() < 0) {
      return -value;
    }
    return value;
  }
  
  /**
   * Multiply two numbers together
   * @param number1
   * @param number2
   * @return
   */
  public static Number MULTIPLY(Number number1, Number number2) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).multiplyBy(number2);
    }
    if (number2 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number2).multiplyBy(number1);
    }
    return number1.doubleValue() * number2.doubleValue();
  }

  /**
   * Multiply two numbers together
   * @param number1
   * @param number2
   * @return
   */
  public static Number ADD(Number number1, Number number2) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).addBy(number2);
    }
    if (number2 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number2).addBy(number1);
    }
    return number1.doubleValue() + number2.doubleValue();
  }

  /**
   * Multiply two numbers together
   * @param number1
   * @param number2
   * @return
   */
  public static Number SUBTRACT(Number number1, Number number2) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).subtractBy(number2);
    }
    if (number2 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number2).inverseSubtractBy(number1);
    }
    return number1.doubleValue() - number2.doubleValue();
  }
  
  /**
   * Multiply two numbers together
   * @param number1
   * @param number2
   * @return
   */
  public static Number DIVIDE(Number number1, Number number2) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).divideBy(number2);
    }
    if (number2 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number2).inverseDivideBy(number1);
    }

    return number1.doubleValue() / number2.doubleValue();
  }

  /**
   * Compute the absolute value
   * @param number1
   * @return
   */
  public static Number ABS(Number number1) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).abs();
    }
    return Math.abs(number1.doubleValue());
  }

  /**
   * This function produces the Max
   * @param number
   * @return
   */
  public static Number MAX(Number number) {
    if (number instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number).max();
    }
    return number.doubleValue();
  }

  /**
   * This function produces the Max
   * @param number
   * @return
   */
  public static Number MIN(Number number) {
    if (number instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number).min();
    }
    return number.doubleValue();
  }


  /**
   * Comparison function
   * @param number1
   * @param number2
   * @return
   */
  public static int COMPARE_TO(Number number1, Number number2) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).compareTo((TimeDependentFunction) number2);
    }
    return new Double(number1.doubleValue()).compareTo(number2.doubleValue());
  }

  /**
   * Max function
   * @param number1
   * @param number2
   * @return
   */
  public static Number MAX(Number number1, Number number2) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).max(number2);
    }
    return Math.max(number1.doubleValue(),number2.doubleValue());
  }

  /**
   * Min function
   * @param number1
   * @param number2
   * @return
   */
  public static Number MIN(Number number1, Number number2) {
    if (number1 instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number1).min(number2);
    }
    return Math.min(number1.doubleValue(),number2.doubleValue());
  }

  /**
   * Convert from radians to degrees
   * @param number
   */
  public static Number TO_DEGREES(Number number) {
  	if (number instanceof TimeDependentFunction) {
      return ((TimeDependentFunction) number).toDegrees();
    }
    return Math.toDegrees(number.doubleValue());
  }
  
  /**
   * Do a conversion to a time dependent function
   * @param number
   * @return
   */
  public static TimeDependentFunction TO_FUNCTION(Number number) {
    if (number instanceof TimeDependentFunction) {
      return (TimeDependentFunction) number;
    }
    return new ConstantTimeDependentFunction(number);
  }

}
