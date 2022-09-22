/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * The Class StringUtilsTest.
 *
 * @author Didier Verstraete
 */
class StringUtilsTest {

	@Test
	void testStringUtils() {

		// ******************************
		// Test Method StringUtils.equals
		// ******************************
		String myString = "myString"; //$NON-NLS-1$
		String myString1 = "myString1"; //$NON-NLS-1$

		// Equals
		assertTrue(StringTools.equals("", "")); //$NON-NLS-1$//$NON-NLS-2$
		assertTrue(StringTools.equals(null, null));
		assertTrue(StringTools.equals(myString1, myString1));

		// not equals
		assertFalse(StringTools.equals(myString1, myString));
		assertFalse(StringTools.equals(myString, myString1));
		assertFalse(StringTools.equals(null, "")); //$NON-NLS-1$
		assertFalse(StringTools.equals(null, "Test")); //$NON-NLS-1$
		assertFalse(StringTools.equals("Test", null)); //$NON-NLS-1$

		// ******************************************
		// Test Method StringUtils.insertPeriodically
		// *****************************************
		String longString = "A long string to insert text inside"; //$NON-NLS-1$
		assertEquals("A lonhig strhiing thio inshiert thiext ihinside", //$NON-NLS-1$
				StringTools.insertPeriodically(longString, "hi", 5)); //$NON-NLS-1$
		assertEquals(longString, StringTools.insertPeriodically(longString, "hi", 0)); //$NON-NLS-1$
		assertEquals(longString, StringTools.insertPeriodically(longString, null, 10)); // $NON-NLS-1$
		assertEquals(longString, StringTools.insertPeriodically(longString, "", 10)); //$NON-NLS-1$
	}

	@Test
	void testNl2br() {
		assertEquals("<br><br>", StringTools.nl2br("\n\n")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("<br>", StringTools.nl2br("\n")); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("<br>TEST<br>", StringTools.nl2br("\nTEST\n")); //$NON-NLS-1$ //$NON-NLS-2$
	}
}
