/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.tools.ColorTools;

/**
 * A CollapsibleControl composite
 * 
 * @author Maxime N.
 *
 */
public class CollapsibleWidget extends Composite {

	private ListenerList<ExpandListener> listeners = new ListenerList<>();

	private Label label;
	private Button checkbox;
	private ButtonTheme button;
	private Composite childComposite;

	private Composite contentComposite;
	private int contentHeight;
	/**
	 * The resource manager
	 */
	private ResourceManager rscMgr;

	/**
	 * Constructs a collapsible composite.
	 * 
	 * 
	 * @param rscMgr         the resource manager used to manage the resources
	 *                       (fonts, colors, images, cursors...)
	 * @param parent         the parent composite
	 * @param style          the style
	 * @param childComposite the composite to show/hide
	 * @param headerText     the header text
	 * @param hasCheckbox    the label has to have a check-box
	 * @param isExpanded     The childComponent default state
	 */
	public CollapsibleWidget(ResourceManager rscMgr, Composite parent, int style, Composite childComposite,
			String headerText, boolean hasCheckbox, boolean isExpanded) {

		// Call super
		super(parent, style & SWT.BORDER);

		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;

		// Set child composite
		this.childComposite = childComposite;

		// Render main
		renderMain();

		// Render header
		renderHeader(headerText, hasCheckbox, isExpanded);

		// Render content
		renderContent(isExpanded);

		// add button listener
		addButtonListener();

		// Layout
		this.getParent().layout();
	}

	/**
	 * Construct Simplified By default, child composite is expanded and text is set
	 * to blank.
	 * 
	 * 
	 * @param rscMgr         the resource manager used to manage the resources
	 *                       (fonts, colors, images, cursors...)
	 * @param parent         the parent composite
	 * @param style          the style
	 * @param childComposite the child composite
	 */
	public CollapsibleWidget(ResourceManager rscMgr, Composite parent, int style, Composite childComposite) {
		this(rscMgr, parent, style, childComposite, "", false, true); //$NON-NLS-1$
	}

	/**
	 * Set the label
	 * 
	 * @param textLabel the label to set
	 */
	public void setLabel(String textLabel) {
		label.setText(textLabel);
		this.requestLayout();
	}

	/**
	 * @return the label
	 */
	public Label getLabel() {
		return label;
	}

	/**
	 * @return the collapse/expand button
	 */
	public ButtonTheme getButton() {
		return button;
	}

	/**
	 * @return the check-box button
	 */
	public Button getCheckbox() {
		return checkbox;
	}

	/**
	 * Render global widget
	 */
	private void renderMain() {
		// Set layout & layout data
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginWidth = 0;
		this.setLayout(gridLayout);
		this.setBackground(this.getParent().getBackground());
	}

