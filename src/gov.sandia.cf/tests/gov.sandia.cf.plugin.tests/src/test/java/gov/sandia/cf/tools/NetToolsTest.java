/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class NetToolsTest {
	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(NetToolsTest.class);

	@Test
	void testIsValidURL() {
		// Logs
		logger.info("Test started isValidURL"); //$NON-NLS-1$

		// Test true
		assertTrue(NetTools.isValidURL("http://127.0.0.1")); //$NON-NLS-1$
		assertTrue(NetTools.isValidURL("http://localhost")); //$NON-NLS-1$
		assertTrue(NetTools.isValidURL("https://localhost")); //$NON-NLS-1$
		assertTrue(NetTools.isValidURL("http://localhost.fr")); //$NON-NLS-1$
		assertTrue(NetTools.isValidURL("http://www.localhost")); //$NON-NLS-1$
		assertTrue(NetTools.isValidURL("http://www.localhost.fr")); //$NON-NLS-1$

		// Test composed url
		assertTrue(NetTools.isValidURL(
				"https://web.archive.org/web/20190201171526/http://www.roymech.co.uk/Useful_Tables/Tribology/co_of_frict.htm#method")); //$NON-NLS-1$

		// Test false
		assertFalse(NetTools.isValidURL("localhost")); //$NON-NLS-1$
		assertFalse(NetTools.isValidURL("//localhost")); //$NON-NLS-1$
		assertFalse(NetTools.isValidURL("127.0.0.1")); //$NON-NLS-1$
		assertFalse(NetTools.isValidURL("hello")); //$NON-NLS-1$
		assertFalse(NetTools.isValidURL("12345")); //$NON-NLS-1$
	}
}
