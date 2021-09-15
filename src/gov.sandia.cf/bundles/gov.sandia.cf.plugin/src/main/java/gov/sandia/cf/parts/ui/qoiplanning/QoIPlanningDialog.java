/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.qoiplanning;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IGenericParameterApplication;
import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.FormFieldWidget;
import gov.sandia.cf.parts.widgets.GenericValueFieldWidget;
import gov.sandia.cf.parts.widgets.LinkWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a QoI Planning
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningDialog extends GenericCFSmallDialog<QoIPlanningViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(QoIPlanningDialog.class);

	/**
	 * the button name
	 */
	private String buttonName;

	/**
	 * The model
	 */
	private Model model;

	/**
	 * List of parameters
	 */
	private List<QoIPlanningParam> parameters;

	/**
	 * the name Text
	 */
	private FormFieldWidget<?> txtSymbol;

	/**
	 * the description Text
	 */
	private FormFieldWidget<?> editorDescription;

	/**
	 * List of viewer for each parameter
	 */
	private Map<QoIPlanningParam, GenericValueFieldWidget<QoIPlanningParam>> parameterViewers;

	/**
	 * The qoi to create/update/view
	 */
	private QuantityOfInterest qoi;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 * @param qoi         the requirement group to update (if null, put the dialog
	 *                    in create mode)
	 * @param mode        the dialog mode
	 */
	public QoIPlanningDialog(QoIPlanningViewManager viewManager, Shell parentShell, QuantityOfInterest qoi,
			DialogMode mode) {
		super(viewManager, parentShell);

		// Get model
		model = getViewManager().getCache().getModel();

		// Set mode
		if (mode == null) {
			mode = DialogMode.VIEW;
		}

		// Parameter columns
		parameters = getViewManager().getCache().getQoIPlanningSpecification().getParameters();

		// Manage view mode
		switch (mode) {
		case CREATE:
			this.qoi = new QuantityOfInterest();
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = DialogMode.CREATE;
			break;

		case UPDATE:
			this.qoi = qoi;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_UPDATE);
			this.mode = mode;
			break;

		case VIEW:
			this.qoi = qoi;
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
		setTitle(RscTools.getString(RscConst.MSG_QOIPLANNING_DIALOG_TITLE));
		if (mode != DialogMode.VIEW) {
			setMessage(RscTools.getString(RscConst.MSG_QOIPLANNING_DIALOG_DESCRIPTION), IMessageProvider.INFORMATION);
		}
	}

	/**
	 * @return the qoi created
	 */
	public QuantityOfInterest openDialog() {
		if (open() == Window.OK) {
			return qoi;
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
		switch (mode) {
		case CREATE:
			newShell.setText(RscTools.getString(RscConst.MSG_QOIPLANNING_DIALOG_PAGENAME_ADD));
			break;
		case UPDATE:
			newShell.setText(RscTools.getString(RscConst.MSG_QOIPLANNING_DIALOG_PAGENAME_EDIT));
			break;
		case VIEW:
			newShell.setText(RscTools.getString(RscConst.MSG_QOIPLANNING_DIALOG_PAGENAME_VIEW));
			break;
		default:
			break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		String okButtonName = (buttonName != null && !buttonName.isEmpty()) ? buttonName : IDialogConstants.OK_LABEL;
		createButton(parent, IDialogConstants.OK_ID, okButtonName, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		logger.debug("Create QoI Planning dialog area"); //$NON-NLS-1$

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

	/**
	 * Load QoI Planning data
	 */
	private void loadData() {

		if (qoi == null) {
			return;
		}

		// set QoI fields
		if (txtSymbol != null) {
			txtSymbol.setValue(qoi.getSymbol());
		}

		String qoiDescription = qoi.getDescription() != null ? qoi.getDescription() : RscTools.empty();
		editorDescription.setValue(qoiDescription);

		// set planning fields
		if (qoi.getQoiPlanningList() != null) {
			for (QoIPlanningParam parameter : parameters) {

				// Get viewer
				GenericValueFieldWidget<QoIPlanningParam> viewer = parameterViewers.get(parameter);

				if (viewer != null) {
					QoIPlanningValue qoiPlanningValueToUpdate = qoi.getQoiPlanningList().stream()
							.filter(qoiValueTemp -> viewer.getParameter().equals(qoiValueTemp.getParameter()))
							.findFirst().orElse(null);

					viewer.setValue(qoiPlanningValueToUpdate);
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

		// label name
		FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_QOI_FIELD_SYMBOL)));

		// text name
		txtSymbol = FormFactory.createNonEditableFormFieldWidget(getViewManager(), parent, FormFieldType.TEXT, null,
				null);

		// label description
		FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_QOI_FIELD_DESCRIPTION)),
				SWT.ON_TOP);

		// text description - editor
		editorDescription = FormFactory.createNonEditableFormFieldWidget(getViewManager(), parent,
				FormFieldType.RICH_TEXT, null, RscTools.getString(RscConst.MSG_QOI_FIELD_DESCRIPTION));

		// Create a horizontal separator
		FormFactory.createLabelHorizontalSeparator(parent);

		// label QoI Planning
		Label labelPlanning = new Label(parent, SWT.NONE);
		labelPlanning.setText(RscTools.getString(RscConst.MSG_QOIPLANNING_DIALOG_PLANNING_SEPARATOR));
		GridData dataAdequacyGroup = new GridData();
		dataAdequacyGroup.horizontalSpan = 2;
		dataAdequacyGroup.grabExcessHorizontalSpace = true;
		dataAdequacyGroup.horizontalAlignment = GridData.CENTER;
		labelPlanning.setLayoutData(dataAdequacyGroup);
		FontTools.setBoldFont(getViewManager().getRscMgr(), labelPlanning);
		FontTools.setImportantTextFont(getViewManager().getRscMgr(), labelPlanning);

		// Initialize
		parameterViewers = new HashMap<>();

		// Parameter columns
		if (model != null) {
			for (QoIPlanningParam param : parameters) {
				GenericValueFieldWidget<QoIPlanningParam> nonEditableField = FormFactory
						.createNonEditableGenericValueWidget(getViewManager(), parent, param);

				// Add to viewer list
				parameterViewers.put(param, nonEditableField);
			}
		}
	}

	/**
	 * Render Editable content
	 * 
	 * @param parent
	 */
	private void renderEditableContent(Composite parent) {

		// label name
		FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_QOI_FIELD_SYMBOL)));

		// text name
		txtSymbol = FormFactory.createEditableFormFieldWidget(getViewManager(), parent, FormFieldType.TEXT, null, null);
		txtSymbol.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				checkSymbolUnicity();
			}
		});

		// label description
		FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_QOI_FIELD_DESCRIPTION)),
				SWT.ON_TOP);

		// text description - editor
		editorDescription = FormFactory.createEditableFormFieldWidget(getViewManager(), parent, FormFieldType.RICH_TEXT,
				null, RscTools.getString(RscConst.MSG_QOI_FIELD_DESCRIPTION));

		// Create a horizontal separator
		FormFactory.createLabelHorizontalSeparator(parent);

		// label QoI Planning
		Label labelPlanning = new Label(parent, SWT.NONE);
		labelPlanning.setText(RscTools.getString(RscConst.MSG_QOIPLANNING_DIALOG_PLANNING_SEPARATOR));
		GridData dataAdequacyGroup = new GridData();
		dataAdequacyGroup.horizontalSpan = 2;
		dataAdequacyGroup.grabExcessHorizontalSpace = true;
		dataAdequacyGroup.horizontalAlignment = GridData.CENTER;
		labelPlanning.setLayoutData(dataAdequacyGroup);
		FontTools.setBoldFont(getViewManager().getRscMgr(), labelPlanning);
		FontTools.setImportantTextFont(getViewManager().getRscMgr(), labelPlanning);

		// keep viewers in a map
		parameterViewers = new HashMap<>();

		for (QoIPlanningParam param : parameters) {

			// render field
			GenericValueFieldWidget<QoIPlanningParam> editableField = FormFactory
					.createEditableGenericValueWidget(getViewManager(), parent, param);

			// check link
			if (FormFieldType.LINK.equals(editableField.getType())) {
				editableField.addLinkChangedListener(event -> checkLink(editableField.getLinkWidget()));
			}

			// Add to viewer list
			parameterViewers.put(param, editableField);
		}

		// dialog behavior
		txtSymbol.setFocus();
	}

	/**
	 * Check the QoI symbol unicity and add a helper if needed under the txtSymbol
	 * field
	 */
	private void checkSymbolUnicity() {
		boolean isError = false;

		Notification notification = NotificationFactory.getNewWarning();

		// tests Name
		if (txtSymbol.getValue() == null || txtSymbol.getValue().isEmpty()) {
			notification.addMessage(RscTools.getString(RscConst.ERR_ADDQOI_SYMBOL_MANDATORY));
			isError = true;
		} else {

			// check if qoi name already exists
			try {
				boolean existsQoISymbol = getViewManager().getAppManager().getService(IPIRTApplication.class)
						.existsQoISymbol(new Integer[] { qoi.getId() }, txtSymbol.getValue());
				if (existsQoISymbol) {
					notification
							.addMessage(RscTools.getString(RscConst.ERR_COPYQOI_NAME_DUPLICATED, txtSymbol.getValue()));
					isError = true;
				}
			} catch (CredibilityException e) {
				logger.error("An error occured while retrieving the qoi names", e); //$NON-NLS-1$
				notification.addMessage(e.getMessage());
				isError = true;
			}
		}

		if (isError) {
			txtSymbol.setHelper(notification);
		} else {
			txtSymbol.clearHelper();
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {
		// View - Just exit
		if (mode == DialogMode.VIEW) {
			super.okPressed();
		}

		// Save - Validate form and populate the QoI
		else {
			save();
		}
	}

	/**
	 * Save QoI
	 */
	private void save() {

		// Initialize
		boolean formValid = true;
		txtSymbol.clearHelper();
		parameterViewers.forEach((param, viewer) -> viewer.clearHelper());

		// set QoI fields
		if (StringUtils.isBlank(txtSymbol.getValue())) {
			formValid = false;
			txtSymbol.setHelper(NotificationFactory.getNewError(RscTools.getString(
					RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED, RscTools.getString(RscConst.MSG_QOI_FIELD_SYMBOL))));
		}

		// QoI Planning parameter list
		List<QoIPlanningValue> tempValues = getValuesInput();

		// check validity
		for (QoIPlanningValue value : tempValues) {

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

		// set symbol
		qoi.setSymbol(txtSymbol.getValue());

		// set description
		qoi.setDescription(editorDescription.getValue());

		// Set value list
		List<QoIPlanningValue> toPersistValues = new ArrayList<>();
		for (QoIPlanningValue tempValue : tempValues) {

			// Initialize
			QoIPlanningValue toPersistValue = null;
			QoIPlanningValue existingValue = null;

			if (qoi.getQoiPlanningList() != null) {
				existingValue = qoi.getQoiPlanningList().stream()
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

			toPersistValue.setQoi(qoi);
			toPersistValue.setParameter(tempValue.getParameter());

			toPersistValues.add(toPersistValue);
		}
		qoi.setQoiPlanningList(toPersistValues);

		// Call super
		super.okPressed();
	}

	/**
	 * Fulfill the widgets input into a list of qoi planning values.
	 * 
	 * This values are detached from the database to validate the data before
	 * persisting.
	 * 
	 * @return the input values list
	 */
	private List<QoIPlanningValue> getValuesInput() {

		List<QoIPlanningValue> tempValues = new ArrayList<>();

		for (QoIPlanningParam parameter : parameters) {

			// Initialize
			QoIPlanningValue genericValue = null;

			// Get viewer
			GenericValueFieldWidget<QoIPlanningParam> viewer = parameterViewers.get(parameter);

			if (viewer == null) {
				continue;
			}

			if (qoi.getQoiPlanningList() != null) {
				genericValue = qoi.getQoiPlanningList().stream()
						.filter(up -> viewer.getParameter().equals(up.getParameter())).findFirst().orElse(null);
			}

			// Not found - create
			if (genericValue == null) {
				// Create QoIPlanningParameter
				genericValue = new QoIPlanningValue();
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
