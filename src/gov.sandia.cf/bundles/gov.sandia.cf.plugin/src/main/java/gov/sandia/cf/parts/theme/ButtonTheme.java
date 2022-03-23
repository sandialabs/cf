/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Listener;

import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.SystemTools;

/**
 * An extension of the button widget to apply customizations.
 * 
 * @author Maxime Noyelle
 *
 */
public class ButtonTheme extends Button {

	/**
	 * // * Button default color
	 */
	public static final String BUTTON_COLOR_DEFAULT = ConstantTheme.COLOR_NAME_PRIMARY;

	/** TEXT option */
	public static final String OPTION_TEXT = "text"; // String //$NON-NLS-1$
	/** DATA option */
	public static final String OPTION_DATA = "data"; // HashMap //$NON-NLS-1$
	/** ICON option */
	public static final String OPTION_ICON = "icon"; // String //$NON-NLS-1$
	/** ICON SIZE option */
	public static final String OPTION_ICON_SIZE = "icon_size"; // String //$NON-NLS-1$
	/** LISTENER option */
	public static final String OPTION_LISTENER = "listener"; // Listener //$NON-NLS-1$
	/** COLOR option */
	public static final String OPTION_COLOR = "color"; // String //$NON-NLS-1$
	/** ENABLED option */
	public static final String OPTION_ENABLED = "is_enabled"; // Boolean //$NON-NLS-1$
	/** OUTLINE option */
	public static final String OPTION_OUTLINE = "is_outline"; // Boolean //$NON-NLS-1$

	/**
	 * Options received
	 */
	private Map<String, Object> options = new HashMap<>();

	/**
	 * The resource manager
	 */
	private ResourceManager rscMgr;

	/**
	 * Construct with option
	 * 
	 * @param rscMgr  the resource manager used to manage the resources (fonts,
	 *                colors, images, cursors...)
	 * @param parent  the parent composite
	 * @param style   the style
	 * @param options the button options
	 */
	public ButtonTheme(ResourceManager rscMgr, Composite parent, int style, Map<String, Object> options) {
		super(parent, style);

		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;

		// Register option
		if (options != null) {
			this.options = options;
		}

		// Apply theme personalization
		applyTheme();
	}

	/**
	 * Constructor
	 * 
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param parent the parent composite
	 * @param style  the SWT style
	 */
	public ButtonTheme(ResourceManager rscMgr, Composite parent, int style) {
		this(rscMgr, parent, style, null);
	}

	/**
	 * Configure the button
	 */
	private void applyTheme() {

		// Set text
		if (options.containsKey(OPTION_TEXT)) {
			setText((String) options.get(OPTION_TEXT));
		}

		// Set Enabled
		if (options.containsKey(OPTION_ENABLED) && options.get(OPTION_ENABLED) instanceof Boolean) {
			setEnabled((Boolean) options.get(OPTION_ENABLED));
		}

		// Set Data
		if (options.containsKey(OPTION_DATA) && options.get(OPTION_DATA) instanceof HashMap) {
			setButtonData((HashMap<?, ?>) options.get(OPTION_DATA));
		}

		// Set listener
		if (options.containsKey(OPTION_LISTENER)) {
			addListener(SWT.Selection, (Listener) options.get(OPTION_LISTENER));
		}

		// Check is an outline button
		boolean isOutline = false;
		if (options.containsKey(OPTION_OUTLINE) && options.get(OPTION_OUTLINE) instanceof Boolean
				&& (Boolean) options.get(OPTION_OUTLINE) != null) {
			isOutline = Boolean.TRUE.equals(options.get(OPTION_OUTLINE));
		}

		// Get main color
		String mainColor = ConstantTheme.getColor(BUTTON_COLOR_DEFAULT);
		if (options.containsKey(OPTION_COLOR) && ConstantTheme.existColor((String) options.get(OPTION_COLOR))) {
			// Get color
			mainColor = ConstantTheme.getColor((String) options.get(OPTION_COLOR));
		}

		// Set Button Colors
		setButtonColors(mainColor, isOutline);

		// Set Icon
		if (options.containsKey(OPTION_ICON) && IconTheme.ICONS.containsKey(options.get(OPTION_ICON))) {
			setIcon((String) options.get(OPTION_ICON), mainColor, isOutline);
		}

		// Set font
		if (SystemTools.isWindows()) {
			FontTools.setButtonFont(rscMgr, this);
		}
	}

