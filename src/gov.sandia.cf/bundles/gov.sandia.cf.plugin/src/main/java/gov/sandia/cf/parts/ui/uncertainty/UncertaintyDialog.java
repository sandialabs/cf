/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.uncertainty;

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

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.application.uncertainty.IUncertaintyApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintyValue;
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
 * Dialog to add a uncertainty group
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyDialog extends GenericCFScrolledDialog<UncertaintyViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UncertaintyDialog.class);

	/**
	 * List of parameters
	 */
	private List<UncertaintyParam> parameters;

	/**
	 * the Name Text
	 */
	private FormFieldWidget<?> txtName;

	/**
	 * The model
	 */
	private Model model;

	/**
	 * The uncertainty to create
	 */
	private Uncertainty uncertainty;

	/**
	 * The uncertainty group selected
	 */
	private Uncertainty groupSelected;

	/**
	 * Combo viewer
	 */
	private ComboViewer cbxUncertaintyGroup;

	/**
	 * the button name
	 */
	private String buttonName;

	/**
	 * List of viewer for each parameter
	 */
	private Map<UncertaintyParam, GenericValueFieldWidget<UncertaintyParam>> parameterViewers;

	/**
	 * The constructor
	 * 
	 * @param viewManager   the view manager
	 * @param parentShell   the parent shell
	 * @param uncertainty   the uncertainty group to update (if null, put the dialog
	 *                      in create mode)
	 * @param groupSelected the uncertainty group
	 * @param mode          the dialog mode
	 */
	public UncertaintyDialog(UncertaintyViewManager viewManager, Shell parentShell, Uncertainty uncertainty,
			Uncertainty groupSelected, ViewMode mode) {
		super(viewManager, parentShell);

		// Get model
		model = getViewManager().getCache().getModel();

		// Set group
		if (groupSelected != null) {
			this.groupSelected = groupSelected;
		} else {
			this.groupSelected = (uncertainty != null) ? uncertainty.getParent() : null;
		}

		// Set mode
		if (mode == null) {
			mode = ViewMode.VIEW;
		}

		// Parameter columns
		parameters = getViewManager().getCache().getUncertaintySpecification().getParameters();

		// Manage view mode
		switch (mode) {
		case CREATE:
			this.uncertainty = new Uncertainty();
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = ViewMode.CREATE;
			break;

		case UPDATE:
			this.uncertainty = uncertainty;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_UPDATE);
			this.mode = mode;
			break;

		case VIEW:
			this.uncertainty = uncertainty;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CLOSE);
			this.mode = mode;
			break;
		default:
			break;
		}
	}

	@Override
	public void create() {
		super.create();

		if (this.groupSelected == null) {
			setTitle(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_TITLE));
		} else {
			setTitle(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_TITLE));
		}

		if (mode != ViewMode.VIEW) {
			if (this.groupSelected == null) {
				setMessage(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_DESCRIPTION),
						IMessageProvider.INFORMATION);
			} else {
				setMessage(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_DESCRIPTION),
						IMessageProvider.INFORMATION);
			}
		}
	}

	/**
	 * @return the uncertainty group created
	 */
	public Uncertainty openDialog() {
		if (open() == Window.OK) {
			return uncertainty;
		}
		return null;
	}

	@Override
	protected Composite createDialogScrolledContent(Composite parent) {

		logger.debug("Create Uncertainty dialog area"); //$NON-NLS-1$

		Composite content = createDefaultDialogScrolledContent(parent);

		// Select content type
		if (mode == ViewMode.VIEW) {
			renderNonEditableContent(content);
		} else {
			renderEditableContent(content);
		}

		return content;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		// Sets the new title of the dialog
		String title = RscTools.empty();
		switch (mode) {
		case CREATE:
			if (this.groupSelected == null) {
				title = RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_ADD);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_PAGENAME_ADD);
			}
			break;
		case UPDATE:
			if (this.groupSelected == null) {
				title = RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_EDIT);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_PAGENAME_EDIT);
			}
			break;
		case VIEW:
			if (this.groupSelected == null) {
				title = RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP_PAGENAME_VIEW);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_PAGENAME_VIEW);
			}
			break;
		default:
			break;
		}
		newShell.setText(title);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		String okButtonName = (buttonName != null && !buttonName.isEmpty()) ? buttonName : IDialogConstants.OK_LABEL;
		createButton(parent, IDialogConstants.OK_ID, okButtonName, true);
		if (!ViewMode.VIEW.equals(this.mode)) {
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		}
	}

	/**
	 * Render Non Editable content
	 * 
	 * @param parent the parent composite
	 */
	private void renderNonEditableContent(Composite parent) {

		// label Name
		FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_UNCERTAINTY_NAME)));

		// text Name
		txtName = FormFactory.createNonEditableFormFieldWidget(getViewManager(), parent, FormFieldType.TEXT, null,
				null);

		// keep viewers in a map
		parameterViewers = new HashMap<>();

		// Get model
		model = getViewManager().getCache().getModel();

		// Parameter columns
		if (model != null) {

			for (UncertaintyParam param : parameters) {

				// check if the parameter is for the current level
				if (getViewManager().getClientService(IGenericParameterService.class)
						.isParameterAvailableForLevel(param, getCurrentLevel())) {

					// search the value for the parameter
					Optional<UncertaintyValue> uncertaintyValue = Optional.empty();
					if (uncertainty.getValues() != null) {
						uncertaintyValue = uncertainty.getValues().stream().filter(u -> param.equals(u.getParameter()))
								.findFirst();
					}

					if (uncertaintyValue.isPresent()) {
						GenericValueFieldWidget<UncertaintyParam> nonEditableField = FormFactory
								.createNonEditableGenericValueWidget(getViewManager(), parent, param);

						// Add to viewer list
						parameterViewers.put(param, nonEditableField);
					}
				}
			}
		}
	}

	/**
	 * Render Editable content
	 * 
	 * @param parent the parent composite
	 */
	private void renderEditableContent(Composite parent) {

		// create parent combobox
		if (mode != ViewMode.CREATE) {
			renderParentComboBox(parent);
		}

		// label name
		FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_UNCERTAINTY_NAME)));

		// text name
		txtName = FormFactory.createEditableFormFieldWidget(getViewManager(), parent, FormFieldType.TEXT, null, null);
		txtName.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				checkNameUnicity();
			}
		});

		// keep viewers in a map
		parameterViewers = new HashMap<>();

		for (UncertaintyParam param : parameters) {

			if (getViewManager().getClientService(IGenericParameterService.class).isParameterAvailableForLevel(param,
					getCurrentLevel())) {

				// render field
				GenericValueFieldWidget<UncertaintyParam> editableField = FormFactory
						.createEditableGenericValueWidget(getViewManager(), parent, param);

				// check link
				if (FormFieldType.LINK.equals(editableField.getType())) {
					editableField.addLinkChangedListener(event -> checkLink(editableField.getLinkWidget()));
				}

				// Add to viewer list
				parameterViewers.put(param, editableField);
			}
		}

		// dialog behavior
		txtName.setFocus();
	}

	/**
	 * Render the uncertainty parameter parent combobox
	 * 
	 * @param parent the parent composite
	 */
	private void renderParentComboBox(Composite parent) {

		// label uncertainty
		Label lblParent = new Label(parent, SWT.NONE);
		lblParent.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP));

		// combo uncertainty
		cbxUncertaintyGroup = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		GridData dataParent = new GridData();
		dataParent.grabExcessHorizontalSpace = true;
		dataParent.horizontalAlignment = GridData.FILL;
		cbxUncertaintyGroup.getCombo().setLayoutData(dataParent);
		cbxUncertaintyGroup.setContentProvider(new ArrayContentProvider());
		cbxUncertaintyGroup.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Uncertainty) element).getFullGeneratedId();
			}
		});

		// Initialize input list
		List<Uncertainty> inputs = new ArrayList<>();

		// Get input list tree
		if (model != null) {

			List<Uncertainty> roots = getViewManager().getAppManager().getService(IUncertaintyApplication.class)
					.getUncertaintyGroupByModel(model);

			if (roots != null) {

				// compute uncertainties
				List<Uncertainty> uncertainties = roots.stream().map(root -> root.getChildrenTree(true))
						.flatMap(List::stream).collect(Collectors.toList());

				// search for the current uncertainty
				for (Uncertainty child : uncertainties) {
					if (groupSelected != null && groupSelected.getLevel().equals(child.getLevel())) {
						inputs.add(child);
					}
				}
			}
		}

		cbxUncertaintyGroup.setInput(inputs);
		cbxUncertaintyGroup.getCombo().addKeyListener(new ComboDropDownKeyListener());
		cbxUncertaintyGroup.getCombo().setEnabled(true);
	}

	/** {@inheritDoc} */
	@Override
	protected void loadDataAfterCreation() {

		if (uncertainty == null) {
			return;
		}

		// set uncertainty group selected in parent view
		if (groupSelected != null && cbxUncertaintyGroup != null) {
			final ISelection slParent = new StructuredSelection(groupSelected);
			cbxUncertaintyGroup.setSelection(slParent);
		}

		// set Uncertainty Name
		if (txtName != null) {
			txtName.setValue(uncertainty.getName());
		}

		// Set generic parameters
		if (uncertainty.getValues() != null) {

			for (UncertaintyParam parameter : parameters) {

				// Get viewer
				GenericValueFieldWidget<UncertaintyParam> viewer = parameterViewers.get(parameter);

				if (viewer != null) {
					UncertaintyValue uncertaintyParameterToUpdate = uncertainty.getValues().stream()
							.filter(uncertaintyParameterTemp -> viewer.getParameter()
									.equals(uncertaintyParameterTemp.getParameter()))
							.findFirst().orElse(null);

					viewer.setValue(uncertaintyParameterToUpdate);
				}
			}
		}
	}

	/**
	 * @return the current level
	 */
	private int getCurrentLevel() {
		int parentLevel = groupSelected != null ? groupSelected.getLevel() : -1;
		return uncertainty.getId() != null ? uncertainty.getLevel() : parentLevel + 1;
	}

	/**
	 * Check the uncertainty name unicity and add a helper if needed under the
	 * txtName field
	 */
	private void checkNameUnicity() {
		boolean isError = false;

		Notification notification = NotificationFactory.getNewWarning();

		// tests Name
		if (txtName.getValue() == null || txtName.getValue().isEmpty()) {
			notification.addMessage(RscTools.getString(RscConst.ERR_UNCERTAINTY_NAME_MANDATORY));
			isError = true;
		} else {

			// check if uncertainty name already exists
			try {
				boolean existsUncertaintyName = getViewManager().getAppManager().getService(IDecisionApplication.class)
						.existsDecisionTitle(new Integer[] { uncertainty.getId() }, txtName.getValue());
				if (existsUncertaintyName) {
					notification.addMessage(
							RscTools.getString(RscConst.ERR_UNCERTAINTY_NAME_DUPLICATED, txtName.getValue()));
					isError = true;
				}
			} catch (CredibilityException e) {
				logger.error("An error occured while retrieving the qoi names", e); //$NON-NLS-1$
				notification.addMessage(e.getMessage());
				isError = true;
			}
		}

		if (isError) {
			txtName.setHelper(notification);
		} else {
			txtName.clearHelper();
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

	@Override
	protected void okPressed() {
		// View - Just exit
		if (mode == ViewMode.VIEW) {
			super.okPressed();
		}

		// Save - Validate form and populate the uncertainty
		else {
			save();
		}
	}

	/**
	 * Save Uncertainty
	 */
	private void save() {

		// retrieve parent selected
		setParentSelection();

		// Initialize
		boolean formValid = true;
		txtName.clearHelper();
		parameterViewers.forEach((param, viewer) -> viewer.clearHelper());

		// check name
		if (StringUtils.isBlank(txtName.getValue())) {
			formValid = false;
			txtName.setHelper(NotificationFactory.getNewError(RscTools.getString(
					RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED, RscTools.getString(RscConst.MSG_UNCERTAINTY_NAME))));
		}

		// Uncertainty parameter list
		List<UncertaintyValue> tempValues = getValuesInput();

		// check validity
		for (UncertaintyValue value : tempValues) {

			// validate constraints
			Notification notification = getViewManager().getClientService(IGenericParameterService.class)
					.checkValid(value, tempValues);

			if (notification != null) {
				formValid &= !notification.isError();
				parameterViewers.get(value.getParameter()).setHelper(notification);
			} else {
				GenericValueFieldWidget<UncertaintyParam> genericValueFieldWidget = parameterViewers
						.get(value.getParameter());
				if (FormFieldType.LINK.equals(genericValueFieldWidget.getType())) {
					formValid &= genericValueFieldWidget.validate();
				}
			}
		}

		// If form not valid
		if (!formValid) {
			return;
		}

		setErrorMessage(null);

		// Set title
		uncertainty.setName(txtName.getValue());

		// Set Group
		uncertainty.setParent(groupSelected);

		// Set value list
		List<UncertaintyValue> toPersistValues = new ArrayList<>();
		for (UncertaintyValue tempValue : tempValues) {

			// Initialize
			UncertaintyValue toPersistValue = null;
			UncertaintyValue existingValue = null;

			if (uncertainty.getValues() != null) {
				existingValue = uncertainty.getValues().stream()
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

			toPersistValue.setUncertainty(uncertainty);
			toPersistValue.setParameter(tempValue.getParameter());

			toPersistValues.add(toPersistValue);
		}
		uncertainty.setValues(toPersistValues);

		// Call super
		super.okPressed();
	}

	/**
	 * Fulfill the widgets input into a list of uncertainty values.
	 * 
	 * This values are detached from the database to validate the data before
	 * persisting.
	 * 
	 * @return the input values list
	 */
	private List<UncertaintyValue> getValuesInput() {

		List<UncertaintyValue> tempValues = new ArrayList<>();

		for (UncertaintyParam parameter : parameters) {

			// Initialize
			UncertaintyValue genericValue = null;

			// Get viewer
			GenericValueFieldWidget<UncertaintyParam> viewer = parameterViewers.get(parameter);

			if (viewer == null) {
				continue;
			}

			if (uncertainty.getValues() != null) {
				genericValue = uncertainty.getValues().stream()
						.filter(up -> viewer.getParameter().equals(up.getParameter())).findFirst().orElse(null);
			}

			// Not found - create
			if (genericValue == null) {
				// Create UncertaintyParameter
				genericValue = new UncertaintyValue();
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
		if (cbxUncertaintyGroup != null) {
			ISelection selection = cbxUncertaintyGroup.getSelection();
			if (!selection.isEmpty()) {
				IStructuredSelection structuredSelection = (IStructuredSelection) selection;
				groupSelected = (Uncertainty) structuredSelection.getFirstElement();
				if (groupSelected.getId() == null) {
					groupSelected = null;
				}
			}
		}
	}
}
