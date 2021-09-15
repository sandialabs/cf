/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.nebula.widgets.richtext.RichTextEditor;
import org.eclipse.nebula.widgets.richtext.RichTextEditorConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;

import gov.sandia.cf.model.Notification;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.tools.CursorTools;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.tools.RscTools;

/**
 * A collapsible richtext editor composite
 * 
 * @author Didier Verstraete
 *
 */
public class RichTextWidget extends AHelperWidget {

	boolean enabled;
	private boolean expanded;
	private String labelText;
	private Label label;
	private ButtonTheme button;
	private RichTextEditor richtext;
	private Browser richtextNonEditable;

	/**
	 * List of cell editor listeners (element type: <code>ExpandListener</code>).
	 */
	private ListenerList<ExpandListener> listeners = new ListenerList<>();
	private Composite labelComposite;

	/**
	 * Constructs a rich text widget.
	 * 
	 * By default, rich text is expanded and text is set to blank.
	 * 
	 * 
	 * @param rscMgr the resource manager used to manage the resources (fonts,
	 *               colors, images, cursors...)
	 * @param parent the parent composite
	 * @param style  the style
	 */
	public RichTextWidget(ResourceManager rscMgr, Composite parent, int style) {
		this(rscMgr, parent, style, RscTools.empty(), true);
	}

	/**
	 * Constructs a rich text widget.
	 * 
	 * @param rscMgr    the resource manager used to manage the resources (fonts,
	 *                  colors, images, cursors...)
	 * @param parent    the parent composite
	 * @param style     the style
	 * @param labelText the label text
	 * @param expanded  set the richtext content by default expanded or collpased
	 */
	public RichTextWidget(ResourceManager rscMgr, Composite parent, int style, String labelText, boolean expanded) {
		this(rscMgr, parent, style, labelText, expanded, true);
	}

	/**
	 * Constructs a rich text widget.
	 * 
	 * @param rscMgr    the resource manager used to manage the resources (fonts,
	 *                  colors, images, cursors...)
	 * @param parent    the parent composite
	 * @param style     the style
	 * @param labelText the label text
	 * @param expanded  set the richtext content by default expanded or collapsed
	 * @param editable  is editable?
	 */
	public RichTextWidget(ResourceManager rscMgr, Composite parent, int style, String labelText, boolean expanded,
			boolean editable) {
		super(rscMgr, parent, style & SWT.BORDER, editable);

		this.enabled = true;
		this.expanded = expanded;
		this.labelText = labelText;

		// create control
		createControl();
	}

	/**
	 * Create the richtext content
	 */
	private void createControl() {

		// field content
		this.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		this.setLayout(new GridLayout(2, false));

		// create label
		labelComposite = new Composite(this, SWT.NONE);
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		labelComposite.setLayout(gridLayout);
		GridData gdContainer = new GridData(SWT.FILL, SWT.FILL, true, false);
		labelComposite.setLayoutData(gdContainer);

		label = FormFactory.createLabel(labelComposite, labelText);
		((GridData) label.getLayoutData()).grabExcessHorizontalSpace = true;
		CursorTools.setCursor(getRscMgr(), label, SWT.CURSOR_HAND);

		// create helper
		super.createHelper(labelComposite);

		// create button
		button = FormFactory.createButton(getRscMgr(), this, null, null, IconTheme.ICON_NAME_ADD,
				IconTheme.ICON_SIZE_SMALL, ConstantTheme.COLOR_NAME_NO_COLOR);
		((GridData) button.getLayoutData()).verticalAlignment = SWT.TOP;

		// render control
		if (super.isEditable()) {
			renderEditableField();
		} else {
			renderNonEditableField();
		}

		// layout
		layout();
		getParent().layout();

		// label listener
		label.addListener(SWT.MouseDown, this::resizeWidget);

		// button listener
		button.addListener(SWT.Selection, this::resizeWidget);
	}

