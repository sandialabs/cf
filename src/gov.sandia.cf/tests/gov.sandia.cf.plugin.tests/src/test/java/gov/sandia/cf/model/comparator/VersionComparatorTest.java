/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.comparator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Test the version comparator
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class VersionComparatorTest {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(VersionComparatorTest.class);

	/**
	 * the version comparator
	 */
	private static VersionComparator comparator = new VersionComparator();

	@Test
	void test_compare_CompareWithNullVersions() {
		assertEquals(0, comparator.compare(null, null));
	}

	@Test
	void test_compare_CompareWithEmptyVersions() {
		assertEquals(0, comparator.compare(RscTools.empty(), RscTools.empty()));
	}

	@Test
	void test_compare_CompareWithEmptyVersionRight() {
		assertTrue(comparator.compare("0.3.0", RscTools.empty()) > 0); //$NON-NLS-1$
	}

	@Test
	void test_compare_CompareWithNullVersionRight() {
		assertTrue(comparator.compare("0.3.0", null) > 0); //$NON-NLS-1$
	}

	@Test
	void test_compare_CompareWithEmptyVersionLeft() {
		assertTrue(comparator.compare(RscTools.empty(), "0.3.0") < 0); //$NON-NLS-1$
	}

	@Test
	void test_compare_CompareWithNullVersionLeft() {
		assertTrue(comparator.compare(null, "0.3.0") < 0); //$NON-NLS-1$
	}

	@Test
	void test_compare_ValidScriptMidVersion_Short() {
		assertTrue(comparator.compare("0.2", "0.2.0") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptMidVersion_Short2() {
		assertTrue(comparator.compare("0.2", "0.2.1") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptMidVersion_Short_Greater() {
		assertTrue(comparator.compare("0.2", "0.3.0") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptMidVersion() {
		assertTrue(comparator.compare("0.3.0", "0.2.0") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptMinorVersion() {
		assertTrue(comparator.compare("0.2.1", "0.2.0") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptMidMinorVersion() {
		assertTrue(comparator.compare("0.3.0", "0.2.9") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptMajorMidMinorVersion() {
		assertTrue(comparator.compare("1.3.0", "0.5.9") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptMajorVersion_Equals() {
		assertTrue(comparator.compare("1.2.0", "1.2.0") == 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptMinorVersion_Equals() {
		assertTrue(comparator.compare("0.2.1", "0.2.1") == 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixVersion_Hyphen() {
		assertTrue(comparator.compare("0.2.0-20200602", "0.2.0-20200601") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixVersion_Point() {
		assertTrue(comparator.compare("0.2.0.20200602", "0.2.0.20200601") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixRCVersion_Hyphen() {
		assertTrue(comparator.compare("0.2.0-RC1", "0.2.0-20200601") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixRCVersion_Point() {
		assertTrue(comparator.compare("0.2.0.RC1", "0.2.0.20200601") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixStringVersion_Hyphen() {
		assertTrue(comparator.compare("0.2.0-RC1", "0.2.0-M1") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixStringVersion_PointAndReleaseVersion() {
		assertTrue(comparator.compare("0.5.0.RC1", "0.5.0") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixStringVersion_HyphenAndReleaseVersion() {
		assertTrue(comparator.compare("0.5.0-RC1", "0.5.0") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixStringVersion_Point() {
		assertTrue(comparator.compare("0.2.0.RC1", "0.2.0.M1") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixStringVersion_RC1_RC1dev() {
		assertTrue(comparator.compare("0.5.0.RC1", "0.5.0.RC1-dev") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScriptSuffixStringVersion_RC1_RC1_point_dev() {
		assertTrue(comparator.compare("0.5.0.RC1", "0.5.0.RC1.dev") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	@Test
	void test_compare_ValidScript_Hyphen() {
		assertTrue(comparator.compare("0.2.0", "0.2.0-20200601") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_ValidScript_Point() {
		assertTrue(comparator.compare("0.2.0", "0.2.0.20200601") > 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_InvalidScript() {
		assertTrue(comparator.compare("test", "0.2.0-20200601") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_MinorSuffixVersionScript() {
		assertTrue(comparator.compare("0.2.0-20200600", "0.2.0-20200601") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_MinorSuffixVersionScript_Equals() {
		assertTrue(comparator.compare("0.2.0-20200601", "0.2.0-20200601") == 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_SuffixAsNumber_With_Release() {
		assertTrue(comparator.compare("0.2.0.202008122545", "0.2.0") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_SuffixAsNumber_With_SuffixAString() {
		assertTrue(comparator.compare("0.2.0.202008122545", "0.2.0.qualifier") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_SuffixQualifier_With_MilestoneAndPoint() {
		assertTrue(comparator.compare("0.2.0.qualifier", "0.2.0.M1") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_SuffixQualifier_With_ReleaseAndPoint() {
		assertTrue(comparator.compare("0.2.0.qualifier", "0.2.0.RC1") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_SuffixQualifier_With_SuffixQualifier() {
		assertTrue(comparator.compare("0.2.0.qualifier", "0.2.0.qualifier") == 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_MinorVersionScript() {
		assertTrue(comparator.compare("0.1.9", "0.2.0") < 0); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void test_compare_Script1Null() {
		try {
			comparator.compare(null, "0.2.0"); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_VERSION_TOKENIZER_NULL_PARAM));
		}
	}

	@Test
	void test_compare_Script2Null() {
		try {
			comparator.compare("0.2.0", null); //$NON-NLS-1$
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_VERSION_TOKENIZER_NULL_PARAM));
		}
	}

	@Test
	void test_compare_ScriptAllNull() {
		try {
			comparator.compare(null, null); // $NON-NLS-1$
		} catch (IllegalArgumentException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_VERSION_TOKENIZER_NULL_PARAM));
		}
	}
}