	/**
	 * Set the button data.
	 * 
	 * @param data the button data
	 */
	private void setButtonData(Map<?, ?> data) {

		if (data != null) {
			for (Entry<?, ?> entry : data.entrySet()) {
				if (entry != null && entry.getKey() instanceof String) {
					super.setData((String) entry.getKey(), entry.getValue());
				}
			}
		}
	}

	/**
	 * Set icon color
	 * 
	 * @param iconName  the icon name
	 * @param mainColor the main color
	 * @param isOutline is it outline?
	 */
	public void setIcon(String iconName, String mainColor, boolean isOutline) {

		// define size
		int size = SystemTools.isWindows() ? IconTheme.ICON_SIZE_DEFAULT_WINDOWS : IconTheme.ICON_SIZE_DEFAULT_UNIX;
		if (options.containsKey(OPTION_ICON_SIZE)) {
			size = (int) options.get(OPTION_ICON_SIZE);
		}

		// Colored with main color
		if (isOutline) {
			super.setImage(IconTheme.getIconImage(rscMgr, iconName, mainColor, size));
		}
		// Colored with associated color
		else {
			super.setImage(IconTheme.getIconImage(rscMgr, iconName, ConstantTheme.getAssociatedColor(mainColor), size));
		}
	}

	/**
	 * Set button colors
	 * 
	 * @param mainColorString the main color
	 * @param isOutline       is it outline?
	 */
	public void setButtonColors(String mainColorString, boolean isOutline) {

		// mainColor can be null if the color setted is COLOR_NAME_NO_COLOR
		if (this.getEnabled() && mainColorString != null) {

			Color mainColor = ColorTools.toColor(rscMgr, mainColorString);
			// Get associated color
			Color associatedColor = ColorTools.toColor(rscMgr, ConstantTheme.getAssociatedColor(mainColorString));

			// Outline button
			if (isOutline) {
				// Text color
				this.setForeground(mainColor);

				// Set background
				this.setBackground(associatedColor);

				if (SystemTools.isWindows()) {
					// Border
					this.addPaintListener(e -> {
						int h = getBounds().height;
						int w = getBounds().width;
						e.gc.setForeground(mainColor);
						e.gc.drawRectangle(1, 1, w - 3, h - 3);
					});
				}
			}
			// Standard button
			else {
				// Text color
				this.setForeground(associatedColor);

				// Background color
				this.setBackground(mainColor);

				if (SystemTools.isWindows()) {
					// Border
					this.addPaintListener(e -> {
						int h = getBounds().height;
						int w = getBounds().width;
						e.gc.setForeground(mainColor);
						e.gc.drawRectangle(1, 1, w - 3, h - 3);
					});
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	protected void checkSubclass() {
		// allow subclass
	}

	/** {@inheritDoc} */
	@Override
	public void setEnabled(boolean enabled) {

		if (enabled) {

			// enable before setting color
			super.setEnabled(enabled);

			// Get main color
			String mainColor = ConstantTheme.getColor(BUTTON_COLOR_DEFAULT);
			if (options.containsKey(OPTION_COLOR) && ConstantTheme.existColor((String) options.get(OPTION_COLOR))) {
				// Get color
				mainColor = ConstantTheme.getColor((String) options.get(OPTION_COLOR));
			}

			// Check is an outline button
			boolean isOutline = false;
			if (options.containsKey(OPTION_OUTLINE) && options.get(OPTION_OUTLINE) instanceof Boolean) {
				isOutline = Boolean.TRUE.equals(options.get(OPTION_OUTLINE));
			}

			// draw button color
			setButtonColors(mainColor, isOutline);

		} else {

			// draw button disable color
			setButtonColors(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY), false);

			// disable after setting color
			super.setEnabled(enabled);
		}
	}
}
