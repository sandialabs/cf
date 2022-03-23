/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.List;
import java.util.Optional;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ExpandEvent;
import org.eclipse.swt.events.ExpandListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.widgets.LinkWidget.LinkChangedListener;
import gov.sandia.cf.parts.widgets.TextWidget.TextWidgetType;
import gov.sandia.cf.tools.MathTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * The Form field widget for each type of field
 * 
 * @author Didier Verstraete
 * @param <S> the generic parameter select value class
 *
 */
public class FormFieldWidget<S extends GenericParameterSelectValue<?>> extends Composite {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(FormFieldWidget.class);

	private boolean editable;
	private FormFieldType type;
	private String id;
	private String name;
	private List<S> selectValues;

	// fields
	private TextWidget textFloat;
	private LinkWidget link;
	private SysRequirementSelectorWidget sysReqSelector;
	private RichTextWidget richText;
	private SelectWidget<S> select;
	private TextWidget text;
	private PCMMElementSelectorWidget pcmmElementSelector;

	private IViewManager viewManager;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager to query database
	 * @param parent      the composite parent
	 * @param type        the form field type
	 * @param editable    is editable?
	 * @param id          the id to set
	 * @param name        the name of the field
	 */
	public FormFieldWidget(IViewManager viewManager, Composite parent, FormFieldType type, boolean editable, String id,
			String name) {
		super(parent, SWT.NONE);
		Assert.isNotNull(type);
		Assert.isNotNull(viewManager);
		// This constructor does not have the select values
		Assert.isTrue(!FormFieldType.SELECT.equals(type),
				"This constructor can not be used for field of type 'Select'"); //$NON-NLS-1$

		// view Manager
		this.viewManager = viewManager;
		this.id = id;
		this.name = name;
		this.type = type;
		this.editable = editable;

		// create widget
		createControl();
	}

	/**
	 * The constructor
	 * 
	 * @param viewManager  the view manager to query database
	 * @param parent       the composite parent
	 * @param type         the form field type
	 * @param editable     is editable?
	 * @param id           the id to set
	 * @param name         the name of the field
	 * @param selectValues the values for combo field
	 */
	public FormFieldWidget(IViewManager viewManager, Composite parent, FormFieldType type, boolean editable, String id,
			String name, List<S> selectValues) {
		super(parent, SWT.NONE);
		Assert.isNotNull(type);
		Assert.isNotNull(viewManager);
		Assert.isNotNull(selectValues);

		// view Manager
		this.viewManager = viewManager;
		this.id = id;
		this.name = name;
		this.selectValues = selectValues;
		this.type = type;
		this.editable = editable;

		// create widget
		createControl();
	}

	/**
	 * Create the form field content
	 */
	private void createControl() {

		logger.debug("Create form value field widget content"); //$NON-NLS-1$

		// layout data
		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		this.setLayout(gridLayout);
		GridData gdContainer = new GridData(SWT.FILL, SWT.FILL, true, false);
		this.setLayoutData(gdContainer);

		// render control
		if (editable) {
			renderEditableField();
		} else {
			renderNonEditableField();
		}
	}

	/**
	 * Render editable field
	 * 
	 * @param parent the parent composite
	 * @param value  the form value to display
	 */
	private void renderEditableField() {

		if (FormFieldType.CREDIBILITY_ELEMENT.equals(type)) {
			// Float text
			pcmmElementSelector = FormFactory.createCredibilityElementSelectorWidget(viewManager, this, id, true);
		} else if (FormFieldType.FLOAT.equals(type)) {
			// Float text
			textFloat = FormFactory.createTextWidget(viewManager.getRscMgr(), this, true, TextWidgetType.FLOAT, id);
		} else if (FormFieldType.LINK.equals(type)) {
			// Link Container
			link = FormFactory.createLinkWidget(this, viewManager, id, true);
		} else if (FormFieldType.RICH_TEXT.equals(type)) {
			// text
			boolean expanded = false;
			richText = FormFactory.createRichTextWidget(viewManager.getRscMgr(), this,
					RscTools.getString(RscConst.MSG_RICHTEXT_CLICK_BAR, name), id, expanded, true, true);

			// add expand listener to layout the parent form
			richText.addExpandListener(new ExpandListener() {

				@Override
				public void itemExpanded(ExpandEvent e) {
					getParent().layout();
				}

				@Override
				public void itemCollapsed(ExpandEvent e) {
					getParent().layout();
				}
			});

		} else if (FormFieldType.SELECT.equals(type)) {
			// Combo-box
			select = FormFactory.createSelectWidget(viewManager.getRscMgr(), this, true, id, selectValues);
		} else if (FormFieldType.SYSTEM_REQUIREMENT.equals(type)) {
			// System Requirement Container
			sysReqSelector = FormFactory.createSysRequirementSelectorWidget(viewManager, this, id, true);
		} else if (FormFieldType.TEXT.equals(type)) {
			// text
			text = FormFactory.createTextWidget(viewManager.getRscMgr(), this, true, id);
		}
	}

