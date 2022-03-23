/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Maxime Noyelle
 *
 */
public class ConstantTheme {

	/**
	 * Colors name
	 */
	/** NO COLOR */
	public static final String COLOR_NAME_NO_COLOR = "NO_COLOR"; //$NON-NLS-1$
	/** BLACK Color */
	public static final String COLOR_NAME_BLACK = "BLACK"; //$NON-NLS-1$
	/** BLUE Color */
	public static final String COLOR_NAME_BLUE = "BLUE"; //$NON-NLS-1$
	/** BLUE_LIGHT Color */
	public static final String COLOR_NAME_BLUE_LIGHT = "BLUE_LIGHT"; //$NON-NLS-1$
	/** The Constant COLOR_NAME_BLUE_INFO. */
	public static final String COLOR_NAME_INFO = "BLUE_INFO"; //$NON-NLS-1$ /
	/** The Constant COLOR_NAME_BLUE_INFO_LIGHT. */
	public static final String COLOR_NAME_INFO_LIGHT = "BLUE_INFO_LIGHT"; //$NON-NLS-1$
	/** BLUE_PANTONE Color */
	public static final String COLOR_NAME_BLUE_PANTONE = "BLUE_PANTONE"; //$NON-NLS-1$
	/** BROWN Color */
	public static final String COLOR_NAME_BROWN = "BROWN"; //$NON-NLS-1$
	/** BROWN_LIGHT Color */
	public static final String COLOR_NAME_BROWN_LIGHT = "BROWN_LIGHT"; //$NON-NLS-1$
	/** The Constant COLOR_NAME_ERROR. */
	public static final String COLOR_NAME_ERROR = "ERROR"; //$NON-NLS-1$
	/** The Constant COLOR_NAME_ERROR_LIGHT. */
	public static final String COLOR_NAME_ERROR_LIGHT = "ERROR_LIGHT"; //$NON-NLS-1$
	/** GRAY Color */
	public static final String COLOR_NAME_GRAY = "GRAY"; //$NON-NLS-1$
	/** GRAY_DARK Color */
	public static final String COLOR_NAME_GRAY_DARK = "GRAY_DARK"; //$NON-NLS-1$
	/** GRAY_PENCIL Color */
	public static final String COLOR_NAME_GRAY_PENCIL = "GRAY_PENCIL"; //$NON-NLS-1$
	/** GREEN Color */
	public static final String COLOR_NAME_GREEN = "GREEN"; //$NON-NLS-1$
	/** GREY_LIGHT Color */
	public static final String COLOR_NAME_GREY_LIGHT = "GREY_LIGHT"; //$NON-NLS-1$
	/** GREY_LIGHT_1 Color */
	public static final String COLOR_NAME_GREY_LIGHT_1 = "GREY_LIGHT_1"; //$NON-NLS-1$
	/** ORANGE Color */
	public static final String COLOR_NAME_ORANGE = "ORANGE"; //$NON-NLS-1$
	/** PINK Color */
	public static final String COLOR_NAME_PINK = "PINK"; //$NON-NLS-1$
	/** PRIMARY Color */
	public static final String COLOR_NAME_PRIMARY = "PRIMARY"; //$NON-NLS-1$
	/** PRIMARY_LIGHT Color */
	public static final String COLOR_NAME_PRIMARY_LIGHT = "PRIMARY_LIGHT"; //$NON-NLS-1$
	/** PRIMARY_LIGHT_2 Color */
	public static final String COLOR_NAME_PRIMARY_LIGHT_2 = "PRIMARY_LIGHT_2"; //$NON-NLS-1$
	/** PURPLE Color */
	public static final String COLOR_NAME_PURPLE = "PURPLE"; //$NON-NLS-1$
	/** RED Color */
	public static final String COLOR_NAME_RED = "RED"; //$NON-NLS-1$
	/** SECONDARY Color */
	public static final String COLOR_NAME_SECONDARY = "SECONDARY"; //$NON-NLS-1$
	/** The Constant COLOR_NAME_SUCCESS. */
	public static final String COLOR_NAME_SUCCESS = "SUCCESS"; //$NON-NLS-1$
	/** The Constant COLOR_NAME_SUCCESS_LIGHT. */
	public static final String COLOR_NAME_SUCCESS_LIGHT = "SUCCESS_LIGHT"; //$NON-NLS-1$
	/** SECONDARY_LIGHT Color */
	public static final String COLOR_NAME_SECONDARY_LIGHT = "SECONDARY_LIGHT"; //$NON-NLS-1$
	/** The Constant COLOR_NAME_WARNING. */
	public static final String COLOR_NAME_WARNING = "WARNING"; //$NON-NLS-1$
	/** The Constant COLOR_NAME_WARNING_LIGHT. */
	public static final String COLOR_NAME_WARNING_LIGHT = "WARNING_LIGHT"; //$NON-NLS-1$
	/** WHITE Color */
	public static final String COLOR_NAME_WHITE = "WHITE"; //$NON-NLS-1$
	/** YELLOW Color */
	public static final String COLOR_NAME_YELLOW = "YELLOW"; //$NON-NLS-1$

