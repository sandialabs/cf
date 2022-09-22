/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * The Class ConstantThemeTest.
 * 
 * @author Didier Verstraete
 */
class ConstantThemeTest {

	@Test
	void test() {

		// Null
		String colorNullAssoc = ConstantTheme.getAssociatedColor(null);
		assertEquals(colorNullAssoc, "0, 0, 0"); //$NON-NLS-1$

		// Black
		String black = ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
		assertEquals(black, "0, 0, 0"); //$NON-NLS-1$

		String blackAssoc = ConstantTheme.getAssociatedColor(black);
		assertEquals(blackAssoc, "255, 255, 255"); //$NON-NLS-1$

		// Required colors
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_NO_COLOR));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_PRIMARY));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_SECONDARY));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_PRIMARY_LIGHT_2));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_SECONDARY_LIGHT));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_RED));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_GRAY));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_GREEN));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_BLUE));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_YELLOW));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_BLACK));
		assertTrue(ConstantTheme.existColor(ConstantTheme.COLOR_NAME_WHITE));

		// Scaled test
		assertNotNull(ConstantTheme.getScaledColor(0));
		assertNotNull(ConstantTheme.getScaledColor(999));
		assertNotNull(ConstantTheme.getScaledColor(-999));
	}
}
