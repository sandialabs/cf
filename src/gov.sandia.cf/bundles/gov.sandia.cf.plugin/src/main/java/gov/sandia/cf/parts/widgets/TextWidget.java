/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.parts.listeners.FloatVerifyListener;
import gov.sandia.cf.tools.RscTools;

/**
 * The Text widget with helper
 * 
 * @author Didier Verstraete
 *
 */
public class TextWidget extends AHelperWidget {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(TextWidget.class);

	/**
	 * The Text Widget type
	 */
	@SuppressWarnings("javadoc")
	public enum TextWidgetType {
		STRING, FLOAT, PASSWORD;
	}

	private Object id;

	// editable fields
	private Text text;

	// non editable fields
	private Label textNonEditable;

	private TextWidgetType type;

	/**
	 * The constructor
	 * 
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the composite parent
	 * @param style    the style
	 * @param editable is editable?
	 * @param id       the id to set
	 */
	public TextWidget(ResourceManager rscMgr, Composite parent, int style, boolean editable, Object id) {
		this(rscMgr, parent, style, editable, TextWidgetType.STRING, id);
	}

	/**
	 * The constructor
	 * 
	 * @param rscMgr   the resource manager used to manage the resources (fonts,
	 *                 colors, images, cursors...)
	 * @param parent   the composite parent
	 * @param style    the style
	 * @param editable is editable?
	 * @param type     the text widget type (string, integer...)
	 * @param id       the id to set
	 */
	public TextWidget(ResourceManager rscMgr, Composite parent, int style, boolean editable, TextWidgetType type,
			Object id) {
		super(rscMgr, parent, style, editable);

		// view Manager
		this.id = id;
		this.type = type;

		// create control
		createControl();
	}

	/**
	 * Create the form field content
	 */
	private void createControl() {

		logger.debug("Create form value field widget content"); //$NON-NLS-1$

		// render control
		if (super.isEditable()) {
			renderEditableField();
		} else {
			renderNonEditableField();
		}

		// create helper
		super.createHelper();
	}

	/**
	 * Render editable control
	 */
	private void renderEditableField() {
		int style = SWT.LEFT | SWT.SINGLE | SWT.WRAP | SWT.BORDER;
		if (TextWidgetType.PASSWORD.equals(type)) {
			style |= SWT.PASSWORD;
		}
		text = FormFactory.createText(this, id, style);

		// set float verify listener
		if (TextWidgetType.FLOAT.equals(type)) {
			text.addVerifyListener(new FloatVerifyListener());
		}
	}

	/**
	 * Render non editable control
	 */
	private void renderNonEditableField() {
		int style = SWT.LEFT | SWT.WRAP | SWT.V_SCROLL;
		if (TextWidgetType.PASSWORD.equals(type)) {
			style |= SWT.PASSWORD;
		}
		textNonEditable = FormFactory.createNonEditableText(this, RscTools.empty(), style);
	}

	/**
	 * @return the currently activated control
	 */
	@Override
	@SuppressWarnings("unchecked")
	public TextWidget getControl() {
		return this;
	}

	/**
	 * @return the current value
	 */
	public String getValue() {
		if (super.isEditable()) {
			return getEditableValue();
		} else {
			return getNonEditableValue();
		}
	}

	/**
	 * @return the editable field value
	 */
	private String getEditableValue() {
		return text != null ? text.getText() : null;
	}

	/**
	 * @return the non editable field value
	 */
	private String getNonEditableValue() {
		return textNonEditable != null ? textNonEditable.getText() : null;
	}

	/**
	 * Set the form value
	 * 
	 * @param textValue the value to set
	 */
	public void setValue(String textValue) {
		if (super.isEditable()) {
			setEditableValue(textValue);
		} else {
			setNonEditableValue(textValue);
		}

		if (textValue != null) {
			setSelection(textValue.length());
		}
	}

	/**
	 * Set editable field value
	 * 
	 * @param value the form value to set
	 */
	private void setEditableValue(String textValue) {
		text.setText(gov.sandia.cf.tools.StringTools.getOrEmpty(textValue));
	}

	/**
	 * Set non editable field value
	 * 
	 * @param textValue the value to set
	 */
	private void setNonEditableValue(String textValue) {
		textNonEditable.setText(gov.sandia.cf.tools.StringTools.getOrEmpty(textValue));
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		if (text != null)
			text.addKeyListener(listener);
	}

	@SuppressWarnings("javadoc")
	public void setSelection(int selection) {
		if (text != null)
			text.setSelection(selection);
	}

	@SuppressWarnings("javadoc")
	public void setTextLimit(int textLimit) {
		if (text != null)
			text.setTextLimit(textLimit);
	}

	@Override
	public void addListener(int eventType, Listener listener) {
		if (text != null)
			text.addListener(eventType, listener);
		super.addListener(eventType, listener);
	}

	@Override
	public void setEnabled(boolean enabled) {
		if (super.isEditable()) {
			text.setEnabled(enabled);
		} else {
			textNonEditable.setEnabled(enabled);
		}
	}
}