	/**
	 * Render non editable field
	 * 
	 * @param parent the parent composite
	 * @param value  the form value to display
	 */
	private void renderNonEditableField() {

		if (FormFieldType.CREDIBILITY_ELEMENT.equals(type)) {
			// value
			pcmmElementSelector = FormFactory.createCredibilityElementSelectorWidget(viewManager, this, id, false);
		} else if (FormFieldType.FLOAT.equals(type)) {
			// value
			textFloat = FormFactory.createTextWidget(viewManager.getRscMgr(), this, false, TextWidgetType.FLOAT, null);
		} else if (FormFieldType.LINK.equals(type)) {
			// value
			link = FormFactory.createLinkWidget(this, viewManager, RscTools.empty(), false);
		} else if (FormFieldType.RICH_TEXT.equals(type)) {
			// text
			boolean expanded = false;
			richText = FormFactory.createRichTextWidget(viewManager.getRscMgr(), this,
					RscTools.getString(RscConst.MSG_RICHTEXT_CLICK_BAR, name), id, expanded, false, true);

			// add expand listener to layout the parent form
			richText.addExpandListener(new ExpandListener() {

				@Override
				public void itemExpanded(ExpandEvent e) {
					getParent().layout();
				}

				@Override
				public void itemCollapsed(ExpandEvent e) {
					getParent().layout();
				}
			});
		} else if (FormFieldType.SELECT.equals(type)) {
			// value
			select = FormFactory.createSelectWidget(viewManager.getRscMgr(), this, false, id);
		} else if (FormFieldType.SYSTEM_REQUIREMENT.equals(type)) {
			// value
			sysReqSelector = FormFactory.createSysRequirementSelectorWidget(viewManager, this, id, false);
		} else if (FormFieldType.TEXT.equals(type)) {
			// value
			text = FormFactory.createTextWidget(viewManager.getRscMgr(), this, false, null);
		}
	}

	/**
	 * @return the form field type
	 */
	public FormFieldType getType() {
		return type;
	}

	@Override
	public void addKeyListener(KeyListener listener) {
		Control control = getControl();
		if (control != null) {
			control.addKeyListener(listener);

			// for richtext editors add modify listener
			if (FormFieldType.RICH_TEXT.equals(type) && control instanceof RichTextWidget) {
				((RichTextWidget) control).addModifyListener(e -> listener.notify());
			}
		}
	}

	/**
	 * Add a link changed listener
	 * 
	 * @param listener the listener to add
	 */
	public void addLinkChangedListener(LinkChangedListener listener) {
		if (FormFieldType.LINK.equals(type)) {
			link.addChangedListener(listener);
		}
	}

	/**
	 * @return the link widget (may be null if type is not LINK)
	 */
	public LinkWidget getLinkWidget() {
		return link;
	}

