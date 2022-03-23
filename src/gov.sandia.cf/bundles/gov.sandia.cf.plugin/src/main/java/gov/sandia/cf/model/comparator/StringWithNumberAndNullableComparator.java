/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.comparator;

import java.io.Serializable;
import java.util.Comparator;
import java.util.regex.Pattern;

/**
 * The version comparator.
 * 
 * This comparator can be used to sort and compare strings with numbers. It
 * places the numbers first as int and compare the strings.
 * 
 * @author Didier Verstraete
 *
 */
public class StringWithNumberAndNullableComparator implements Comparator<String>, Serializable {

	private static final long serialVersionUID = -2727626233938749987L;

	final Pattern pattern = Pattern.compile("^\\d+"); //$NON-NLS-1$

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(String string1, String string2) {

		int compare = 0;

		if (string1 == null || string1.isEmpty()) {
			return (string2 == null || string2.isEmpty()) ? 0 : 1;
		} else if (string2 == null || string2.isEmpty()) {
			return -1;
		} else {

			// tokenize the version strings
			StringWithNumberAndNullableTokenizer token1 = new StringWithNumberAndNullableTokenizer(string1);
			StringWithNumberAndNullableTokenizer token2 = new StringWithNumberAndNullableTokenizer(string2);

			// parse the first version token
			while (token1.moveNext()) {

				// parse the second version token
				if (!token2.moveNext()) {
					// if string 1 is longer than string2, string 2 is the parent
					return 1;
				}

				if (token1.isNumber()) {
					if (token2.isNumber()) {
						if (token1.hasValue()) {
							compare = token1.getNumber().compareTo(token2.getNumber());
						} else {
							compare = token2.hasValue() ? -1 : 0;
						}
						if (compare != 0) {
							break;
						}
					} else {
						// token 2 is greater than token 1
						return -1;
					}
				} else {
					if (!token2.isNumber()) {
						if (token1.hasValue()) {
							compare = token1.getToken().compareTo(token2.getToken());
						} else {
							compare = token2.hasValue() ? -1 : 0;
						}
						if (compare != 0) {
							break;
						}
					} else {
						// token 1 is greater than token 2
						return 1;
					}
				}
			}

			// if token 2 has more chars than token 1, token 2 is greater than token 1
			compare = (!token1.hasValue() && token2.moveNext()) ? -1 : compare;
		}

		return compare;
	}
}
