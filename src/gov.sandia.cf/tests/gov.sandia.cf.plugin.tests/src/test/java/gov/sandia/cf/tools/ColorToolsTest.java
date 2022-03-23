/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.resource.LocalResourceManager;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import gov.sandia.cf.parts.theme.ConstantTheme;

@RunWith(JUnitPlatform.class)
class ColorToolsTest {

	private static ResourceManager rscMgr;

	@BeforeAll
	static void init() {
		rscMgr = new LocalResourceManager(JFaceResources.getResources());
	}

	@AfterAll
	static void stop() {
		// free the resources
		rscMgr.dispose();
	}

	@Test
	void test_hexToSwtColor() {
		String colorGreenHexString = "FF09FF"; //$NON-NLS-1$

		Color colorGreenSwt = ColorTools.hexToSwtColor(rscMgr, colorGreenHexString);
		assertEquals(255, colorGreenSwt.getRed());
		assertEquals(9, colorGreenSwt.getGreen());
		assertEquals(255, colorGreenSwt.getBlue());
	}

	@Test
	void test_hexToAwtColor() {
		String colorGreenHexString = "FF09FF"; //$NON-NLS-1$

		java.awt.Color colorGreenAwt = ColorTools.hexToAwtColor(colorGreenHexString);
		assertEquals(255, colorGreenAwt.getRed());
		assertEquals(9, colorGreenAwt.getGreen());
		assertEquals(255, colorGreenAwt.getBlue());
	}

	@Test
	void test_grayed_FF09FF_percentage1() {
		String colorGreenHexString = "FF09FF"; //$NON-NLS-1$

		RGB greyed1 = ColorTools.grayed(ColorTools.hexToSwtColor(rscMgr, colorGreenHexString).getRGB(), 1);
		assertEquals(253, greyed1.red);
		assertEquals(10, greyed1.green);
		assertEquals(253, greyed1.blue);
	}

	@Test
	void test_grayedRgb_FF09FF_percentage_default() {
		String colorGreenHexString = "FF09FF"; //$NON-NLS-1$

		RGB greyed1 = ColorTools.grayedRgb(ColorTools.hexToSwtColor(rscMgr, colorGreenHexString).getRGB());
		assertEquals(182, greyed1.red);
		assertEquals(59, greyed1.green);
		assertEquals(182, greyed1.blue);
	}

	@Test
	void test_stringRGBToColor() {
		String colorGreenRGBString = "255,9,255"; //$NON-NLS-1$

		RGB greenRGBFromString = ColorTools.stringRGBToColor(colorGreenRGBString);
		assertEquals(255, greenRGBFromString.red);
		assertEquals(9, greenRGBFromString.green);
		assertEquals(255, greenRGBFromString.blue);
	}

	@Test
	void test_stringRGBToColor_null() {
		assertNull(ColorTools.stringRGBToColor(null));
	}

	@Test
	void test_stringRGBToColor_empty() {
		assertNull(ColorTools.stringRGBToColor("")); //$NON-NLS-1$
	}

	@Test
	void test_stringRGBToColor_partial() {
		assertNull(ColorTools.stringRGBToColor("123,123")); //$NON-NLS-1$
	}

	@Test
	void test_stringRGBToColor_text() {
		assertNull(ColorTools.stringRGBToColor("BLABLA")); //$NON-NLS-1$
	}

	@Test
	void test_toColor() {
		String colorGreenRGBString = "255,9,255"; //$NON-NLS-1$

		Color greenStringToColor = ColorTools.toColor(rscMgr, colorGreenRGBString);
		assertEquals(255, greenStringToColor.getRed());
		assertEquals(9, greenStringToColor.getGreen());
		assertEquals(255, greenStringToColor.getBlue());
	}

