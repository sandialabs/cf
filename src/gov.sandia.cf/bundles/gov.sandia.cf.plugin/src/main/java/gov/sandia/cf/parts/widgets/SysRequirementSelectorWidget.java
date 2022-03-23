/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.parts.theme.ButtonTheme;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.parts.ui.requirement.SystemRequirementSelectorDialog;
import gov.sandia.cf.tools.MathTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The System Requirement widget to select one.
 * 
 * @author Didier Verstraete
 *
 */
public class SysRequirementSelectorWidget extends AHelperWidget {

	private Text textInput;
	private SystemRequirement requirementSelected;
	private ButtonTheme btnSelect;
	private Label textSysReqNonEditable;
	private IViewManager viewManager;

	/**
	 * @param viewManager the view manager
	 * @param parent      the parent composite
	 * @param style       the style
	 * @param editable    is editable?
	 */
	public SysRequirementSelectorWidget(IViewManager viewManager, Composite parent, int style, boolean editable) {
		super(viewManager.getRscMgr(), parent, style, editable);

		Assert.isNotNull(viewManager);
		this.viewManager = viewManager;

		// create widget
		createControl();
	}

	/**
	 * Create the link content
	 */
	private void createControl() {

		// layout data
		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginLeft = 0;
		gridLayout.marginRight = 0;
		this.setLayout(gridLayout);
		GridData gdContainer = new GridData(SWT.FILL, SWT.FILL, true, false);
		this.setLayoutData(gdContainer);

		// render control
		if (super.isEditable()) {
			renderEditableField();
		} else {
			renderNonEditableField();
		}

		// reload
		reload();

		// create helper
		super.createHelper();
	}

	/**
	 * Render editable control
	 */
	private void renderEditableField() {
		// Text System Requirement - Input text
		textInput = new Text(this, SWT.LEFT | SWT.WRAP | SWT.BORDER);
		GridData gdUrlInput = new GridData();
		gdUrlInput.grabExcessHorizontalSpace = true;
		gdUrlInput.horizontalAlignment = GridData.FILL;
		textInput.setLayoutData(gdUrlInput);
		textInput.setEditable(false);

		// Select button
		Map<String, Object> btnSelectOptions = new HashMap<>();
		btnSelectOptions.put(ButtonTheme.OPTION_TEXT, RscTools.getString(RscConst.MSG_BTN_SELECT));
		btnSelectOptions.put(ButtonTheme.OPTION_ENABLED, false);
		btnSelectOptions.put(ButtonTheme.OPTION_OUTLINE, true);
		btnSelect = new ButtonTheme(viewManager.getRscMgr(), this, SWT.CENTER, btnSelectOptions);
		btnSelect.addListener(SWT.Selection, e -> openSelectDialog());
	}

	/**
	 * Render non editable control
	 */
	private void renderNonEditableField() {
		textSysReqNonEditable = FormFactory.createNonEditableText(this, RscTools.empty());
	}

	/**
	 * Search and set the system requirement matching id
	 * 
	 * @param id the id of the system requirement to set
	 */
	public void setIdValue(String id) {

		if (MathTools.isInteger(id)) {

			// search system requirement by id
			SystemRequirement requirement = viewManager.getAppManager().getService(ISystemRequirementApplication.class)
					.getRequirementById(Integer.valueOf(id));

			// set value
			if (super.isEditable()) {
				setEditableValue(requirement);
			} else {
				setNonEditableValue(requirement);
			}
		}
	}

	/**
	 * @return the current value
	 */
	public SystemRequirement getValue() {
		return requirementSelected;
	}

	/**
	 * @return the current value as string
	 */
	public String getTextValue() {
		return requirementSelected != null ? requirementSelected.getAbstract() : RscTools.empty();
	}

	/**
	 * @return the system requirement selected
	 */
	public Integer getValueId() {
		return requirementSelected != null ? requirementSelected.getId() : null;
	}

	/**
	 * Set the form value
	 * 
	 * @param value the value to set
	 */
	public void setValue(SystemRequirement value) {
		if (super.isEditable()) {
			setEditableValue(value);
		} else {
			setNonEditableValue(value);
		}
	}

	/**
	 * Set editable field value
	 * 
	 * @param requirement the form value to set
	 */
	private void setEditableValue(SystemRequirement requirement) {
		if (requirement != null) {
			requirementSelected = requirement;
		}

		// set the text
		textInput.setText(getTextValue());
	}

	/**
	 * Set non editable field value
	 * 
	 * @param requirement the value to set
	 */
	private void setNonEditableValue(SystemRequirement requirement) {

		requirementSelected = requirement;

		// set the text
		textSysReqNonEditable
				.setText(requirementSelected != null ? requirementSelected.getAbstract() : RscTools.empty());

		if (requirementSelected != null) {
			viewManager.plugSystemRequirementsButton(textSysReqNonEditable);
		}
	}

	/**
	 * Open the selected dialog
	 */
	private void openSelectDialog() {

		// open dialog
		SystemRequirementSelectorDialog<?> dlg = new SystemRequirementSelectorDialog<>(viewManager, getShell());
		SystemRequirement newRequirementSelected = dlg.openDialog();

		if (newRequirementSelected != null) {
			requirementSelected = newRequirementSelected;
		}

		// set the text
		textInput.setText(requirementSelected != null ? requirementSelected.getAbstract() : RscTools.empty());
	}

	/**
	 * Reload the widget
	 */
	public void reload() {

		// load system requirement
		if (btnSelect != null) {
			boolean isRequirementEnabled = viewManager.getAppManager().getService(ISystemRequirementApplication.class)
					.isRequirementEnabled(viewManager.getCache().getModel());
			btnSelect.setEnabled(isRequirementEnabled);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public SysRequirementSelectorWidget getControl() {
		return this;
	}
}
