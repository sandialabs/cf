/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;

/**
 * Manage ToolTop into a Scrollable Web View
 * 
 * @author Maxime N
 */
public class FancyToolTipSupport extends ColumnViewerToolTipSupport {

	/**
	 * Private constructor to not allow instantiation.
	 * 
	 * @param viewer           the column viewer
	 * @param style            the style
	 * @param manualActivation is manual activation?
	 */
	private FancyToolTipSupport(ColumnViewer viewer, int style, boolean manualActivation) {
		super(viewer, style, manualActivation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Composite createToolTipContentArea(Event event, Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		if (!getText(event).isEmpty()) {
			GridLayout l = new GridLayout(1, true);
			l.horizontalSpacing = 0;
			l.marginWidth = 0;
			l.marginHeight = 0;
			l.verticalSpacing = 0;
			comp.setLayout(l);
			Browser browser = new Browser(comp, SWT.BORDER);
			browser.setText(getText(event));
			browser.setLayoutData(new GridData(300, 200));
		}
		return comp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isHideOnMouseDown() {
		return false;
	}

	/**
	 * Enable the tooltip for the viewer in parameter.
	 * 
	 * @param viewer the column viewer
	 * @param style  the style
	 */
	public static final void enableFor(ColumnViewer viewer, int style) {
		new FancyToolTipSupport(viewer, style, false);
	}
}