	/**
	 * Render the editable field
	 */
	private void renderEditableField() {

		// create richText
		richtext = FormFactory.createRichText(this, null);
		GridData dataComments = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataComments.minimumHeight = PartsResourceConstants.RICHTEXTEDITOR_MINHEIGHT;
		dataComments.heightHint = PartsResourceConstants.RICHTEXTEDITOR_MINHEIGHT;
		dataComments.horizontalSpan = 2;
		richtext.setLayoutData(dataComments);

		// set expanded/collapsed parameters
		final int labelHeight = ((GridData) label.getLayoutData()).heightHint + getHelperHeight() + 10;
		final int richTextHeight = ((GridData) richtext.getLayoutData()).heightHint + 20;

		// set initial size
		if (!expanded) {
			richtext.setVisible(false);
			richtext.setSize(SWT.DEFAULT, 0);
			((GridData) this.getLayoutData()).heightHint = labelHeight;
			button.setIcon(IconTheme.ICON_NAME_EXPAND, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
					false);
		} else {
			richtext.setVisible(true);
			richtext.setSize(SWT.DEFAULT, richTextHeight);
			((GridData) this.getLayoutData()).heightHint = labelHeight + richTextHeight;
			button.setIcon(IconTheme.ICON_NAME_COLLAPSE, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
					false);
		}
	}

	/**
	 * Render non editable field
	 */
	private void renderNonEditableField() {

		// configure richtext editor
		RichTextEditorConfiguration editConfig = new RichTextEditorConfiguration();
		editConfig.setToolbarCollapsible(true);
		editConfig.setOption(RichTextEditorConfiguration.TOOLBAR_GROUPS, PartsResourceConstants.RICH_EDITOR_TOOLBAR);

		// create richText
		richtextNonEditable = FormFactory.createNonEditableRichText(this, RscTools.empty());
		GridData dataComments = new GridData(SWT.FILL, SWT.FILL, true, true);
		dataComments.minimumHeight = PartsResourceConstants.RICHTEXTEDITOR_MINHEIGHT;
		dataComments.heightHint = PartsResourceConstants.RICHTEXTEDITOR_MINHEIGHT;
		dataComments.horizontalSpan = 2;
		richtextNonEditable.setLayoutData(dataComments);

		// set expanded/collapsed parameters
		richtextNonEditable.setVisible(expanded);
		final int labelHeight = ((GridData) label.getLayoutData()).heightHint + getHelperHeight() + 10;
		final int richTextHeight = ((GridData) richtextNonEditable.getLayoutData()).heightHint + 10;

		// set initial size
		if (!expanded) {
			richtextNonEditable.setVisible(false);
			richtextNonEditable.setSize(SWT.DEFAULT, 0);
			((GridData) this.getLayoutData()).heightHint = labelHeight;
			button.setIcon(IconTheme.ICON_NAME_EXPAND, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
					false);
		} else {
			richtextNonEditable.setVisible(true);
			richtextNonEditable.setSize(SWT.DEFAULT, richTextHeight);
			((GridData) this.getLayoutData()).heightHint = labelHeight + richTextHeight;
			button.setIcon(IconTheme.ICON_NAME_COLLAPSE, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
					false);
		}
	}

	@Override
	public void clearHelper() {
		super.clearHelper();
		labelComposite.requestLayout();
		resizeHeader();
	}

	@Override
	public void setHelper(Notification notification) {
		super.setHelper(notification);
		resizeHeader();
	}

	/**
	 * @param e the event to throw if not null
	 */
	private void resizeWidget(Event e) {

		if (isEditable()) {
			resizeWidget(e, richtext);
		} else {
			resizeWidget(e, richtextNonEditable);
		}

		this.layout();
		getParent().layout();
	}

