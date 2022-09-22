/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.swt.graphics.Image;
import org.junit.jupiter.api.Test;

/**
 * The Class IconThemeTest.
 *
 * @author Didier Verstraete
 */
class IconThemeTest {

	@Test
	void defaultIcon() {

		// Default color
		assertTrue(ConstantTheme.existColor(IconTheme.ICON_COLOR_DEFAULT));
	}

	@Test
	void test_getIconInDefaultColor() {
		IconTheme.ICONS.forEach((iconName, list) -> {
			if (!list.containsKey(IconTheme.getDefaultColor())) {
				fail("The icon " + iconName + " is not available in default color."); //$NON-NLS-1$ //$NON-NLS-2$
			}
		});
	}

	@Test
	void test_getIconPathAdd_DefaultColor() {
		String iconPath = IconTheme.getIconPath(IconTheme.ICON_NAME_ADD, IconTheme.getDefaultColor());
		assertNotNull(iconPath);
		assertEquals(IconTheme.ICONS.get(IconTheme.ICON_NAME_ADD).get(IconTheme.getDefaultColor()), iconPath);
	}

	@Test
	void test_getIconPathAdd_ColorNotFound() {
		String iconPath = IconTheme.getIconPath(IconTheme.ICON_NAME_ADD,
				ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PINK));
		assertNotNull(iconPath);
		assertEquals(IconTheme.ICONS.get(IconTheme.ICON_NAME_ADD).get(IconTheme.getDefaultColor()), iconPath);
	}

	@Test
	void test_getIconPathNotFoundInDefaultColor() {
		String iconPath = IconTheme.getIconPath("blabla", IconTheme.getDefaultColor()); //$NON-NLS-1$
		assertNotNull(iconPath);
		assertEquals(IconTheme.ICONS.get(IconTheme.ICON_NAME_NOT_FOUND).get(IconTheme.getDefaultColor()), iconPath);
	}

	@Test
	void test_getIconPathNotFound_ColorNull() {
		String iconPath = IconTheme.getIconPath("blabla", null); //$NON-NLS-1$
		assertNotNull(iconPath);
		assertEquals(IconTheme.ICONS.get(IconTheme.ICON_NAME_NOT_FOUND).get(IconTheme.getDefaultColor()), iconPath);
	}

	@Test
	void test_getIconAdd_DefaultColor() {
		Image icon = IconTheme.getIconImage(new LocalResourceManager(JFaceResources.getResources()),
				IconTheme.ICON_NAME_ADD, IconTheme.getDefaultColor());
		assertNotNull(icon);
	}

	@Test
	void test_getIconAdd_ColorNotFound() {
		Image icon = IconTheme.getIconImage(new LocalResourceManager(JFaceResources.getResources()),
				IconTheme.ICON_NAME_ADD, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PINK));
		assertNotNull(icon);
	}

	@Test
	void test_getIconNotFoundInDefaultColor() {
		Image icon = IconTheme.getIconImage(new LocalResourceManager(JFaceResources.getResources()), "blabla", //$NON-NLS-1$
				IconTheme.getDefaultColor());
		assertNotNull(icon);
	}

	@Test
	void test_getIconNotFound_ColorNull() {
		Image icon = IconTheme.getIconImage(new LocalResourceManager(JFaceResources.getResources()), "blabla", null); //$NON-NLS-1$
		assertNotNull(icon);
	}
}
