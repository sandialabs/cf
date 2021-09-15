/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import gov.sandia.cf.constants.CFVariable;

@RunWith(JUnitPlatform.class)
class SystemToolsTest {

	@Test
	void test_get() {
		assertFalse(SystemTools.get(CFVariable.JAVA_VERSION).isEmpty());
		assertFalse(SystemTools.get(CFVariable.OS_NAME).isEmpty());
		assertFalse(SystemTools.get(CFVariable.USER_HOME).isEmpty());
		assertFalse(SystemTools.get(CFVariable.USER_NAME).isEmpty());
	}

	@Test
	void testIsWindows() {
		String os = SystemTools.get(CFVariable.OS_NAME);
		assertNotNull(os);
		assertFalse(os.isEmpty());

		if (SystemTools.isWindows()) {
			assertTrue(os.contains(SystemTools.WINDOWS));
		} else {
			assertTrue(!os.contains(SystemTools.WINDOWS));
		}
	}

	@Test
	void test_getHostName() {
		assertFalse(SystemTools.getHostName().isEmpty());
	}
}
