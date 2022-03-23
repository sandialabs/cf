/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.decision;

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

import gov.sandia.cf.application.decision.IDecisionApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.services.genericparam.IGenericParameterService;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.FormFieldWidget;
import gov.sandia.cf.parts.widgets.GenericValueFieldWidget;
import gov.sandia.cf.parts.widgets.LinkWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a decision
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionDialog extends GenericCFSmallDialog<DecisionViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(DecisionDialog.class);

	/**
	 * the button name
	 */
	private String buttonName;

	/**
	 * Combo viewer
	 */
	private ComboViewer cbxParent;

	/**
	 * The model
	 */
	private Model model;

	/**
	 * List of parameters
	 */
	private List<DecisionParam> parameters;

	/**
	 * the title Text
	 */
	private FormFieldWidget<?> txtTitle;

	/**
	 * List of viewer for each parameter
	 */
	private Map<DecisionParam, GenericValueFieldWidget<DecisionParam>> parameterViewers;

	/**
	 * The decision selected
	 */
	private Decision parentSelected;

	/**
	 * The decision to create
	 */
	private Decision decision;

	/**
	 * The constructor
	 * 
	 * @param viewManager    the view manager
	 * @param parentShell    the parent shell
	 * @param decision       the decision to update (if null, put the dialog in
	 *                       create mode)
	 * @param parentSelected the decision parent selected
	 * @param mode           dialog mode
	 */
	public DecisionDialog(DecisionViewManager viewManager, Shell parentShell, Decision decision,
			Decision parentSelected, ViewMode mode) {
		super(viewManager, parentShell);

		// Get model
		model = getViewManager().getCache().getModel();

		// Set parent
		if (parentSelected != null) {
			this.parentSelected = parentSelected;
		} else {
			this.parentSelected = (decision != null) ? decision.getParent() : null;
		}

		// Set mode
		if (mode == null) {
			mode = ViewMode.VIEW;
		}

		// Parameter columns
		parameters = getViewManager().getCache().getDecisionSpecification().getParameters();

		// Manage view mode
		switch (mode) {
		case CREATE:
			this.decision = new Decision();
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = ViewMode.CREATE;
			break;

		case UPDATE:
			this.decision = decision;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_UPDATE);
			this.mode = mode;
			break;

		case VIEW:
			this.decision = decision;
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
		setTitle(RscTools.getString(RscConst.MSG_DIALOG_DECISION_TITLE));

		if (this.parentSelected == null) {
			setTitle(RscTools.getString(RscConst.MSG_DIALOG_DECISION_GROUP_TITLE));
		} else {
			setTitle(RscTools.getString(RscConst.MSG_DIALOG_DECISION_TITLE));
		}
		if (mode != ViewMode.VIEW) {
			if (this.parentSelected == null) {
				setMessage(RscTools.getString(RscConst.MSG_DIALOG_DECISION_GROUP_DESCRIPTION),
						IMessageProvider.INFORMATION);
			} else {
				setMessage(RscTools.getString(RscConst.MSG_DIALOG_DECISION_DESCRIPTION), IMessageProvider.INFORMATION);
			}
		}
	}

	/**
	 * @return the decision created
	 */
	public Decision openDialog() {
		if (open() == Window.OK) {
			return decision;
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
				title = RscTools.getString(RscConst.MSG_DIALOG_DECISION_GROUP_PAGENAME_ADD);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_DECISION_PAGENAME_ADD);
			}
			break;
		case UPDATE:
			if (this.parentSelected == null) {
				title = RscTools.getString(RscConst.MSG_DIALOG_DECISION_GROUP_PAGENAME_EDIT);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_DECISION_PAGENAME_EDIT);
			}
			break;
		case VIEW:
			if (this.parentSelected == null) {
				title = RscTools.getString(RscConst.MSG_DIALOG_DECISION_GROUP_PAGENAME_VIEW);
			} else {
				title = RscTools.getString(RscConst.MSG_DIALOG_DECISION_PAGENAME_VIEW);
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
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		logger.debug("Create Decision dialog area"); //$NON-NLS-1$

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
		if (mode == ViewMode.VIEW) {
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
	 * Load Decision data
	 */
	private void loadData() {

		if (decision == null) {
			return;
		}

		// set decision selected in parent view
		if (parentSelected != null && cbxParent != null) {
			final ISelection slParent = new StructuredSelection(parentSelected);
			cbxParent.setSelection(slParent);
		}

		// set Decision fields
		if (txtTitle != null) {
			txtTitle.setValue(decision.getTitle());
		}

		// Set generic parameters
		if (decision.getDecisionList() != null) {
			for (DecisionParam parameter : parameters) {

				// Get viewer
				GenericValueFieldWidget<DecisionParam> viewer = parameterViewers.get(parameter);

				if (viewer != null) {
					DecisionValue decisionParameterToUpdate = decision.getDecisionList().stream().filter(
							decisionParameterTemp -> viewer.getParameter().equals(decisionParameterTemp.getParameter()))
							.findFirst().orElse(null);

					viewer.setValue(decisionParameterToUpdate);
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

		// label title
		FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_DECISION_COLUMN_TITLE)));

		// text title
		txtTitle = FormFactory.createNonEditableFormFieldWidget(getViewManager(), parent, FormFieldType.TEXT, null,
				null);

		// Initialize
		parameterViewers = new HashMap<>();

		// Parameter columns
		if (model != null) {

			for (DecisionParam param : parameters) {

				// check if the parameter is for the current level
				if (getViewManager().getClientService(IGenericParameterService.class)
						.isParameterAvailableForLevel(param, getCurrentLevel())) {

					// search the value for the parameter
					Optional<DecisionValue> decisionValue = Optional.empty();
					if (decision.getDecisionList() != null) {
						decisionValue = decision.getDecisionList().stream().filter(d -> param.equals(d.getParameter()))
								.findFirst();
					}

					if (decisionValue.isPresent()) {
						GenericValueFieldWidget<DecisionParam> nonEditableField = FormFactory
								.createNonEditableGenericValueWidget(getViewManager(), parent, param);

						// Add to viewer list
						parameterViewers.put(param, nonEditableField);
					}
				}
			}
		}

		// Check not null
		if (cbxParent != null) {
			List<Decision> inputs = new ArrayList<>();
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

		// label name
		FormFactory.createLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_DECISION_COLUMN_TITLE)));

		// text name
		txtTitle = FormFactory.createEditableFormFieldWidget(getViewManager(), parent, FormFieldType.TEXT, null, null);
		txtTitle.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				checkTitleUnicity();
			}
		});

		// keep viewers in a map
		parameterViewers = new HashMap<>();

		for (DecisionParam param : parameters) {
			if (getViewManager().getClientService(IGenericParameterService.class)
					.isParameterAvailableForLevel(param, getCurrentLevel())) {

				// render field
				GenericValueFieldWidget<DecisionParam> editableField = FormFactory
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
		txtTitle.setFocus();
	}

	/**
	 * Render the decision parameter parent combobox
	 * 
	 * @param parent the parent composite
	 */
	private void renderParentComboBox(Composite parent) {

		// label decision
		Label lblParent = new Label(parent, SWT.NONE);
		lblParent.setText(RscTools.getString(RscConst.MSG_DIALOG_DECISION_PARENT));

		// combo decision
		cbxParent = new ComboViewer(parent, SWT.BORDER | SWT.READ_ONLY);
		GridData dataParent = new GridData();
		dataParent.grabExcessHorizontalSpace = true;
		dataParent.horizontalAlignment = GridData.FILL;
		cbxParent.getCombo().setLayoutData(dataParent);
		cbxParent.setContentProvider(new ArrayContentProvider());
		cbxParent.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((Decision) element).getFullGeneratedId();
			}
		});

		// Initialize input list
		List<Decision> inputs = new ArrayList<>();

		// Get input list tree
		if (model != null) {

			List<Decision> roots = getViewManager().getAppManager().getService(IDecisionApplication.class)
					.getDecisionRootByModel(model);

			if (roots != null) {

				// compute decisions
				List<Decision> decisions = roots.stream().map(root -> root.getChildrenTree(true)).flatMap(List::stream)
						.collect(Collectors.toList());

				// search for the current decision
				for (Decision child : decisions) {
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
	 * @return the current decision level
	 */
	private int getCurrentLevel() {
		int parentLevel = parentSelected != null ? parentSelected.getLevel() : -1;
		return decision.getId() != null ? decision.getLevel() : parentLevel + 1;
	}

	/**
	 * Check the decision title unicity and add a helper if needed under the
	 * txtTitle field
	 */
	private void checkTitleUnicity() {
		boolean isError = false;

		Notification notification = NotificationFactory.getNewWarning();

		// tests Name
		if (txtTitle.getValue() == null || txtTitle.getValue().isEmpty()) {
			notification.addMessage(RscTools.getString(RscConst.ERR_ADDDECISION_TITLE_MANDATORY));
			isError = true;
		} else {

			// check if decision title already exists
			try {
				boolean existsDecisionTitle = getViewManager().getAppManager().getService(IDecisionApplication.class)
						.existsDecisionTitle(new Integer[] { decision.getId() }, txtTitle.getValue());
				if (existsDecisionTitle) {
					notification.addMessage(
							RscTools.getString(RscConst.ERR_DECISION_TITLE_DUPLICATED, txtTitle.getValue()));
					isError = true;
				}
			} catch (CredibilityException e) {
				logger.error("An error occured while retrieving the qoi names", e); //$NON-NLS-1$
				notification.addMessage(e.getMessage());
				isError = true;
			}
		}

		if (isError) {
			txtTitle.setHelper(notification);
		} else {
			txtTitle.clearHelper();
		}

		// change ok button
		setEnableOkButton(!isError);
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

		// Save - Validate form and populate the decision
		else {
			save();
		}
	}

	/**
	 * Save decision
	 */
	private void save() {

		// retrieve parent selected
		setParentSelection();

		// Initialize
		boolean formValid = true;
		txtTitle.clearHelper();
		parameterViewers.forEach((param, viewer) -> viewer.clearHelper());

		// check title
		if (StringUtils.isBlank(txtTitle.getValue())) {
			formValid = false;
			txtTitle.setHelper(
					NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED,
							RscTools.getString(RscConst.MSG_DECISION_COLUMN_TITLE))));
		}

		// decision parameter list
		List<DecisionValue> tempValues = getValuesInput();

		// check validity
		for (DecisionValue value : tempValues) {

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

		// Set title
		decision.setTitle(txtTitle.getValue());

		// Set Parent
		decision.setParent(parentSelected);

		// Set value list
		List<DecisionValue> toPersistValues = new ArrayList<>();
		for (DecisionValue tempValue : tempValues) {

			// Initialize
			DecisionValue toPersistValue = null;
			DecisionValue existingValue = null;

			if (decision.getDecisionList() != null) {
				existingValue = decision.getDecisionList().stream()
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

			toPersistValue.setDecision(decision);
			toPersistValue.setParameter(tempValue.getParameter());

			toPersistValues.add(toPersistValue);
		}
		decision.setDecisionList(toPersistValues);

		// Call super
		super.okPressed();
	}

	/**
	 * Fulfill the widgets input into a list of decision values.
	 * 
	 * This values are detached from the database to validate the data before
	 * persisting.
	 * 
	 * @return the input values list
	 */
	private List<DecisionValue> getValuesInput() {

		List<DecisionValue> tempValues = new ArrayList<>();

		for (DecisionParam parameter : parameters) {

			// Initialize
			DecisionValue genericValue = null;

			// Get viewer
			GenericValueFieldWidget<DecisionParam> viewer = parameterViewers.get(parameter);

			if (viewer == null) {
				continue;
			}

			if (decision.getDecisionList() != null) {
				genericValue = decision.getDecisionList().stream()
						.filter(up -> viewer.getParameter().equals(up.getParameter())).findFirst().orElse(null);
			}

			// Not found - create
			if (genericValue == null) {
				// Create DecisionParameter
				genericValue = new DecisionValue();
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
				parentSelected = (Decision) structuredSelection.getFirstElement();
				if (parentSelected.getId() == null) {
					parentSelected = null;
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

}