	// Colors
	private static final Map<String, String> COLORS;
	static {
		COLORS = new HashMap<>();
		COLORS.put(COLOR_NAME_NO_COLOR, null);
		COLORS.put(COLOR_NAME_BLACK, "0, 0, 0"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_BLUE, "23, 162, 184"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_BLUE_LIGHT, "145, 211, 235"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_BLUE_PANTONE, "17, 67, 85"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_BROWN, "114, 55, 22"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_BROWN_LIGHT, "198, 139, 72"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_ERROR, "153, 72, 102"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_ERROR_LIGHT, "248, 215, 218"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_GRAY, "192, 192, 192"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_GRAY_DARK, "65, 65, 65"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_GRAY_PENCIL, "89, 98, 117"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_GREEN, "40, 167, 69"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_GREY_LIGHT, "250, 250, 250"); // Grey very light //$NON-NLS-1$
		COLORS.put(COLOR_NAME_GREY_LIGHT_1, "240, 240, 240"); // Grey light //$NON-NLS-1$
		COLORS.put(COLOR_NAME_INFO, "62, 145, 180"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_INFO_LIGHT, "209, 236, 241"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_ORANGE, "255, 160, 51"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_PINK, "234, 134, 133"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_PRIMARY, "4, 56, 101"); // Blue //$NON-NLS-1$
		COLORS.put(COLOR_NAME_PRIMARY_LIGHT, "31, 115, 160"); // Blue light //$NON-NLS-1$
		COLORS.put(COLOR_NAME_PRIMARY_LIGHT_2, "147, 185, 216"); // Blue light 2 //$NON-NLS-1$
		COLORS.put(COLOR_NAME_PURPLE, "128, 0, 133"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_RED, "193, 56, 56"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_SECONDARY, "114, 114, 114"); // Grey //$NON-NLS-1$
		COLORS.put(COLOR_NAME_SECONDARY_LIGHT, "233, 233, 233"); // Grey light //$NON-NLS-1$
		COLORS.put(COLOR_NAME_SUCCESS, "73, 118, 85"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_SUCCESS_LIGHT, "212, 237, 218"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_WARNING, "192, 149, 39"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_WARNING_LIGHT, "255, 243, 205"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_WHITE, "255, 255, 255"); //$NON-NLS-1$
		COLORS.put(COLOR_NAME_YELLOW, "255, 205, 0"); //$NON-NLS-1$
	}

	// Associated Color
	private static final Map<String, String> ASSOCIATED_COLORS;
	static {
		ASSOCIATED_COLORS = new HashMap<>();
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_NO_COLOR), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BLACK), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BLUE), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BLUE_LIGHT), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BLUE_PANTONE), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BROWN), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BROWN_LIGHT), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_ERROR), COLORS.get(COLOR_NAME_ERROR_LIGHT));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_ERROR_LIGHT), COLORS.get(COLOR_NAME_ERROR));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GREEN), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GREY_LIGHT), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GRAY), COLORS.get(COLOR_NAME_GRAY_DARK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GRAY_PENCIL), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GRAY_DARK), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_INFO), COLORS.get(COLOR_NAME_INFO_LIGHT));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_INFO_LIGHT), COLORS.get(COLOR_NAME_INFO));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_ORANGE), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PINK), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PRIMARY), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PRIMARY_LIGHT), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PRIMARY_LIGHT_2), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PURPLE), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_RED), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_SECONDARY), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_SECONDARY_LIGHT), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_SUCCESS), COLORS.get(COLOR_NAME_SUCCESS_LIGHT));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_SUCCESS_LIGHT), COLORS.get(COLOR_NAME_SUCCESS));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_WARNING), COLORS.get(COLOR_NAME_WARNING_LIGHT));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_WARNING_LIGHT), COLORS.get(COLOR_NAME_WARNING));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_WHITE), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_YELLOW), COLORS.get(COLOR_NAME_WHITE));
	}

	private static final List<String> SCALED_COLOR;
	static {
		SCALED_COLOR = new ArrayList<>();
		SCALED_COLOR.add("255, 199, 132"); // 0 //$NON-NLS-1$
		SCALED_COLOR.add("255, 172, 71"); // 1 //$NON-NLS-1$
		SCALED_COLOR.add("255, 149, 20"); // 2 //$NON-NLS-1$
		SCALED_COLOR.add("211, 124, 16"); // 3 //$NON-NLS-1$
		SCALED_COLOR.add("175, 103, 14"); // 4 //$NON-NLS-1$
		SCALED_COLOR.add("153, 89, 12"); // 5 //$NON-NLS-1$
		SCALED_COLOR.add("135, 66, 27"); // 6 //$NON-NLS-1$
		SCALED_COLOR.add("114, 55, 22"); // 7 //$NON-NLS-1$
	}

	/**
	 * Private constructor to avoid instantiation.
	 */
	private ConstantTheme() {
	}

	/**
	 * Get the Color by name
	 *
	 * @param colorName the color name
	 * @return the Color object
	 */
	public static String getColor(String colorName) {
		return COLORS.get(colorName);
	}

	/**
	 * Gets the associated color.
	 *
	 * @param color the color value (not the name)
	 * @return the color value associated (not the name)
	 */
	public static String getAssociatedColor(String color) {
		return ASSOCIATED_COLORS.get(color);
	}

	/**
	 * Get scaled color
	 * 
	 * @param index the color index
	 * @return the color scaled
	 */
	public static String getScaledColor(Integer index) {
		String scaledColor = null;
		if (index < 0) {
			scaledColor = SCALED_COLOR.get(0);
		} else if (index >= SCALED_COLOR.size()) {
			scaledColor = SCALED_COLOR.get(SCALED_COLOR.size() - 1);
		} else {
			scaledColor = SCALED_COLOR.get(index);
		}
		return scaledColor;
	}

	/**
	 * Color exist
	 *
	 * @param colorName the color name
	 * @return true if color exist
	 */
	public static boolean existColor(String colorName) {
		return COLORS.containsKey(colorName);
	}
}