	/**
	 * Header collapse.
	 *
	 * @param headerText  the header text
	 * @param hasCheckbox the has checkbox
	 * @param isExpanded  the is expanded
	 */
	private void renderHeader(String headerText, boolean hasCheckbox, boolean isExpanded) {
		// Header composite
		Composite headerComposite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(2, false);
		headerComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		headerComposite.setLayout(gridLayout);
		headerComposite.setBackground(headerComposite.getParent().getBackground());

		// create label or check-box label
		if (hasCheckbox) {
			checkbox = new Button(headerComposite, SWT.CHECK);
			checkbox.setText(headerText);
			checkbox.setSelection(true);
			checkbox.setBackground(checkbox.getParent().getBackground());
			checkbox.setForeground(
					ColorTools.toColor(rscMgr, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
			GridData dataCheckbox = new GridData();
			dataCheckbox.horizontalAlignment = GridData.FILL;
			dataCheckbox.verticalAlignment = GridData.FILL;
			dataCheckbox.grabExcessHorizontalSpace = true;
			dataCheckbox.heightHint = 28;
			checkbox.setLayoutData(dataCheckbox);
			FontTools.setSubtitleFont(rscMgr, checkbox);
		} else {
			label = FormFactory.createLabel(headerComposite, headerText);
			label.setBackground(label.getParent().getBackground());
			label.setForeground(ColorTools.toColor(rscMgr, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)));
			GridData dataLabel = (GridData) label.getLayoutData();
			dataLabel.horizontalAlignment = GridData.FILL;
			dataLabel.verticalAlignment = GridData.FILL;
			dataLabel.grabExcessHorizontalSpace = true;
			dataLabel.heightHint = dataLabel.heightHint + 10;
			label.setLayoutData(dataLabel);
			FontTools.setSubtitleFont(rscMgr, label);
		}

		// Create button
		button = FormFactory.createButton(rscMgr, headerComposite, null, null, IconTheme.ICON_NAME_EXPAND,
				IconTheme.ICON_SIZE_SMALL, ConstantTheme.COLOR_NAME_NO_COLOR);

		if (isExpanded) {
			button.setIcon(IconTheme.ICON_NAME_COLLAPSE, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
					false);
		}
	}

	/**
	 * Content collapse.
	 *
	 * @param isExpanded the is expanded
	 */
	private void renderContent(boolean isExpanded) {
		// Main table composite
		contentComposite = new Composite(this, SWT.NONE);
		contentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		GridLayout gridLayout = new GridLayout();
		contentComposite.setLayout(gridLayout);
		contentComposite.setBackground(contentComposite.getParent().getBackground());

		// Set visibility
		contentComposite.setVisible(isExpanded);

		// Set parent
		childComposite.setParent(contentComposite);
	}

	/**
	 * Content collapse
	 */
	private void addButtonListener() {

		final int labelHeight = (label != null && label.getLayoutData() != null)
				? ((GridData) label.getLayoutData()).heightHint + 20
				: ((GridData) checkbox.getLayoutData()).heightHint + 20;

		// resize child listener
		childComposite.addListener(SWT.Resize, e -> contentHeight = contentComposite.getSize().y + 10);

		// button listener
		button.addListener(SWT.Selection, e -> {
			if (childComposite.isVisible()) {
				contentComposite.setVisible(false);
				contentComposite.setSize(contentComposite.getSize().x, 0);
				((GridData) this.getLayoutData()).heightHint = labelHeight;
				button.setIcon(IconTheme.ICON_NAME_EXPAND, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
						false);
				if (e != null) {
					fireCollapsed(new ExpandEvent(e));
				}
			} else {
				contentComposite.setVisible(true);
				contentComposite.setSize(contentComposite.getSize().x, contentHeight);
				((GridData) this.getLayoutData()).heightHint = labelHeight + contentHeight;
				button.setIcon(IconTheme.ICON_NAME_COLLAPSE, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
						false);
				if (e != null) {
					fireExpanded(new ExpandEvent(e));
				}
			}

			// Layout parent
			this.getParent().layout();
		});
	}

	/**
	 * @param listener the expand listener
	 */
	public void addExpandListener(ExpandListener listener) {
		listeners.add(listener);
	}

	/**
	 * @param listener the expand listener
	 */
	public void removeExpandListener(ExpandListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Notifies all registered cell editor listeners if the item is expanded
	 * 
	 * @param e the event
	 */
	protected void fireExpanded(ExpandEvent e) {
		for (ExpandListener l : listeners) {
			SafeRunnable.run(new SafeRunnable() {
				@Override
				public void run() {
					l.itemExpanded(e);
				}
			});
		}
	}

	/**
	 * Notifies all registered cell editor listeners if the item is collapsed
	 * 
	 * @param e the event
	 */
	protected void fireCollapsed(ExpandEvent e) {
		for (ExpandListener l : listeners) {
			SafeRunnable.run(new SafeRunnable() {
				@Override
				public void run() {
					l.itemCollapsed(e);
				}
			});
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (checkbox != null) {
			checkbox.setEnabled(enabled);
		}
		if (contentComposite != null) {
			contentComposite.setEnabled(enabled);
			for (Control control : contentComposite.getChildren()) {
				if (control != null) {
					control.setEnabled(enabled);
				}
			}
		}
		if (label != null) {
			label.setForeground(
					ColorTools.toColor(rscMgr, (enabled ? ConstantTheme.getColor(ConstantTheme.COLOR_NAME_PRIMARY)
							: ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GRAY))));
		}
	}
}
