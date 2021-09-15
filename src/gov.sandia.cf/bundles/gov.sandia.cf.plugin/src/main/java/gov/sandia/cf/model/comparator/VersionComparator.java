/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.comparator;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The version comparator.
 * 
 * This comparator can be used to sort and compare versions. It compares the
 * first numbers separed by a point each after each. A number without suffix is
 * higher than the same number with a suffix. Then the suffix are compared
 * alphanumerically.
 * 
 * @author Didier Verstraete
 *
 */
public class VersionComparator implements Comparator<String>, Serializable {

	private static final long serialVersionUID = -2727626233938749987L;

	private static final String QUALIFIER = "qualifier"; //$NON-NLS-1$

	/**
	 * @param o1 the first string
	 * @param o2 the second string
	 * @return true if o1 equals o2, otherwise false
	 */
	public boolean equals(String o1, String o2) {
		return compare(o1, o2) == 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compare(String version1, String version2) {

		int compare = 0;

		// test version numbers
		if (version1 == null || version1.isEmpty() || version2 == null || version2.isEmpty()) {
			if (version1 == null || version1.isEmpty()) {
				compare = (version2 == null || version2.isEmpty()) ? 0 : -1;
			} else {
				if (version2 == null || version2.isEmpty()) {
					compare = 1;
				}
			}

		} else {

			// tokenize the version strings
			VersionTokenizer versionToken1 = new VersionTokenizer(version1);
			VersionTokenizer versionToken2 = new VersionTokenizer(version2);

			int number1 = 0;
			int number2 = 0;
			String suffix1 = ""; //$NON-NLS-1$
			String suffix2 = ""; //$NON-NLS-1$

			// parse the first version token
			while (versionToken1.moveNext()) {

				// parse the second version token
				if (!versionToken2.moveNext()) {
					do {
						number1 = versionToken1.getNumber();
						suffix1 = versionToken1.getSuffix();
						if (number1 != VersionTokenizer.NUMBER_EMPTY || suffix1.length() != 0) {
							// Version one is longer than number two, and non-zero -> it is not a release
							return -1;
						}
					} while (versionToken1.moveNext());

					// Version one is longer than version two, but zero
					compare = 0;
					break;
				}

				number1 = versionToken1.getNumber();
				suffix1 = versionToken1.getSuffix();
				number2 = versionToken2.getNumber();
				suffix2 = versionToken2.getSuffix();

				// compare numbers
				if (number1 < number2) {
					// Number one is less than number two
					compare = -1;
					break;
				}
				if (number1 > number2) {
					// Number one is greater than number two
					compare = 1;
					break;
				}

				// compare suffix

				// compare empty
				boolean empty1 = suffix1.length() == 0;
				boolean empty2 = suffix2.length() == 0;

				if (empty1 && empty2)
					continue; // No suffixes
				if (empty1) {
					compare = 1; // First suffix is empty (1.2 > 1.2b)
					break;
				}
				if (empty2) {
					compare = -1; // Second suffix is empty (1.2a < 1.2)
					break;
				}

				// compare first suffix with qualifier which is always before the releases
				boolean qualifier1 = QUALIFIER.equals(suffix1);
				boolean qualifier2 = QUALIFIER.equals(suffix2);

				if (qualifier1 && qualifier2)
					continue; // No suffixes
				if (qualifier1) {
					compare = -1; // First suffix is qualifier (1.2 > 1.2qualifier)
					break;
				}
				if (qualifier2) {
					compare = 1; // Second suffix is qualifier (1.2qualifier < 1.2)
					break;
				}

				// Lexical comparison of suffixes
				int result = suffix1.compareTo(suffix2);
				if (result != 0) {
					compare = result;
					break;
				}

			}

			// parse the second version token
			if (compare == 0 && versionToken2.moveNext()) {
				do {
					number2 = versionToken2.getNumber();
					suffix2 = versionToken2.getSuffix();
					if (number2 != VersionTokenizer.NUMBER_EMPTY || suffix2.length() != 0) {
						// Version one is longer than version two, and non-zero -> it is a release
						return 1;
					}
				} while (versionToken2.moveNext());

				// Version two is longer than version one, but zero
				compare = 0;
			}
		}

		return compare;
	}
}