	@Test
	void test_toColor_with_spaces() {
		String colorGreenRGBString = "255 , 9 , 255"; //$NON-NLS-1$

		Color greenStringToColor = ColorTools.toColor(rscMgr, colorGreenRGBString);
		assertEquals(255, greenStringToColor.getRed());
		assertEquals(9, greenStringToColor.getGreen());
		assertEquals(255, greenStringToColor.getBlue());
	}

	@Test
	void test_toColor_null() {
		assertNull(ColorTools.toColor(rscMgr, null));
	}

	@Test
	void test_toColor_null_rscMgr() {
		String colorGreenRGBString = "255,9,255"; //$NON-NLS-1$

		assertNull(ColorTools.toColor(null, colorGreenRGBString));
	}

	@Test
	void test_toColor_empty() {
		assertNull(ColorTools.toColor(rscMgr, "")); //$NON-NLS-1$
	}

	@Test
	void test_toColor_partial() {
		assertNull(ColorTools.toColor(rscMgr, "5, 8")); //$NON-NLS-1$
	}

	@Test
	void test_toColor_text() {
		assertNull(ColorTools.toColor(rscMgr, "GHA")); //$NON-NLS-1$
	}

	@Test
	void test_toAwtColor() {
		String colorGreenRGBString = "255,9,255"; //$NON-NLS-1$

		java.awt.Color greenStringtoAwtColor = ColorTools.toAwtColor(colorGreenRGBString);
		assertEquals(255, greenStringtoAwtColor.getRed());
		assertEquals(9, greenStringtoAwtColor.getGreen());
		assertEquals(255, greenStringtoAwtColor.getBlue());
	}

	@Test
	void test_toAwtColor_with_spaces() {
		String colorGreenRGBString = "255 , 9 , 255"; //$NON-NLS-1$

		java.awt.Color greenStringtoAwtColor = ColorTools.toAwtColor(colorGreenRGBString);
		assertEquals(255, greenStringtoAwtColor.getRed());
		assertEquals(9, greenStringtoAwtColor.getGreen());
		assertEquals(255, greenStringtoAwtColor.getBlue());
	}

	@Test
	void test_toAwtColor_null() {
		assertNull(ColorTools.toAwtColor(null));
	}

	@Test
	void test_toAwtColor_empty() {
		assertNull(ColorTools.toAwtColor("")); //$NON-NLS-1$
	}

	@Test
	void test_toAwtColor_partial() {
		assertNull(ColorTools.toAwtColor("5, 8")); //$NON-NLS-1$
	}

	@Test
	void test_toAwtColor_text() {
		assertNull(ColorTools.toAwtColor("GHA")); //$NON-NLS-1$
	}

	@Test
	void test_rgbToAwtColor() {
		String colorGreenRGBString = "255,9,255"; //$NON-NLS-1$

		java.awt.Color greenStringtoAwtColor = ColorTools
				.rgbToAwtColor(ColorTools.stringRGBToColor(colorGreenRGBString));
		assertEquals(255, greenStringtoAwtColor.getRed());
		assertEquals(9, greenStringtoAwtColor.getGreen());
		assertEquals(255, greenStringtoAwtColor.getBlue());
	}

	@Test
	void test_rgbToAwtColor_null() {
		assertNull(ColorTools.rgbToAwtColor(null));
	}

	@Test
	void test_toStringRGB_RGB() {
		String colorGreenRGBString = "255, 9, 255"; //$NON-NLS-1$
		String stringRGB = ColorTools.toStringRGB(ColorTools.stringRGBToColor(colorGreenRGBString));
		assertEquals(colorGreenRGBString, stringRGB);
	}

	@Test
	void test_toStringRGB_RGB_null() {
		assertNull(ColorTools.toStringRGB((RGB) null));
	}

	@Test
	void test_toStringRGB_Color() {
		String stringRGB = ColorTools
				.toStringRGB(ColorTools.toColor(rscMgr, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY)));
		assertEquals(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY), stringRGB);
	}

	@Test
	void test_toStringRGB_Color_null() {
		assertNull(ColorTools.toStringRGB((Color) null));
	}
}
