/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.theme;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.ExpandBar;

/**
 * The Expand Bar Factory
 * 
 * @author Didier Verstraete
 *
 */
public class ExpandBarTheme {

	private static final int EXPANDBAR_VERTICAL_SCROLL_INCREMENT = 5;

	/**
	 * Private constructor to avoid instantiation.
	 */
	private ExpandBarTheme() {
	}

	/**
	 * @param parent the parent composite
	 * @return an expand bar with the default configuration and style.
	 */
	public static ExpandBar createExpandBar(Composite parent) {
		return createExpandBar(parent, SWT.V_SCROLL | SWT.FILL);
	}

	/**
	 * @param parent     the parent composite
	 * @param withBorder draw the expand bar border or not
	 * @return an expand bar with the default configuration and style.
	 */
	public static ExpandBar createExpandBar(Composite parent, boolean withBorder) {
		return createExpandBar(parent, withBorder, SWT.V_SCROLL | SWT.FILL);
	}

	/**
	 * @param parent the parent composite
	 * @param style  the style to apply
	 * @return an expand bar with the default configuration.
	 */
	public static ExpandBar createExpandBar(Composite parent, int style) {
		boolean withBorder = true;
		return createExpandBar(parent, withBorder, style);
	}

	/**
	 * @param parent     the parent composite
	 * @param withBorder draw the expand bar border or not
	 * @param style      the style to apply
	 * @return an expand bar with the default configuration.
	 */
	public static ExpandBar createExpandBar(Composite parent, boolean withBorder, int style) {
		boolean grabVerticalSpace = false;
		return createExpandBar(parent, withBorder, grabVerticalSpace, style);
	}

	/**
	 * @param parent            the parent composite
	 * @param withBorder        draw the expand bar border or not
	 * @param grabVerticalSpace grab excess vertical space
	 * @param style             the style to apply
	 * @return an expand bar with the default configuration.
	 */
	public static ExpandBar createExpandBar(Composite parent, boolean withBorder, boolean grabVerticalSpace,
			int style) {
		ExpandBar barHeader = new ExpandBar(parent, style);
		barHeader.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, grabVerticalSpace, 1, 1));
		if (parent != null) {
			barHeader.setBackground(parent.getBackground());
		}
		barHeader.setForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY));
		barHeader.addPaintListener(e -> {
			e.gc.setForeground(ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SECONDARY));
			if (withBorder) {
				int h = barHeader.getBounds().height;
				int w = barHeader.getBounds().width;
				e.gc.drawRectangle(1, 1, w - 3, h - 3);
			}
		});

		if (barHeader.getVerticalBar() != null) {
			barHeader.getVerticalBar().setIncrement(EXPANDBAR_VERTICAL_SCROLL_INCREMENT);
		}

		return barHeader;
	}

}
