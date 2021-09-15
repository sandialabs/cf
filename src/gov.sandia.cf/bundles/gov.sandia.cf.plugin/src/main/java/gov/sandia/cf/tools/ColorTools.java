/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.text.MessageFormat;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

import gov.sandia.cf.application.configuration.pirt.YmlReaderPIRTSchema;

/**
 * 
 * The color tools class
 * 
 * @author Didier Verstraete
 *
 */
public class ColorTools {

	/**
	 * DEFAULT_STRINGRGB_COLOR constant
	 */
	public static final String DEFAULT_STRINGRGB_COLOR = "255,255,255"; //$NON-NLS-1$

	/**
	 * DEFAULT_RGB_COLOR constant
	 */
	public static final RGB DEFAULT_RGB_COLOR = new RGB(255, 255, 255);

	/**
	 * DEFAULT_GRAY_PERCENTAGE constant
	 */
	public static final int DEFAULT_GRAY_PERCENTAGE = 50;

	/**
	 * DEFAULT_STEP constant
	 */
	public static final int DEFAULT_STEP = 10;

	private static final String RGB_FORMAT = "{0}, {1}, {2}"; //$NON-NLS-1$

	/**
	 * Private constructor to not allow instantiation.
	 */
	private ColorTools() {
	}

	/**
	 * @param device   the eclipse device
	 * @param colorStr the color as hexa string (ie. 44FE87)
	 * @return a swt Color with @param colorStr as hexa string
	 */
	public static Color hexToSwtColor(Device device, String colorStr) {
		return new Color(device, Integer.valueOf(colorStr.substring(0, 2), 16),
				Integer.valueOf(colorStr.substring(2, 4), 16), Integer.valueOf(colorStr.substring(4, 6), 16));
	}

	/**
	 * @param colorStr the color as hexa string
	 * @return an awt Color with @param colorStr as hexa string
	 */
	public static java.awt.Color hexToAwtColor(String colorStr) {
		return new java.awt.Color(Integer.valueOf(colorStr, 16));
	}

	/**
	 * @param rgb the rgb color in a string
	 * @return RGB class from String @param rgb. If @param rgb is null, empty or
	 *         does not contains valid rgb description, return null
	 * 
	 *         Example: valid: 0,255,0 invalid: 0255,55
	 */
	public static RGB stringRGBToColor(String rgb) {

		RGB color = null;

		if (rgb != null) {
			// trim to delete white spaces
			rgb = rgb.trim();

			// split rgb to retrieve int colors
			String[] splittedRGB = rgb.split(YmlReaderPIRTSchema.CONF_PIRT_RGB_SEPARATOR);

			// splitted RGB must have 3 values
			if (splittedRGB != null && splittedRGB.length == 3 && splittedRGB[0] != null && splittedRGB[1] != null
					&& splittedRGB[2] != null) {
				int r = Integer.parseInt(splittedRGB[0].trim());
				int g = Integer.parseInt(splittedRGB[1].trim());
				int b = Integer.parseInt(splittedRGB[2].trim());

				// create new color
				color = new RGB(r, g, b);
			}
		}

		return color;
	}

	/**
	 * @param color the color
	 * @param perc  the percentage of gray
	 * @return a grayed out color depending of the perc in parameter
	 */
	public static Color grayedColor(Color color, int perc) {
		if (color != null) {
			return new Color(Display.getCurrent(), grayed(color.getRGB(), perc));
		}
		return null;
	}

	/**
	 * @param color the color
	 * @return a grayed out color with the default percentage of gray
	 */
	public static Color grayedColor(Color color) {
		if (color != null) {
			return new Color(Display.getCurrent(), grayedRgb(color.getRGB()));
		}
		return null;
	}

	/**
	 * @param rgb the rgb color
	 * @return a grayed out rgb color with the default percentage of gray
	 */
	public static RGB grayedRgb(RGB rgb) {
		return grayed(rgb, DEFAULT_GRAY_PERCENTAGE);
	}

	/**
	 * @param rgb  the rgb color
	 * @param perc the percentage of gray
	 * @return a grayed out rgb color depending of the perc in parameter
	 */
	public static RGB grayed(RGB rgb, int perc) {
		if (rgb != null) {
			double percGrayed = perc / 100.0;
			double percColored = 1.0 - percGrayed;

			double[] weights = { 0.2989, 0.5870, 0.1140 };
			double[] rgbTab = { rgb.red, rgb.green, rgb.blue };

			// Determine luminance:
			double y = 0.0;
			for (int i = 0; i < 3; ++i) {
				y += weights[i] * rgbTab[i];
			}

			// Interpolate between (R, G, B) and (Y, Y, Y):
			for (int i = 0; i < 3; ++i) {
				rgbTab[i] *= percColored;
				rgbTab[i] += y * percGrayed;
			}

			return new RGB((int) rgbTab[0], (int) rgbTab[1], (int) rgbTab[2]);
		}
		return null;
	}

	/**
	 * @param color the color
	 * @return a grayed out color depending of the perc in parameter
	 */
	public static Color minusColor(Color color) {
		return minusColor(color, DEFAULT_STEP);
	}

	/**
	 * @param color the color
	 * @param step  the step of gray
	 * @return a grayed out color depending of the step in parameter
	 */
	public static Color minusColor(Color color, int step) {
		if (color != null) {
			int red = color.getRed() - step >= 0 ? color.getRed() - step : 0;
			int green = color.getRed() - step >= 0 ? color.getGreen() - step : 0;
			int blue = color.getRed() - step >= 0 ? color.getBlue() - step : 0;
			return new Color(Display.getCurrent(), red, green, blue);
		}
		return null;
	}

	/**
	 * @param device the device
	 * @param rgb    the rgb
	 * @return a swt color from the rgb string
	 */
	public static Color stringToColor(Device device, String rgb) {
		return new Color(device, stringRGBToColor(rgb));
	}

	/**
	 * @param color the color to convert
	 * @return the rgb string for the color
	 */
	public static String colorToStringRGB(Color color) {
		if (color == null) {
			return null;
		}

		return MessageFormat.format(RGB_FORMAT, color.getRed(), color.getGreen(), color.getBlue());
	}

	/**
	 * @param color the color to convert
	 * @return the rgb string for the color
	 */
	public static String colorToStringRGB(RGB color) {
		if (color == null) {
			return null;
		}

		return MessageFormat.format(RGB_FORMAT, color.red, color.green, color.blue);
	}

	/**
	 * @param stringRGB the rgb as a string
	 * @return the string rgb color as an awt color
	 */
	public static java.awt.Color stringRGBToAwtColor(String stringRGB) {
		return rgbToAwtColor(stringRGBToColor(stringRGB));
	}

	/**
	 * @param rgb the rgb color
	 * @return the rgb color as an awt color
	 */
	public static java.awt.Color rgbToAwtColor(RGB rgb) {
		java.awt.Color color = null;
		if (rgb != null) {
			color = new java.awt.Color(rgb.red, rgb.green, rgb.blue);
		}
		return color;
	}

	/**
	 * @param swtColor the swt color
	 * @return the rgb color as an awt color
	 */
	public static java.awt.Color swtColorToAwtColor(Color swtColor) {
		java.awt.Color color = null;
		if (swtColor != null) {
			return rgbToAwtColor(swtColor.getRGB());
		}
		return color;
	}
}
