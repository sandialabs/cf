/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RunWith(JUnitPlatform.class)
class MathToolsTest {
	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(MathToolsTest.class);

	@Test
	void test() {
		// ******************************
		// Test method getRandomIntBase10
		// ******************************
		int random = MathTools.getRandomIntBase10();
		logger.info("{}", random); //$NON-NLS-1$
		assertTrue(random >= 0);
		assertTrue(random <= MathTools.BASE10FACTOR);

		// ************************
		// Test method getRandomInt
		// ************************
		int base = 3;
		random = MathTools.getRandomInt(base);
		logger.info("{}", random); //$NON-NLS-1$
		assertTrue(random >= 0);
		assertTrue(random <= base);
	}
}
