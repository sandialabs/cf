/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.preferences;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.core.resources.ResourcesPlugin;
import org.junit.jupiter.api.Test;

/**
 * The Class PrefToolsTest.
 * 
 * @author Didier Verstraete
 */
class PrefToolsTest {

	@Test
	void testGetGlobalDisplayVersionNumber() {
		try {
			// Test if worspace lauch
			ResourcesPlugin.getWorkspace().getRoot();
			assertNotNull(PrefTools.getGlobalDisplayVersionNumber());
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testGetPIRTQueryFilePath() {
		try {
			// Test if worspace lauch
			ResourcesPlugin.getWorkspace().getRoot();
			assertNotNull(PrefTools.getPreference(PrefTools.PIRT_QUERY_FILE_PATH_KEY));
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testGetPIRTSchemaFileLastPath() {
		try {
			// Test if worspace lauch
			ResourcesPlugin.getWorkspace().getRoot();
			assertNotNull(PrefTools.getPreference(PrefTools.PIRT_SCHEMA_FILE_LAST_PATH_KEY));
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testGetPCMMSchemaFileLastPath() {
		try {
			// Test if worspace lauch
			ResourcesPlugin.getWorkspace().getRoot();
			assertNotNull(PrefTools.getPreference(PrefTools.PCMM_SCHEMA_FILE_LAST_PATH_KEY));
		} catch (IllegalStateException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testSetPCMMSchemaFileLastPath() {
		// Test empty
		PrefTools.setPreference(PrefTools.PCMM_SCHEMA_FILE_LAST_PATH_KEY, ""); //$NON-NLS-1$
		assertEquals("", PrefTools.getPreference(PrefTools.PCMM_SCHEMA_FILE_LAST_PATH_KEY));//$NON-NLS-1$
	}

	@Test
	void testSetPIRTSchemaFileLastPath() {
		// Test empty
		PrefTools.setPreference(PrefTools.PIRT_SCHEMA_FILE_LAST_PATH_KEY, ""); //$NON-NLS-1$
		assertEquals("", PrefTools.getPreference(PrefTools.PIRT_SCHEMA_FILE_LAST_PATH_KEY));//$NON-NLS-1$
	}

}