	/**
	 * Resize the widget to show/hide the content
	 * 
	 * @param e       the event to throw
	 * @param control the control to set visible/invisible
	 */
	private void resizeWidget(Event e, Control control) {
		if (label == null || control == null) {
			return;
		}

		// collapse
		if (control.isVisible()) {
			expanded = false;
			control.setVisible(false);
			control.setSize(SWT.DEFAULT, 0);
			getParent().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			resizeHeader();
			button.setIcon(IconTheme.ICON_NAME_EXPAND, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
					false);
			if (e != null) {
				fireCollapsed(new ExpandEvent(e));
			}
		} else { // expand
			final int richTextHeight = getControlHeight() + 20;
			expanded = true;
			control.setVisible(true);
			control.setSize(SWT.DEFAULT, richTextHeight);
			getParent().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			resizeHeader();
			button.setIcon(IconTheme.ICON_NAME_COLLAPSE, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_NO_COLOR),
					false);
			if (e != null) {
				fireExpanded(new ExpandEvent(e));
			}
		}
	}

	/**
	 * Resize the widget header
	 */
	private void resizeHeader() {
		final int labelHeight = ((GridData) label.getLayoutData()).heightHint + getHelperHeight() + 12;

		if (expanded) {
			final int richTextHeight = getControlHeight() + 10;
			((GridData) this.getLayoutData()).heightHint = labelHeight + richTextHeight;
		} else {
			((GridData) this.getLayoutData()).heightHint = labelHeight;
		}
	}

	/**
	 * @return the current control height
	 */
	private int getControlHeight() {
		if (isEditable()) {
			return richtext != null ? ((GridData) richtext.getLayoutData()).heightHint : SWT.DEFAULT;
		} else {
			return richtextNonEditable != null ? ((GridData) richtextNonEditable.getLayoutData()).heightHint
					: SWT.DEFAULT;
		}
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
	 * Set the text
	 * 
	 * @param text the text to set
	 */
	public void setValue(String text) {

		if (isEditable()) {
			richtext.setText(text);
		} else {
			setNonEditableValue(text);
		}

		// set header font
		if (text != null && !text.isEmpty()) {
			FontTools.setBoldFont(getRscMgr(), label);
		} else {
			FontTools.setNormalFont(getRscMgr(), label);
		}

		// layout
		this.requestLayout();
	}

	/**
	 * Set non editable value
	 * 
	 * @param value to value to set
	 */
	private void setNonEditableValue(String value) {
		// html format
		String header = "<!DOCTYPE html>\r\n<html>\r\n<head>\r\n" + //$NON-NLS-1$
				"<style>\r\n" + //$NON-NLS-1$
				"body {overflow: scroll;}\r\n" + //$NON-NLS-1$
				"</style>\r\n" + //$NON-NLS-1$
				"</head>\r\n" + //$NON-NLS-1$
				"<body>"; //$NON-NLS-1$
		String footer = "</body>\r\n</html>"; //$NON-NLS-1$

		value = gov.sandia.cf.tools.StringTools.getOrEmpty(value);
		richtextNonEditable.setText(header + value + footer);
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
	 * @return the rich text editor
	 */
	public RichTextEditor getRichtext() {
		return richtext;
	}

	/**
	 * @return the string value
	 */
	public String getValue() {
		if (isEditable()) {
			return richtext.getText();
		} else {
			return richtextNonEditable.getText();
		}
	}

	@Override
	public boolean isEnabled() {
		return this.enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		boolean previousValue = this.enabled;
		this.enabled = enabled;
		richtext.setEnabled(enabled);

		// update toolbar
		if (previousValue != enabled) {
			if (!enabled) {
				richtext.getEditorConfiguration().setOption(RichTextEditorConfiguration.TOOLBAR_GROUPS,
						PartsResourceConstants.RICH_EDITOR_EMPTY_TOOLBAR);
			} else {
				richtext.getEditorConfiguration().setOption(RichTextEditorConfiguration.TOOLBAR_GROUPS,
						PartsResourceConstants.RICH_EDITOR_TOOLBAR);
			}
		}
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
	@SuppressWarnings("unchecked")
	public RichTextWidget getControl() {
		return this;
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		if (richtext != null)
			richtext.addKeyListener(listener);
	}

	@Override
	public void setBackground(Color color) {
		if (labelComposite != null && !labelComposite.isDisposed()) {
			labelComposite.setBackground(color);
		}
		if (label != null && !label.isDisposed()) {
			label.setBackground(color);
		}
		super.setBackground(color);
	}
}
