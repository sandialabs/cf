/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.comparator;

import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The version number tokenizer.
 * 
 * This classes is used by the version comparator to
 * 
 * @author Didier Verstraete
 *
 */
public class VersionTokenizer {

	/** The NUMBER EMPTY value (used to compare empty strings) */
	public static final int NUMBER_EMPTY = -9999999;
	/** The MAX value */
	public static final int MAX_INT_VALUE = 999;

	// final variables
	private final String version;
	private final int length;

	// cursor variables
	private int position;
	private int number;
	private String suffix;
	private boolean hasValue;

	/**
	 * The constructor
	 * 
	 * @param version the version
	 */
	public VersionTokenizer(String version) {
		if (version == null)
			throw new IllegalArgumentException(RscTools.getString(RscConst.EX_VERSION_TOKENIZER_NULL_PARAM));

		this.version = version;
		this.length = version.length();
	}

	/**
	 * @return the token number
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * @return the token suffix
	 */
	public String getSuffix() {
		return suffix;
	}

	/**
	 * @return true if the token has a value, otherwise false
	 */
	public boolean hasValue() {
		return hasValue;
	}

	/**
	 * Move the cursor to the next token
	 * 
	 * @return true if the token can be moved to the next one, otherwise false
	 */
	public boolean moveNext() {
		number = NUMBER_EMPTY;
		suffix = ""; //$NON-NLS-1$
		hasValue = false;

		// No more characters
		if (position >= this.length) {
			return false;
		}

		hasValue = true;
		number = 0;
		int numberStart = position;

		// parse the version to the end of the number
		while (position < this.length) {
			char c = this.version.charAt(position);
			if (c < '0' || c > '9') {
				break;
			}
			number = number * 10 + (c - '0');

			// if the version number is greater than the int max value, it is considered as
			// a suffix (string)
			if (number > MAX_INT_VALUE) {
				number = NUMBER_EMPTY;
				position = numberStart;
				break;
			}
			position++;
		}

		int suffixStart = position;

		// parse to the end of the point
		while (position < this.length) {
			char c = this.version.charAt(position);
			if (c == '.') {
				break;
			}
			position++;
		}

		// parse to the end of the version
		suffix = this.version.substring(suffixStart, position);

		if (position < this.length) {
			position++;
		}

		return true;
	}
}
