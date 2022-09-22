/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.requirement;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.dialogs.GenericCFScrolledDialog;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.services.genericparam.IGenericParameterService;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.FormFieldWidget;
import gov.sandia.cf.parts.widgets.GenericValueFieldWidget;
import gov.sandia.cf.parts.widgets.LinkWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a requirement
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementDialog extends GenericCFScrolledDialog<SystemRequirementViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SystemRequirementDialog.class);

	/**
	 * the button name
	 */
	private String buttonName;

	/**
	 * Combo viewer
	 */
	private ComboViewer cbxParent;
	private FormFieldWidget<?> statementField;

	/**
	 * The model
	 */
	private Model model;

	/**
	 * List of parameters
	 */
	private List<SystemRequirementParam> parameters;

	/**
	 * List of viewer for each parameter
	 */
	private Map<SystemRequirementParam, GenericValueFieldWidget<SystemRequirementParam>> parameterViewers;

	/**
	 * The requirement selected
	 */
	private SystemRequirement parentSelected;

	/**
	 * The requirement to create
	 */
	private SystemRequirement requirement;

	/**
	 * The constructor
	 * 
	 * @param viewManager    the view manager
	 * @param parentShell    the parent shell
	 * @param requirement    the requirement to update (if null, put the dialog in
	 *                       create mode)
	 * @param parentSelected the requirement parent selected
	 * @param mode           the dialog mode
	 */
	public SystemRequirementDialog(SystemRequirementViewManager viewManager, Shell parentShell,
			SystemRequirement requirement, SystemRequirement parentSelected, ViewMode mode) {
		super(viewManager, parentShell);

		// Get model
		model = getViewManager().getCache().getModel();

		// Set parent
		if (parentSelected != null) {
			this.parentSelected = parentSelected;
		} else {
			this.parentSelected = (requirement != null) ? requirement.getParent() : null;
		}

		// Set mode
		if (mode == null) {
			mode = ViewMode.VIEW;
		}

		// Parameter columns
		parameters = getViewManager().getCache().getSystemRequirementSpecification().getParameters();

		// Manage view mode
		switch (mode) {
		case CREATE:
			this.requirement = new SystemRequirement();
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = ViewMode.CREATE;
			break;

		case UPDATE:
			this.requirement = requirement;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_UPDATE);
			this.mode = mode;
			break;

		case VIEW:
			this.requirement = requirement;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CLOSE);
			this.mode = mode;
			break;
		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		if (this.parentSelected == null) {
			setTitle(RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_GROUP_TITLE));
		} else {
			setTitle(RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_TITLE));
		}
		if (mode != ViewMode.VIEW) {
			if (this.parentSelected == null) {
				setMessage(RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_GROUP_DESCRIPTION),
						IMessageProvider.INFORMATION);
			} else {
				setMessage(RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_DESCRIPTION),
						IMessageProvider.INFORMATION);
			}
		}
	}

	/**
	 * @return the requirement created
	 */
	public SystemRequirement openDialog() {
		if (open() == Window.OK) {
			return requirement;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		// Sets the new title of the dialog
		String title = RscTools.empty();
		switch (mode) {
		case CREATE:
			if (this.parentSelected == null) {
				title = RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_GROUP_PAGENAME_ADD);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_PAGENAME_ADD);
			}
			break;
		case UPDATE:
			if (this.parentSelected == null) {
				title = RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_GROUP_PAGENAME_EDIT);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_PAGENAME_EDIT);
			}
			break;
		case VIEW:
			if (this.parentSelected == null) {
				title = RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_GROUP_PAGENAME_VIEW);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_PAGENAME_VIEW);
			}
			break;
		default:
			break;
		}
		newShell.setText(title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		String okButtonName = (buttonName != null && !buttonName.isEmpty()) ? buttonName : IDialogConstants.OK_LABEL;
		createButton(parent, IDialogConstants.OK_ID, okButtonName, true);
		if (!ViewMode.VIEW.equals(this.mode)) {
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Composite createDialogScrolledContent(Composite parent) {

		logger.debug("Create System Requirement dialog area"); //$NON-NLS-1$

		Composite content = createDefaultDialogScrolledContent(parent);

		// Select content type
		if (mode == ViewMode.VIEW) {
			renderNonEditableContent(content);
		} else {
			renderEditableContent(content);
		}

		return content;
	}

	/** {@inheritDoc} */
	@Override
	protected void loadDataAfterCreation() {

		if (requirement == null) {
			return;
		}

		// set requirement selected in parent view
		if (parentSelected != null && cbxParent != null) {
			final ISelection slParent = new StructuredSelection(parentSelected);
			cbxParent.setSelection(slParent);
		}

		// set statement
		statementField.setValue(requirement.getStatement());

		// set generic values
		if (requirement.getRequirementParameterList() != null) {
			for (SystemRequirementParam parameter : parameters) {

				// Get viewer
				GenericValueFieldWidget<SystemRequirementParam> viewer = parameterViewers.get(parameter);

				if (viewer != null) {
					SystemRequirementValue requirementParameterToUpdate = requirement
							.getRequirementParameterList().stream().filter(requirementParameterTemp -> viewer
									.getParameter().equals(requirementParameterTemp.getParameter()))
							.findFirst().orElse(null);

					viewer.setValue(requirementParameterToUpdate);
				}
			}
		}
	}

	/**
	 * Render Non Editable content
	 * 
	 * @param parent
	 */
	private void renderNonEditableContent(Composite parent) {

		// Initialize
		parameterViewers = new HashMap<>();

		// Parameter columns
		if (model != null) {

			// Statement
			FormFactory.createLabel(parent, RscTools.getString(RscConst.MSG_SYSREQUIREMENT_STATEMENT));
			statementField = FormFactory.createNonEditableFormFieldWidget(getViewManager(), parent, FormFieldType.TEXT,
					RscConst.MSG_SYSREQUIREMENT_STATEMENT, RscTools.getString(RscConst.MSG_SYSREQUIREMENT_STATEMENT));

			for (SystemRequirementParam param : parameters) {

				// check if the parameter is for the current level
				if (getViewManager().getClientService(IGenericParameterService.class)
						.isParameterAvailableForLevel(param, getCurrentLevel())) {

					// search the value for the parameter
					Optional<SystemRequirementValue> requirementValue = Optional.empty();
					if (requirement.getRequirementParameterList() != null) {
						requirementValue = requirement.getRequirementParameterList().stream()
								.filter(u -> param.equals(u.getParameter())).findFirst();
					}

					if (requirementValue.isPresent()) {
						GenericValueFieldWidget<SystemRequirementParam> nonEditableField = FormFactory
								.createNonEditableGenericValueWidget(getViewManager(), parent, param);

						// Add to viewer list
						parameterViewers.put(param, nonEditableField);
					}
				}
			}
		}

		// Check not null
		if (cbxParent != null) {
			List<SystemRequirement> inputs = new ArrayList<>();
			inputs.add(null);
			cbxParent.setInput(inputs);
		}
	}

	/**
	 * Render Editable content
	 * 
	 * @param parent
	 */
	private void renderEditableContent(Composite parent) {

		// create parent combobox
		if (mode != ViewMode.CREATE) {
			renderParentComboBox(parent);
		}

		// keep viewers in a map
		parameterViewers = new HashMap<>();

		// Statement
		FormFactory.createLabel(parent, RscTools.getString(RscConst.MSG_SYSREQUIREMENT_STATEMENT));
		statementField = FormFactory.createEditableFormFieldWidget(getViewManager(), parent, FormFieldType.TEXT,
				RscConst.MSG_SYSREQUIREMENT_STATEMENT, RscTools.getString(RscConst.MSG_SYSREQUIREMENT_STATEMENT));
		statementField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkStatementUnicity();
			}
		});

		// Generic values
		for (SystemRequirementParam param : parameters) {
			if (getViewManager().getClientService(IGenericParameterService.class).isParameterAvailableForLevel(param,
					getCurrentLevel())) {

				// render field
				GenericValueFieldWidget<SystemRequirementParam> editableField = FormFactory
						.createEditableGenericValueWidget(getViewManager(), parent, param);

				// check link
				if (FormFieldType.LINK.equals(editableField.getType())) {
					editableField.addLinkChangedListener(event -> checkLink(editableField.getLinkWidget()));
				}

				// Add to viewer list
				parameterViewers.put(param, editableField);
			}
		}
	}

	/**
	 * Render the requirement parameter parent combobox
	 * 
	 * @param parent the parent composite
	 */
	private void renderParentComboBox(Composite parent) {

		// label Requirement
		Label lblParent = new Label(parent, SWT.NONE);
		lblParent.setText(RscTools.getString(RscConst.MSG_DIALOG_SYSREQUIREMENT_PARENT));

		// combo Requirement
		cbxParent = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		GridData dataParent = new GridData();
		dataParent.grabExcessHorizontalSpace = true;
		dataParent.horizontalAlignment = GridData.FILL;
		cbxParent.getCombo().setLayoutData(dataParent);
		cbxParent.setContentProvider(new ArrayContentProvider());
		cbxParent.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((SystemRequirement) element).getFullGeneratedId();
			}
		});

		// Initialize input list
		List<SystemRequirement> inputs = new ArrayList<>();

		// Get input list tree
		if (model != null) {

			List<SystemRequirement> roots = getViewManager().getAppManager()
					.getService(ISystemRequirementApplication.class).getRequirementRootByModel(model);

			if (roots != null) {

				// compute requirements
				List<SystemRequirement> requirements = roots.stream().map(root -> root.getChildrenTree(true))
						.flatMap(List::stream).collect(Collectors.toList());

				// search for the current requirement
				for (SystemRequirement child : requirements) {
					if (parentSelected != null && parentSelected.getLevel().equals(child.getLevel())) {
						inputs.add(child);
					}
				}
			}
		}

		cbxParent.setInput(inputs);
		cbxParent.getCombo().addKeyListener(new ComboDropDownKeyListener());
		cbxParent.getCombo().setEnabled(true);
	}

	/**
	 * @return the current requirement level
	 */
	private int getCurrentLevel() {
		int parentLevel = parentSelected != null ? parentSelected.getLevel() : -1;
		return requirement.getId() != null ? requirement.getLevel() : parentLevel + 1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		// View - Just exit
		if (mode == ViewMode.VIEW) {
			super.okPressed();
		}

		// Save - Validate form and populate the requirement
		else {
			save();
		}
	}

	/**
	 * Save requirement
	 */
	private void save() {

		// retrieve parent selected
		setParentSelection();

		// Initialize
		boolean formValid = true;
		parameterViewers.forEach((param, viewer) -> viewer.clearHelper());

		if (StringUtils.isBlank(statementField.getValue())) {
			formValid = false;
			statementField.setHelper(
					NotificationFactory.getNewError(RscTools.getString(RscConst.EX_REQUIREMENT_STATEMENT_NULL)));
		}

		// Requirement parameter list
		List<SystemRequirementValue> tempValues = getValuesInput();

		// check validity
		for (SystemRequirementValue value : tempValues) {

			// validate constraints
			Notification notification = getViewManager().getClientService(IGenericParameterService.class)
					.checkValid(value, tempValues);

			if (notification != null) {
				formValid &= !notification.isError();
				parameterViewers.get(value.getParameter()).setHelper(notification);
			}
		}

		// If form not valid
		if (!formValid) {
			return;
		}

		// Set Values
		requirement.setStatement(statementField.getValue());

		// Set Parent
		requirement.setParent(parentSelected);

		// Set value list
		List<SystemRequirementValue> toPersistValues = new ArrayList<>();
		for (SystemRequirementValue tempValue : tempValues) {

			// Initialize
			SystemRequirementValue toPersistValue = null;
			SystemRequirementValue existingValue = null;

			if (requirement.getRequirementParameterList() != null) {
				existingValue = requirement.getRequirementParameterList().stream()
						.filter(up -> tempValue.getParameter().equals(up.getParameter())).findFirst().orElse(null);
			}

			// Found - Set the values to update
			if (existingValue != null) {
				toPersistValue = existingValue;
				toPersistValue.setValue(tempValue.getValue());
			} else {
				toPersistValue = tempValue.copy();
				toPersistValue.setDateCreation(new Date());
				toPersistValue.setUserCreation(getViewManager().getCache().getUser());
			}

			toPersistValue.setRequirement(requirement);
			toPersistValue.setParameter(tempValue.getParameter());

			toPersistValues.add(toPersistValue);
		}
		requirement.setRequirementParameterList(toPersistValues);

		// Call super
		super.okPressed();
	}

	/**
	 * Fulfill the widgets input into a list of requirement values.
	 * 
	 * This values are detached from the database to validate the data before
	 * persisting.
	 * 
	 * @return the input values list
	 */
	private List<SystemRequirementValue> getValuesInput() {

		List<SystemRequirementValue> tempValues = new ArrayList<>();

		for (SystemRequirementParam parameter : parameters) {

			// Initialize
			SystemRequirementValue genericValue = null;

			// Get viewer
			GenericValueFieldWidget<SystemRequirementParam> viewer = parameterViewers.get(parameter);

			if (viewer == null) {
				continue;
			}

			if (requirement.getRequirementParameterList() != null) {
				genericValue = requirement.getRequirementParameterList().stream()
						.filter(up -> viewer.getParameter().equals(up.getParameter())).findFirst().orElse(null);
			}

			// Not found - create
			if (genericValue == null) {
				// Create SystemRequirementParameter
				genericValue = new SystemRequirementValue();
				genericValue.setParameter(parameter);
			} else {
				// make a copy to detach managed entity
				genericValue = genericValue.copy();
			}

			// get value
			String textValue = viewer.getValue() != null && viewer.getValue().isEmpty() ? null : viewer.getValue();

			// Set Value
			genericValue.setValue(textValue);
			tempValues.add(genericValue);
		}

		return tempValues;
	}

	/**
	 * Set the parent selection
	 */
	private void setParentSelection() {

		// Check parent selection
		if (cbxParent != null) {
			ISelection selection = cbxParent.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				parentSelected = (SystemRequirement) structuredSelection.getFirstElement();
				if (parentSelected.getId() == null) {
					parentSelected = null;
				}
			}
		}
	}

	/**
	 * Check the statement unicity and add a helper if needed under the text field
	 */
	private void checkStatementUnicity() {
		boolean isError = false;

		Notification notification = NotificationFactory.getNewWarning();

		// tests Name
		if (statementField.getValue() == null || statementField.getValue().isEmpty()) {
			notification.addMessage(RscTools.getString(RscConst.ERR_SYSREQUIREMENT_STATEMENT_MANDATORY));
			isError = true;
		} else {

			// check if statement already exists
			try {
				boolean exists = getViewManager().getAppManager().getService(ISystemRequirementApplication.class)
						.existsRequirementStatement(new Integer[] { requirement.getId() }, statementField.getValue());
				if (exists) {
					notification.addMessage(RscTools.getString(RscConst.ERR_SYSREQUIREMENT_STATEMENT_DUPLICATED,
							statementField.getValue()));
					isError = true;
				}
			} catch (CredibilityException e) {
				logger.error("An error occured while retrieving the requirement statements", e); //$NON-NLS-1$
				notification.addMessage(e.getMessage());
				isError = true;
			}
		}

		if (isError) {
			statementField.setHelper(notification);
		} else {
			statementField.clearHelper();
		}

		// change ok button
		setEnableOkButton(!isError);
	}

	/**
	 * Check the link changes.
	 * 
	 * @param link the link widget to verify
	 */
	private void checkLink(LinkWidget link) {
		if (link != null) {
			// Enable/disable ok button
			setEnableOkButton(link.isValid());
			link.validateLink();
		}
	}

}
