/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.HashSet;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

/**
 * Test for RscTools class
 * 
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class RscToolsTest {

	@Test
	void testSetLocal() {
		RscTools.setLocale(null, null);
		assertNotNull(RscTools.getLocale());

		RscTools.setLocale("", ""); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(RscTools.getLocale());

		RscTools.setLocale(null, ""); //$NON-NLS-1$
		assertNotNull(RscTools.getLocale());

		RscTools.setLocale("", null); //$NON-NLS-1$
		assertNotNull(RscTools.getLocale());

		RscTools.setLocale("en", null); //$NON-NLS-1$
		assertNotNull(RscTools.getLocale());

		RscTools.setLocale("en", ""); //$NON-NLS-1$ //$NON-NLS-2$
		assertNotNull(RscTools.getLocale());
	}

	@Test
	void testGetString() {
		// Set locale
		RscTools.setLocale("en", "US"); //$NON-NLS-1$ //$NON-NLS-2$

		// Test translation ok
		assertEquals("\n", RscTools.carriageReturn()); //$NON-NLS-1$
		assertEquals("entity not found: WHITE=BLACK", //$NON-NLS-1$
				RscTools.getString(RscConst.EX_DAO_CRUD_ENTITYNOTFOUND, "WHITE", "BLACK")); //$NON-NLS-1$ //$NON-NLS-2$

		// Test translation exception
		String pattern = "A.B.C.D.E.F"; //$NON-NLS-1$
		assertEquals(MessageFormat.format(RscTools.MSG_ERROR_PATTERN, pattern), RscTools.getString(pattern)); // $NON-NLS-1$
		assertEquals(MessageFormat.format(RscTools.MSG_ERROR_PATTERN, pattern), RscTools.getString(pattern, "A", "B")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Test
	void testAllMessageConstantsMatchingInDefaultBundle() {
		Field[] declaredFields = RscConst.class.getDeclaredFields();
		for (Field field : declaredFields) {

			// ********************************************************************
			// **************************** IMPORTANT *****************************
			// This is testing the RscTools matching between the class keys and the
			// messagebundle resource file. For static attributed not be to parsed, add _ as
			// a prefix of your class attribute.
			// ********************************************************************
			// ********************************************************************

			if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == String.class
					&& field.getName() != null && !field.getName().startsWith("_")) { //$NON-NLS-1$
				try {
					String keyValue = (String) field.get(null);
					assertNotEquals(MessageFormat.format(RscTools.MSG_ERROR_PATTERN, keyValue),
							RscTools.getString(keyValue));
				} catch (IllegalArgumentException | IllegalAccessException e) {
					fail(e.getMessage());
				}
			}
		}
	}

	@Test
	void testDefaultBundleMatchingInMessageConstants() {
		Field[] declaredFields = RscConst.class.getDeclaredFields();

		// set of message constant keys
		HashSet<String> setMessageConstants = new HashSet<>();
		for (Field field : declaredFields) {

			// ********************************************************************
			// **************************** IMPORTANT *****************************
			// This is testing the RscTools matching between the class keys and the
			// messagebundle resource file. For static attributed not be to parsed, add _ as
			// a prefix of your class attribute.
			// ********************************************************************
			// ********************************************************************

			if (java.lang.reflect.Modifier.isStatic(field.getModifiers()) && field.getType() == String.class
					&& field.getName() != null && !field.getName().startsWith("_")) { //$NON-NLS-1$
				try {
					String keyValue = (String) field.get(null);
					setMessageConstants.add(keyValue);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					fail(e.getMessage());
				}
			}
		}

		// set of all bundle keys
		Enumeration<String> keys = RscTools.getBundle().getKeys();

		// matching compare
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			if (!setMessageConstants.contains(key)) {
				fail(MessageFormat.format("Key {0} in bundle does not exist in {1} Message Constants", key, //$NON-NLS-1$
						RscTools.class.getName()));
			}
		}
	}
}
