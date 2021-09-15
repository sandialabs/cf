/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

@RunWith(JUnitPlatform.class)
class ColorToolsTest {

	@Test
	void test() {
		// **********
		// Initialize
		// **********
		Device device = null;
		String colorGreenHexString = "FF09FF"; //$NON-NLS-1$
		String colorGreenRGBString = "255,9,255"; //$NON-NLS-1$

		// *************************
		// Test method HexToSwtColor
		// *************************
		Color colorGreenSwt = ColorTools.hexToSwtColor(device, colorGreenHexString);
		assertEquals(colorGreenSwt.getRed(), 255);
		assertEquals(colorGreenSwt.getGreen(), 9);
		assertEquals(colorGreenSwt.getBlue(), 255);

		// *************
		// HexToAwtColor
		// *************
		java.awt.Color colorGreenAwt = ColorTools.hexToAwtColor(colorGreenHexString);
		assertEquals(colorGreenAwt.getRed(), 255);
		assertEquals(colorGreenAwt.getGreen(), 9);
		assertEquals(colorGreenAwt.getBlue(), 255);

		// ******
		// grayed
		// ******
		RGB greyed1 = ColorTools.grayed(colorGreenSwt.getRGB(), 1);
		assertEquals(greyed1.red, 253);
		assertEquals(greyed1.green, 10);
		assertEquals(greyed1.blue, 253);

		// **********
		// grayed rgb
		// **********
		RGB greyed2 = ColorTools.grayedRgb(colorGreenSwt.getRGB());
		assertEquals(greyed2.red, 182);
		assertEquals(greyed2.green, 59);
		assertEquals(greyed2.blue, 182);

		// *******************
		// String rgb to color
		// *******************
		RGB greenRGBFromString = ColorTools.stringRGBToColor(colorGreenRGBString);
		assertEquals(greenRGBFromString.red, 255);
		assertEquals(greenRGBFromString.green, 9);
		assertEquals(greenRGBFromString.blue, 255);

		RGB colorNull = ColorTools.stringRGBToColor(null);
		assertNull(colorNull);

		RGB colorEmpty = ColorTools.stringRGBToColor(""); //$NON-NLS-1$
		assertNull(colorEmpty);

		RGB colorNotComplete = ColorTools.stringRGBToColor("123,123"); //$NON-NLS-1$
		assertNull(colorNotComplete);

		// String to color
		Color greenStringToColor = ColorTools.stringToColor(device, colorGreenRGBString);
		assertEquals(greenStringToColor.getRed(), 255);
		assertEquals(greenStringToColor.getGreen(), 9);
		assertEquals(greenStringToColor.getBlue(), 255);

		// String to awt color
		java.awt.Color greenStringToAwtColor = ColorTools.stringRGBToAwtColor(colorGreenRGBString);
		assertEquals(greenStringToAwtColor.getRed(), 255);
		assertEquals(greenStringToAwtColor.getGreen(), 9);
		assertEquals(greenStringToAwtColor.getBlue(), 255);

		// RGB to awt color
		java.awt.Color greenRGBToAwtColor = ColorTools.rgbToAwtColor(greenRGBFromString);
		assertEquals(greenRGBToAwtColor.getRed(), 255);
		assertEquals(greenRGBToAwtColor.getGreen(), 9);
		assertEquals(greenRGBToAwtColor.getBlue(), 255);

		java.awt.Color rgbToAwtColorNull = ColorTools.rgbToAwtColor(null);
		assertNull(rgbToAwtColorNull);
	}
}
