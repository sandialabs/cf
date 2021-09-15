/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import org.apache.commons.lang3.StringUtils;

/**
 * This class gives mathematics methods
 * 
 * @author Didier Verstraete
 *
 */
public class MathTools {

	/** The BASE 10 factor */
	public static final int BASE10FACTOR = 10;

	/**
	 * Private constructor to not allow instantiation.
	 */
	private MathTools() {
	}

	/**
	 * @return an int random value with base 10 factor
	 */
	public static int getRandomIntBase10() {
		return getRandomInt(BASE10FACTOR);
	}

	/**
	 * @param factor the factor to apply to the
	 * @return an int random value with factor parameter applied
	 */
	public static int getRandomInt(int factor) {
		double randomValue = Math.random() * factor;
		return (int) Math.round(randomValue);
	}

	/**
	 * @param int1 the integer 1
	 * @param int2 the integer 2
	 * @return true if the two string are equals or null, otherwise false
	 */
	public static boolean equals(Integer int1, Integer int2) {
		// Initialize
		Boolean isEquals = false;

		// int1 not null
		if (int1 != null) {
			// int1 equals int2
			if (int1.equals(int2)) {
				isEquals = true;
			}
		} else {
			// Both null
			if (int2 == null) {
				isEquals = true;
			}
		}

		// Result
		return isEquals;
	}

	/**
	 * @param str the string to test
	 * @return true if the string is a float, otherwise false
	 */
	public static boolean isInteger(String str) {

		if (StringUtils.isBlank(str)) {
			return false;
		}

		boolean isFloat = true;
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException ex) {
			isFloat = false;
		}
		return isFloat;
	}

	/**
	 * @param str the string to test
	 * @return true if the string is a float, otherwise false
	 */
	public static boolean isFloat(String str) {
		boolean isFloat = true;
		try {
			Float.parseFloat(str);
		} catch (NumberFormatException ex) {
			isFloat = false;
		}
		return isFloat;
	}
}
