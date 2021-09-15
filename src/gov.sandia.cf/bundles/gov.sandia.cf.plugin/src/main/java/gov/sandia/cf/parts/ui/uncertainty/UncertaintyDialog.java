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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IGenericParameterApplication;
import gov.sandia.cf.application.IUncertaintyApplication;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.GenericValueFieldWidget;
import gov.sandia.cf.parts.widgets.LinkWidget;
import gov.sandia.cf.parts.widgets.SelectWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a uncertainty group
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyDialog extends GenericCFSmallDialog<UncertaintyViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UncertaintyDialog.class);

	/**
	 * List of parameters
	 */
	private List<UncertaintyParam> parameters;

	/**
	 * The uncertainty to create
	 */
	private Uncertainty uncertainty;

	/**
	 * The uncertainty group selected
	 */
	private UncertaintyGroup groupSelected;

	/**
	 * Combo viewer
	 */
	private SelectWidget<UncertaintyGroup> cbxUncertaintyGroup;

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
			UncertaintyGroup groupSelected, DialogMode mode) {
		super(viewManager, parentShell);

		// Set group
		if (groupSelected != null) {
			this.groupSelected = groupSelected;
		} else {
			this.groupSelected = (uncertainty != null) ? uncertainty.getGroup() : null;
		}

		// Set mode
		if (mode == null) {
			mode = DialogMode.VIEW;
		}

		// Parameter columns
		parameters = getViewManager().getCache().getUncertaintySpecification().getParameters();

		// Manage view mode
		switch (mode) {
		case CREATE:
			this.uncertainty = new Uncertainty();
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = DialogMode.CREATE;
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
		setTitle(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_TITLE));
		if (mode != DialogMode.VIEW) {
			setMessage(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_DESCRIPTION), IMessageProvider.INFORMATION);
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
	protected Control createDialogArea(Composite parent) {

		logger.debug("Create Uncertainty dialog area"); //$NON-NLS-1$

		Composite container = (Composite) super.createDialogArea(parent);

		// scroll container
		ScrolledComposite scrollContainer = new ScrolledComposite(container, SWT.V_SCROLL);
		GridData scrollScData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scrollScData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scrollScData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		scrollContainer.setLayoutData(scrollScData);
		scrollContainer.setLayout(new GridLayout());

		// form container
		Composite formContainer = new Composite(scrollContainer, SWT.NONE);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		formContainer.setLayoutData(scData);
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		// Select content type
		if (mode == DialogMode.VIEW) {
			renderNonEditableContent(formContainer);
		} else {
			renderEditableContent(formContainer);
		}

		// set scroll container size
		scrollContainer.setContent(formContainer);
		scrollContainer.setExpandHorizontal(true);
		scrollContainer.setExpandVertical(true);
		scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		formContainer.addListener(SWT.Resize,
				e -> scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT)));
		formContainer
				.addPaintListener(e -> scrollContainer.setMinSize(formContainer.computeSize(SWT.DEFAULT, SWT.DEFAULT)));

		// Load data
		loadData();

		// Return Control
		return container;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		// Sets the new title of the dialog
		switch (mode) {
		case CREATE:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_PAGENAME_ADD));
			break;
		case UPDATE:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_PAGENAME_EDIT));
			break;
		case VIEW:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_PAGENAME_VIEW));
			break;
		default:
			break;
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		String okButtonName = (buttonName != null && !buttonName.isEmpty()) ? buttonName : IDialogConstants.OK_LABEL;
		createButton(parent, IDialogConstants.OK_ID, okButtonName, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Render Non Editable content
	 * 
	 * @param parent the parent composite
	 */
	private void renderNonEditableContent(Composite parent) {

		// keep viewers in a map
		parameterViewers = new HashMap<>();

		// Get model
		Model model = getViewManager().getCache().getModel();

		// Parameter columns
		if (model != null) {

			for (UncertaintyParam param : parameters) {

				// search the value for the parameter
				Optional<UncertaintyValue> uncertaintyValue = Optional.empty();
				if (uncertainty.getUncertaintyParameterList() != null) {
					uncertaintyValue = uncertainty.getUncertaintyParameterList().stream()
							.filter(u -> param.equals(u.getParameter())).findFirst();
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

	/**
	 * Render Editable content
	 * 
	 * @param parent the parent composite
	 */
	private void renderEditableContent(Composite parent) {

		// label Uncertainty Group
		Label lblUncertaintyGroup = new Label(parent, SWT.NONE);
		lblUncertaintyGroup.setText(RscTools.getString(RscConst.MSG_LBL_REQUIRED,
				RscTools.getString(RscConst.MSG_DIALOG_UNCERTAINTY_GROUP)));

		// combo Uncertainty Group
		cbxUncertaintyGroup = FormFactory.createSelectWidget(getViewManager().getRscMgr(), parent, true, null, null);
		cbxUncertaintyGroup.setEnabled(mode.equals(DialogMode.UPDATE));

		Model model = getViewManager().getCache().getModel();
		if (model != null) {
			cbxUncertaintyGroup.setSelectValues(getViewManager().getAppManager()
					.getService(IUncertaintyApplication.class).getUncertaintyGroupByModel(model));
		}
		cbxUncertaintyGroup.addKeyListener(new ComboDropDownKeyListener());

		// keep viewers in a map
		parameterViewers = new HashMap<>();

		for (UncertaintyParam param : parameters) {
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

	/**
	 * Load Uncertainty data
	 */
	private void loadData() {

		// set uncertainty group selected in parent view
		if (groupSelected != null && cbxUncertaintyGroup != null) {
			cbxUncertaintyGroup.setValue(groupSelected);
		}

		if (uncertainty != null && uncertainty.getUncertaintyParameterList() != null) {

			for (UncertaintyParam parameter : parameters) {

				// Get viewer
				GenericValueFieldWidget<UncertaintyParam> viewer = parameterViewers.get(parameter);

				if (viewer != null) {
					UncertaintyValue uncertaintyParameterToUpdate = uncertainty
							.getUncertaintyParameterList().stream().filter(uncertaintyParameterTemp -> viewer
									.getParameter().equals(uncertaintyParameterTemp.getParameter()))
							.findFirst().orElse(null);

					viewer.setValue(uncertaintyParameterToUpdate);
				}
			}
		}
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
		if (mode == DialogMode.VIEW) {
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

		// Save - Validate form and hydrate the uncertainty
		// Initialize
		boolean formValid = true;
		cbxUncertaintyGroup.clearHelper();
		parameterViewers.forEach((param, viewer) -> viewer.clearHelper());

		// Check group selection
		groupSelected = cbxUncertaintyGroup.getValue();
		if (groupSelected == null) {
			formValid = false;
			cbxUncertaintyGroup.setHelper(
					NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_DIALOG_UNCERTAINTY_GROUP)));
		}

		// Uncertainty parameter list
		List<UncertaintyValue> tempValues = getValuesInput();

		// check validity
		for (UncertaintyValue value : tempValues) {

			// validate constraints
			Notification notification = getViewManager().getAppManager().getService(IGenericParameterApplication.class)
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

		setErrorMessage(null);

		// Set Group
		uncertainty.setGroup(groupSelected);

		// Set value list
		List<UncertaintyValue> toPersistValues = new ArrayList<>();
		for (UncertaintyValue tempValue : tempValues) {

			// Initialize
			UncertaintyValue toPersistValue = null;
			UncertaintyValue existingValue = null;

			if (uncertainty.getUncertaintyParameterList() != null) {
				existingValue = uncertainty.getUncertaintyParameterList().stream()
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
		uncertainty.setUncertaintyParameterList(toPersistValues);

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

			if (uncertainty.getUncertaintyParameterList() != null) {
				genericValue = uncertainty.getUncertaintyParameterList().stream()
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

}
