/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

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
	/** PRIMARY Color */
	public static final String COLOR_NAME_PRIMARY = "PRIMARY"; //$NON-NLS-1$
	/** SECONDARY Color */
	public static final String COLOR_NAME_SECONDARY = "SECONDARY"; //$NON-NLS-1$
	/** PRIMARY_LIGHT Color */
	public static final String COLOR_NAME_PRIMARY_LIGHT = "PRIMARY_LIGHT"; //$NON-NLS-1$
	/** PRIMARY_LIGHT_2 Color */
	public static final String COLOR_NAME_PRIMARY_LIGHT_2 = "PRIMARY_LIGHT_2"; //$NON-NLS-1$
	/** SECONDARY_LIGHT Color */
	public static final String COLOR_NAME_SECONDARY_LIGHT = "SECONDARY_LIGHT"; //$NON-NLS-1$
	/** GREY_LIGHT_1 Color */
	public static final String COLOR_NAME_GREY_LIGHT_1 = "GREY_LIGHT_1"; //$NON-NLS-1$
	/** GREY_LIGHT Color */
	public static final String COLOR_NAME_GREY_LIGHT = "GREY_LIGHT"; //$NON-NLS-1$
	/** RED Color */
	public static final String COLOR_NAME_RED = "RED"; //$NON-NLS-1$
	/** GRAY Color */
	public static final String COLOR_NAME_GRAY = "GRAY"; //$NON-NLS-1$
	/** GRAY_PENCIL Color */
	public static final String COLOR_NAME_GRAY_PENCIL = "GRAY_PENCIL"; //$NON-NLS-1$
	/** GRAY_DARK Color */
	public static final String COLOR_NAME_GRAY_DARK = "GRAY_DARK"; //$NON-NLS-1$
	/** GREEN Color */
	public static final String COLOR_NAME_GREEN = "GREEN"; //$NON-NLS-1$
	/** ORANGE Color */
	public static final String COLOR_NAME_ORANGE = "ORANGE"; //$NON-NLS-1$
	/** BLUE Color */
	public static final String COLOR_NAME_BLUE = "BLUE"; //$NON-NLS-1$
	/** BLUE_LIGHT Color */
	public static final String COLOR_NAME_BLUE_LIGHT = "BLUE_LIGHT"; //$NON-NLS-1$
	/** BLUE_PANTONE Color */
	public static final String COLOR_NAME_BLUE_PANTONE = "BLUE_PANTONE"; //$NON-NLS-1$
	/** YELLOW Color */
	public static final String COLOR_NAME_YELLOW = "YELLOW"; //$NON-NLS-1$
	/** BLACK Color */
	public static final String COLOR_NAME_BLACK = "BLACK"; //$NON-NLS-1$
	/** WHITE Color */
	public static final String COLOR_NAME_WHITE = "WHITE"; //$NON-NLS-1$
	/** BROWN Color */
	public static final String COLOR_NAME_BROWN = "BROWN"; //$NON-NLS-1$
	/** BROWN_LIGHT Color */
	public static final String COLOR_NAME_BROWN_LIGHT = "BROWN_LIGHT"; //$NON-NLS-1$
	/** PURPLE Color */
	public static final String COLOR_NAME_PURPLE = "PURPLE"; //$NON-NLS-1$
	/** PINK Color */
	public static final String COLOR_NAME_PINK = "PINK"; //$NON-NLS-1$

	// Colors
	private static final Map<String, Color> COLORS;
	static {
		COLORS = new HashMap<>();
		COLORS.put(COLOR_NAME_NO_COLOR, null);
		COLORS.put(COLOR_NAME_PRIMARY, new Color(Display.getCurrent(), 4, 56, 101)); // Blue
		COLORS.put(COLOR_NAME_SECONDARY, new Color(Display.getCurrent(), 114, 114, 114)); // Grey
		COLORS.put(COLOR_NAME_PRIMARY_LIGHT, new Color(Display.getCurrent(), 31, 115, 160)); // Blue light
		COLORS.put(COLOR_NAME_PRIMARY_LIGHT_2, new Color(Display.getCurrent(), 147, 185, 216)); // Blue light 2
		COLORS.put(COLOR_NAME_SECONDARY_LIGHT, new Color(Display.getCurrent(), 233, 233, 233)); // Grey light
		COLORS.put(COLOR_NAME_GREY_LIGHT, new Color(Display.getCurrent(), 250, 250, 250)); // Grey very light
		COLORS.put(COLOR_NAME_GREY_LIGHT_1, new Color(Display.getCurrent(), 240, 240, 240)); // Grey light
		COLORS.put(COLOR_NAME_RED, new Color(Display.getCurrent(), 193, 56, 56));
		COLORS.put(COLOR_NAME_GRAY, Display.getCurrent().getSystemColor(SWT.COLOR_GRAY));
		COLORS.put(COLOR_NAME_GRAY_PENCIL, new Color(Display.getCurrent(), 89, 98, 117));
		COLORS.put(COLOR_NAME_GRAY_DARK, new Color(Display.getCurrent(), 65, 65, 65));
		COLORS.put(COLOR_NAME_ORANGE, new Color(Display.getCurrent(), 255, 160, 51));
		COLORS.put(COLOR_NAME_GREEN, new Color(Display.getCurrent(), 40, 167, 69));
		COLORS.put(COLOR_NAME_BLUE, new Color(Display.getCurrent(), 23, 162, 184));
		COLORS.put(COLOR_NAME_BLUE_LIGHT, new Color(Display.getCurrent(), 145, 211, 235));
		COLORS.put(COLOR_NAME_BLUE_PANTONE, new Color(Display.getCurrent(), 17, 67, 85));
		COLORS.put(COLOR_NAME_YELLOW, new Color(Display.getCurrent(), 255, 205, 0));
		COLORS.put(COLOR_NAME_BLACK, new Color(Display.getCurrent(), 0, 0, 0));
		COLORS.put(COLOR_NAME_WHITE, new Color(Display.getCurrent(), 255, 255, 255));
		COLORS.put(COLOR_NAME_BROWN, new Color(Display.getCurrent(), 114, 55, 22));
		COLORS.put(COLOR_NAME_BROWN_LIGHT, new Color(Display.getCurrent(), 198, 139, 72));
		COLORS.put(COLOR_NAME_PURPLE, new Color(Display.getCurrent(), 128, 0, 133));
		COLORS.put(COLOR_NAME_PINK, new Color(Display.getCurrent(), 234, 134, 133));
	}

	// Associated Color
	private static final Map<Color, Color> ASSOCIATED_COLORS;
	static {
		ASSOCIATED_COLORS = new HashMap<>();
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_NO_COLOR), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PRIMARY), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_SECONDARY), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PRIMARY_LIGHT), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PRIMARY_LIGHT_2), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_SECONDARY_LIGHT), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GREY_LIGHT), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_RED), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GRAY), Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GRAY_PENCIL), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GRAY_DARK), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_GREEN), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BLUE), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BLUE_LIGHT), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BLUE_PANTONE), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_YELLOW), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_ORANGE), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BLACK), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_WHITE), COLORS.get(COLOR_NAME_BLACK));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BROWN), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_BROWN_LIGHT), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PURPLE), COLORS.get(COLOR_NAME_WHITE));
		ASSOCIATED_COLORS.put(COLORS.get(COLOR_NAME_PINK), COLORS.get(COLOR_NAME_WHITE));
	}

	private static final List<Color> SCALED_COLOR;
	static {
		SCALED_COLOR = new ArrayList<>();
		SCALED_COLOR.add(new Color(Display.getCurrent(), 255, 199, 132)); // 0
		SCALED_COLOR.add(new Color(Display.getCurrent(), 255, 172, 71)); // 1
		SCALED_COLOR.add(new Color(Display.getCurrent(), 255, 149, 20)); // 2
		SCALED_COLOR.add(new Color(Display.getCurrent(), 211, 124, 16)); // 3
		SCALED_COLOR.add(new Color(Display.getCurrent(), 175, 103, 14)); // 4
		SCALED_COLOR.add(new Color(Display.getCurrent(), 153, 89, 12)); // 5
		SCALED_COLOR.add(new Color(Display.getCurrent(), 135, 66, 27)); // 6
		SCALED_COLOR.add(new Color(Display.getCurrent(), 114, 55, 22)); // 7
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
	public static Color getColor(String colorName) {
		return COLORS.get(colorName);
	}

	/**
	 * Get the Associated Color by Color
	 *
	 * @param color the color
	 * @return the Color object
	 */
	public static Color getAssociatedColor(Color color) {
		return ASSOCIATED_COLORS.get(color);
	}

	/**
	 * Get scaled color
	 * 
	 * @param index the color index
	 * @return the color scaled
	 */
	public static Color getScaledColor(Integer index) {
		Color scaledColor = null;
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
