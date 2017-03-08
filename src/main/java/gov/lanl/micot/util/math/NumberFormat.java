package gov.lanl.micot.util.math;

/**
 * Some common utilities for formatting numbers
 * @author Russell Bent
 */
public class NumberFormat {

	/**
	 * Constructor Not to be instantiated
	 */
	private NumberFormat() {
	}
	
	/**
	 * trims the string by a certain amount
	 * @param str
	 * @param size
	 * @return
	 */
	public static String trim(String str, int size) {
		if (str.length() <= size) {
			return str;
		}
		return str.substring(0,size);
	}

	/**
	 * Adds white space before str
	 * @param str
	 * @param size
	 * @return
	 */
	public static String padBefore(String str, int size) {		
		while (str.length() < size) {
			str = " " + str;
		}
		return str;
	}
	
	/**
	 * Adds white space after str
	 * @param str
	 * @param size
	 * @return
	 */
	public static String padAfter(String str, int size) {		
		while (str.length() < size) {
			str = str + " ";
		}
		return str;
	}

	/**
	 * resizing function
	 * @param str
	 * @param size
	 * @return
	 */
	public static String resizeAfter(String str, int size) {
		str = trim(str,size);
		return padAfter(str,size);
	}
	
	/**
	 * resizing function
	 * @param str
	 * @param size
	 * @return
	 */
	public static String resizeBefore(String str, int size) {
		str = trim(str,size);
		return padBefore(str,size);
	}
	
	/**
	 * creates a string format based on significant digits
	 * @param value
	 * @param maxDigits
	 * @param maxDecimal
	 * @return
	 */
	public static String createSignificantDigitFormat(double value, int maxDigits, int maxDecimal) {
		double abs = Math.abs(value);		
		if (abs == 0) {
			return 	"%" + maxDigits + "." + 1 + "f";
		}
		else if (abs < 10) {
			return 	"%" + maxDigits + "." + maxDecimal + "f";
		}
		else if (abs < 100) {
			return 	"%" + maxDigits + "." + (maxDecimal-1) + "f";
		}
		else if (abs < 1000) {
			return 	"%" + maxDigits + "." + (maxDecimal-2) + "f";
		}
		else if (abs < 10000) {
			return 	"%" + maxDigits + "." + (maxDecimal-3) + "f";
		}
		else if (abs < 100000) {
			return 	"%" + maxDigits + "." + (maxDecimal-4) + "f";
		}
		return 	"%" + maxDigits + "." + maxDecimal + "f";
	}
}
