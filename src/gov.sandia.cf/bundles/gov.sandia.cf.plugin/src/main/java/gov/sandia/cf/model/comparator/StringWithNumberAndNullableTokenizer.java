/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.comparator;

/**
 * The string with numbers tokenizer.
 * 
 * This classes is used by the string with numbers comparator to
 * 
 * @author Didier Verstraete
 *
 */
public class StringWithNumberAndNullableTokenizer {

	// final variables
	private final String string;
	private final int length;

	// cursor variables
	private int position;
	private String token;
	private boolean hasValue;
	private boolean isNumber;

	/**
	 * The constructor
	 * 
	 * @param string the version
	 */
	public StringWithNumberAndNullableTokenizer(String string) {
		this.string = string;
		this.length = string != null ? string.length() : 0;
	}

	/**
	 * @return the token suffix
	 */
	public String getToken() {
		return token;
	}

	/**
	 * Gets the number.
	 *
	 * @return the number
	 */
	public Integer getNumber() {
		return isNumber ? Integer.parseInt(token) : null;
	}

	/**
	 * Checks if is number.
	 *
	 * @return true, if is number
	 */
	public boolean isNumber() {
		return isNumber;
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

		token = ""; //$NON-NLS-1$
		hasValue = false;

		// No more characters
		if (position >= this.length || string == null) {
			return false;
		}

		hasValue = true;
		char firstChar = this.string.charAt(position);
		isNumber = (firstChar >= '0' && firstChar <= '9');
		int numberStart = position;

		// parse the string to the end
		while (position < this.length) {
			char c = this.string.charAt(position);
			if ((isNumber && (c < '0' || c > '9')) || (!isNumber && (c >= '0' && c <= '9'))) {
				break;
			}
			position++;
		}

		// parse to the end of the string
		token = this.string.substring(numberStart, position);

		return true;
	}
}