	/**
	 * @return the current value
	 */
	public String getValue() {

		if (FormFieldType.CREDIBILITY_ELEMENT.equals(type)) {
			if (editable) {
				return pcmmElementSelector.getValue() != null && pcmmElementSelector.getValue().getId() != null
						? pcmmElementSelector.getValue().getId().toString()
						: null;
			} else {
				return pcmmElementSelector.getValue() != null ? pcmmElementSelector.getValue().getAbstract()
						: RscTools.empty();
			}
		} else if (FormFieldType.FLOAT.equals(type)) {
			return textFloat.getValue();
		} else if (FormFieldType.LINK.equals(type)) {
			return link.getGSONValue();
		} else if (FormFieldType.RICH_TEXT.equals(type)) {
			return richText.getValue();
		} else if (FormFieldType.SELECT.equals(type) && select.getValue() != null) {
			if (editable) {
				return StringTools.getOrEmpty(select.getValue().getId());
			} else {
				return select.getValue().getName();
			}
		} else if (FormFieldType.SYSTEM_REQUIREMENT.equals(type)) {
			if (editable) {
				return sysReqSelector.getValueId() != null ? sysReqSelector.getValueId().toString() : null;
			} else {
				return sysReqSelector.getTextValue();
			}
		} else if (FormFieldType.TEXT.equals(type)) {
			return text.getValue();
		}

		return RscTools.empty();
	}

	/**
	 * Set the form value
	 * 
	 * @param textValue the value to set
	 */
	public void setValue(String textValue) {

		if (textValue == null) {
			textValue = RscTools.empty();
		}

		if (FormFieldType.CREDIBILITY_ELEMENT.equals(type)) {
			pcmmElementSelector.setIdValue(MathTools.isInteger(textValue) ? Integer.valueOf(textValue) : null);
		} else if (FormFieldType.FLOAT.equals(type)) {
			textFloat.setValue(textValue);
		} else if (FormFieldType.LINK.equals(type)) {
			link.setValue(textValue);
		} else if (FormFieldType.RICH_TEXT.equals(type)) {
			richText.setValue(textValue);
		} else if (FormFieldType.SELECT.equals(type)) {
			if (editable) {
				setEditableSelectValue(textValue);
			} else {
				setNonEditableSelectValue(textValue);
			}
		} else if (FormFieldType.SYSTEM_REQUIREMENT.equals(type)) {
			sysReqSelector.setIdValue(textValue);
		} else if (FormFieldType.TEXT.equals(type)) {
			text.setValue(textValue);
		}
	}

	/**
	 * Clear the helper and remove it
	 */
	public void clearHelper() {
		Control control = getControl();
		if (control instanceof AHelperWidget) {
			((AHelperWidget) control).clearHelper();
		}
	}

	/**
	 * Set an alert under the field
	 * 
	 * @param notification the notification to display
	 */
	public void setHelper(Notification notification) {
		Control control = getControl();
		if (control instanceof AHelperWidget) {
			((AHelperWidget) control).setHelper(notification);
		}
	}

	/**
	 * @return editable field
	 */
	public boolean isEditable() {
		return editable;
	}

	/**
	 * Set Combo select value
	 * 
	 * @param textValue the value to set
	 */
	private void setEditableSelectValue(String textValue) {
		// Set value
		List<S> parameterValues = selectValues;
		S parameterValue = parameterValues.stream()
				.filter(parameterValueTemp -> textValue.equals(parameterValueTemp.getId().toString())).findAny()
				.orElse(null);

		// Set parameter value
		if (parameterValue != null) {
			select.setValue(parameterValue);
		}
	}

	/**
	 * Set select value
	 * 
	 * @param textValue the value to set
	 */
	private void setNonEditableSelectValue(String textValue) {
		// Get value text
		Optional<S> parameterValue = selectValues.stream()
				.filter(param -> param != null && param.getId().toString().equals(textValue)).findFirst();

		if (parameterValue.isPresent()) {
			select.setValue(parameterValue.get());
		}
	}

	private Control getControl() {
		if (FormFieldType.CREDIBILITY_ELEMENT.equals(type)) {
			return pcmmElementSelector;
		} else if (FormFieldType.FLOAT.equals(type)) {
			return textFloat;
		} else if (FormFieldType.LINK.equals(type)) {
			return link;
		} else if (FormFieldType.RICH_TEXT.equals(type)) {
			return richText;
		} else if (FormFieldType.SELECT.equals(type)) {
			return select;
		} else if (FormFieldType.SYSTEM_REQUIREMENT.equals(type)) {
			return sysReqSelector;
		} else if (FormFieldType.TEXT.equals(type)) {
			return text;
		}

		return null;
	}
}
