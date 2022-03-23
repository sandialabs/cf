/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.dialogs;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.PIRTTreeAdequacyColumnType;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.tools.FontTools;
import gov.sandia.cf.parts.ui.pirt.PIRTViewManager;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.RichTextWidget;
import gov.sandia.cf.parts.widgets.SelectWidget;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add a new phenomenon
 * 
 * @author Didier Verstraete
 *
 */
/**
 * @author Didier Verstraete
 *
 */
/**
 * @author Didier Verstraete
 *
 */
public class PIRTPhenomenonDialog extends GenericCFSmallDialog<PIRTViewManager> {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PIRTPhenomenonDialog.class);

	/**
	 * column name property to set criterion name
	 */
	private static final String COLUMN_NAME_PROPERTY = "COLUMN_NAME"; //$NON-NLS-1$

	/**
	 * the phenomenon to create
	 */
	private Phenomenon phenomenon;
	/**
	 * the list of existing groups
	 */
	private List<PhenomenonGroup> existingGroups;
	/**
	 * the phenomenon group combobox
	 */
	private SelectWidget<PhenomenonGroup> cbxPhenomenonGroup;
	/**
	 * the label text
	 */
	private TextWidget txtLabel;

	/**
	 * the importance combo
	 */
	private SelectWidget<PIRTLevelImportance> cbxImportance;
	/**
	 * the adequacy viewers (depends of column type "Text", "Levels"
	 * {@link PIRTTreeAdequacyColumnType} class)
	 */
	private List<Composite> adequacyViewers;

	/**
	 * group selected to autofill cbxPhenomenonGroup
	 */
	private PhenomenonGroup firstGroupSelected;

	/**
	 * the button name
	 */
	private String buttonName;

	/**
	 * The constructor
	 * 
	 * @param viewManager        the view manager
	 * @param parentShell        the parent shell
	 * @param mode               the dialog mode
	 * @param existingGroups     the existing groups
	 * @param phenomenon         the phenomenon to update
	 * @param firstGroupSelected the phenomenon group to select by default
	 */
	public PIRTPhenomenonDialog(PIRTViewManager viewManager, Shell parentShell, List<PhenomenonGroup> existingGroups,
			Phenomenon phenomenon, PhenomenonGroup firstGroupSelected, ViewMode mode) {
		super(viewManager, parentShell);
		this.existingGroups = existingGroups;
		if (firstGroupSelected != null) {
			this.firstGroupSelected = firstGroupSelected;
		} else {
			this.firstGroupSelected = (phenomenon != null) ? phenomenon.getPhenomenonGroup() : null;
		}

		switch (mode) {
		case CREATE:
			this.phenomenon = new Phenomenon();
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = ViewMode.CREATE;
			break;

		case UPDATE:
			this.phenomenon = phenomenon;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_UPDATE);
			this.mode = ViewMode.UPDATE;
			break;

		case VIEW:
			this.phenomenon = phenomenon;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CLOSE);
			this.mode = ViewMode.VIEW;
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
		setTitle(RscTools.getString(RscConst.MSG_DIALOG_PHEN_TITLE));
		if (mode != ViewMode.VIEW) {
			setMessage(RscTools.getString(RscConst.MSG_DIALOG_PHEN_MESSAGE), IMessageProvider.INFORMATION);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);

		// vertical scroll container
		ScrolledComposite scrolledComposite = new ScrolledComposite(container, SWT.V_SCROLL);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
		scData.widthHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_X;
		scData.heightHint = PartsResourceConstants.DESCRIPTIVE_DIALOG_SIZE_Y;
		scrolledComposite.setLayoutData(scData);
		scrolledComposite.setLayout(new GridLayout());

		// form container
		Composite formContainer = new Composite(scrolledComposite, SWT.NONE);
		formContainer.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		renderContent(formContainer);

		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setContent(formContainer);

		// autoresize sub containers
		scrolledComposite.addListener(SWT.Resize, event -> {
			int width = scrolledComposite.getClientArea().width;
			Point computeSize = formContainer.computeSize(width, SWT.DEFAULT);
			scrolledComposite.setMinSize(computeSize);
			formContainer.setSize(computeSize);
		});

		return container;
	}

	/**
	 * Render the dialog content in editable mode
	 * 
	 * @param parent
	 */
	private void renderContent(Composite parent) {

		logger.debug("Render PIRT Phenomenon dialog content"); //$NON-NLS-1$

		PIRTSpecification configuration = getViewManager().getCache().getPIRTSpecification();
		boolean editable = (mode != ViewMode.VIEW);

		// label Phenomenon Group
		FormFactory.createLabel(parent, RscTools.getString(RscConst.MSG_DIALOG_PHEN_GROUP));

		// combo Phenomenon Group
		cbxPhenomenonGroup = FormFactory.createSelectWidget(getViewManager().getRscMgr(), parent, editable, null,
				existingGroups);
		cbxPhenomenonGroup.addKeyListener(new ComboDropDownKeyListener());
		cbxPhenomenonGroup.addListener(SWT.Modify, event -> isValid());
		cbxPhenomenonGroup.setEnabled(mode.equals(ViewMode.UPDATE));

		// label Text
		FormFactory.createLabel(parent, RscTools.getString(RscConst.MSG_DIALOG_PHEN_LABEL));

		// text Text
		txtLabel = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, editable, null);
		txtLabel.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				isValid();
			}
		});

		// label Importance
		FormFactory.createLabel(parent, RscTools.getString(RscConst.MSG_DIALOG_PHEN_IMPORTANCE));

		// combobox Importance
		cbxImportance = FormFactory.createSelectWidget(getViewManager().getRscMgr(), parent, editable, null);
		cbxImportance.addKeyListener(new ComboDropDownKeyListener());

		if (configuration != null) {

			cbxImportance.setSelectValues(configuration.getLevelsListSortedByLevelDescending());

			// add generated fields according to credibility configuration specifications
			if (configuration.getColumns() != null) {

				// Create a horizontal separator
				FormFactory.createLabelHorizontalSeparator(parent);

				// label
				GridData dataAdequacyGroup = new GridData();
				dataAdequacyGroup.horizontalSpan = 2;
				dataAdequacyGroup.grabExcessHorizontalSpace = true;
				dataAdequacyGroup.horizontalAlignment = GridData.CENTER;
				Label labelAdequacy = FormFactory.createLabel(parent,
						RscTools.getString(RscConst.MSG_DIALOG_PHEN_ADEQUACY), dataAdequacyGroup);
				FontTools.setBoldFont(getViewManager().getRscMgr(), labelAdequacy);
				FontTools.setImportantTextFont(getViewManager().getRscMgr(), labelAdequacy);

				// keep labels and viewers in a list
				adequacyViewers = new ArrayList<>();

				// adequacy columns
				for (PIRTAdequacyColumn column : configuration.getColumns().stream()
						.filter(col -> PIRTTreeAdequacyColumnType.LEVELS.getType().equals(col.getType()))
						.collect(Collectors.toList())) {

					// label
					FormFactory.createLabel(parent, column.getName());

					// combobox level
					SelectWidget<PIRTLevelImportance> combobox = FormFactory
							.createSelectWidget(getViewManager().getRscMgr(), parent, editable, null);
					combobox.setSelectValues(configuration.getLevelsListSortedByLevelDescending());
					combobox.addKeyListener(new ComboDropDownKeyListener());
					combobox.setData(COLUMN_NAME_PROPERTY, column.getName());
					adequacyViewers.add(combobox);
				}

				// Create a horizontal separator
				FormFactory.createLabelHorizontalSeparator(parent);

				// other text columns
				for (PIRTAdequacyColumn column : configuration.getColumns().stream()
						.filter(col -> !PIRTTreeAdequacyColumnType.LEVELS.getType().equals(col.getType()))
						.collect(Collectors.toList())) {
					if (PIRTTreeAdequacyColumnType.TEXT.getType().equals(column.getType())) {

						// label
						Label createLabel = FormFactory.createLabel(parent, column.getName());
						GridData dataLabel = new GridData();
						dataLabel.grabExcessVerticalSpace = true;
						dataLabel.verticalAlignment = GridData.FILL;
						createLabel.setLayoutData(dataLabel);

						// text
						TextWidget text = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, editable,
								null, SWT.LEFT | SWT.WRAP | SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
						GridData dataComments = new GridData();
						dataComments.heightHint = 100;
						dataComments.grabExcessHorizontalSpace = true;
						dataComments.horizontalAlignment = GridData.FILL;
						dataComments.grabExcessVerticalSpace = true;
						dataComments.verticalAlignment = GridData.FILL;
						text.setLayoutData(dataComments);
						text.setData(COLUMN_NAME_PROPERTY, column.getName());
						adequacyViewers.add(text);
					} else if (PIRTTreeAdequacyColumnType.RICH_TEXT.getType().equals(column.getType())) {
						// label
						Label label = new Label(parent, SWT.NONE);
						label.setText(column.getName() + RscTools.COLON);

						// text
						RichTextWidget text = FormFactory.createRichTextWidget(getViewManager().getRscMgr(), parent,
								true, editable);
						text.setData(COLUMN_NAME_PROPERTY, column.getName());
						adequacyViewers.add(text);
					}
				}
			}
		}

		/**
		 * Load preset data
		 */
		loadPresetData();

		// dialog behavior
		cbxPhenomenonGroup.setFocus();
	}

	/**
	 * Load preset data from phenomenon
	 */
	@SuppressWarnings("unchecked")
	private void loadPresetData() {

		PIRTSpecification configuration = getViewManager().getCache().getPIRTSpecification();

		// set phenomenon group selected in parent view
		if (firstGroupSelected != null) {
			cbxPhenomenonGroup.setValue(firstGroupSelected);
		}

		if (phenomenon == null) {
			return;
		}

		// description
		txtLabel.setValue(phenomenon.getName() != null ? phenomenon.getName() : RscTools.empty());

		// importance
		cbxImportance.setValue(configuration.getLevels().get(phenomenon.getImportance()));

		// stop if there is no criterion
		if (phenomenon.getCriterionList() == null) {
			return;
		}

		// criterion fields
		for (Composite viewer : adequacyViewers.stream().filter(v -> v.getData(COLUMN_NAME_PROPERTY) instanceof String)
				.collect(Collectors.toList())) {

			String columnName = (String) viewer.getData(COLUMN_NAME_PROPERTY);
			Criterion criterionToUpdate = phenomenon.getCriterionList().stream()
					.filter(criterionTemp -> columnName.equals(criterionTemp.getName())).findAny().orElse(null);

			if (criterionToUpdate == null) {
				continue;
			}

			if (viewer instanceof SelectWidget) {
				PIRTLevelImportance pirtImportanceLevel = configuration.getLevels().get(criterionToUpdate.getValue());
				if (pirtImportanceLevel != null) {
					((SelectWidget<PIRTLevelImportance>) viewer).setValue(pirtImportanceLevel);
				}
			} else if (viewer instanceof TextWidget) {
				((TextWidget) viewer).setValue(criterionToUpdate.getValue());
			} else if (viewer instanceof RichTextWidget) {
				((RichTextWidget) viewer).setValue(criterionToUpdate.getValue());
			}
		}
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
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_PHEN_PAGENAME_ADD));
			break;
		case UPDATE:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_PHEN_PAGENAME_EDIT));
			break;
		case VIEW:
			newShell.setText(RscTools.getString(RscConst.MSG_DIALOG_PHEN_PAGENAME_VIEW));
			break;
		default:
			break;
		}
	}

	/**
	 * @return a string if no error is detected, otherwise the error message
	 */
	private boolean isValid() {

		// clear message
		cbxPhenomenonGroup.clearHelper();
		txtLabel.clearHelper();

		// defines if form is valid or not
		boolean valid = true;

		PhenomenonGroup groupSelected = cbxPhenomenonGroup.getValue();
		if (groupSelected == null) {
			cbxPhenomenonGroup
					.setHelper(NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_DIALOG_PHEN_GROUP)));
			valid = false;
		}

		// test Label
		if (txtLabel.getValue() == null || txtLabel.getValue().isEmpty()) {
			txtLabel.setHelper(NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_DIALOG_PHEN_LABEL)));
			valid = false;
		}

		return valid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		if (mode == ViewMode.VIEW) {
			super.okPressed();
			return;
		}

		// tests form validation
		if (!isValid()) {
			return;
		}

		// Set data
		// Set Name
		phenomenon.setName(txtLabel.getValue());

		// Set PhenomenonGroup
		PhenomenonGroup groupSelected = cbxPhenomenonGroup.getValue();
		phenomenon.setPhenomenonGroup(groupSelected);

		// Set importance
		PIRTLevelImportance importance = cbxImportance.getValue();
		if (importance != null) {
			phenomenon.setImportance(importance.getIdLabel());
		}

		// Set Criterion list
		phenomenon.setCriterionList(getCriteria());

		// Call super
		super.okPressed();
	}

	/**
	 * @return the list of criteria correctly fulfilled for creation/edition. If
	 *         there is no criteria, return an empty list.
	 */
	private List<Criterion> getCriteria() {

		List<Criterion> criteria = new ArrayList<>();

		if (adequacyViewers == null) {
			return criteria;
		}

		for (Composite viewer : adequacyViewers) {

			// get the column name, if null go to the next one
			String columnName = (String) viewer.getData(COLUMN_NAME_PROPERTY);
			if (columnName == null) {
				continue;
			}

			Criterion criterionModel = new Criterion();

			// get column
			Criterion criterionSearched = getCriterionByColumnName(columnName);
			if (criterionSearched == null) {
				criterionModel.setPhenomenon(phenomenon);
				criterionModel.setName(columnName);
			} else {
				criterionModel = criterionSearched;
			}

			// get viewer values to update criterion fields
			if (viewer instanceof SelectWidget) {
				@SuppressWarnings("unchecked")
				PIRTLevelImportance criterion = ((SelectWidget<PIRTLevelImportance>) viewer).getValue();
				criterionModel.setType(PIRTTreeAdequacyColumnType.LEVELS.getType());
				criterionModel.setValue(criterion != null ? criterion.getIdLabel() : RscTools.empty());
			} else if (viewer instanceof TextWidget) {
				criterionModel.setType(PIRTTreeAdequacyColumnType.TEXT.getType());
				criterionModel.setValue(((TextWidget) viewer).getValue());
			} else if (viewer instanceof RichTextWidget) {
				criterionModel.setType(PIRTTreeAdequacyColumnType.RICH_TEXT.getType());
				criterionModel.setValue(((RichTextWidget) viewer).getValue());
			}

			criteria.add(criterionModel);
		}

		return criteria;
	}

	/**
	 * test if criterion already exists or not.
	 * 
	 * @param columnName the criterion column name
	 * @return the criterion associated to the column name if found, otherwise null
	 */
	private Criterion getCriterionByColumnName(String columnName) {
		Criterion criterionSearched = null;
		if (columnName != null && phenomenon != null && phenomenon.getCriterionList() != null) {
			criterionSearched = phenomenon.getCriterionList().stream()
					.filter(criterionTemp -> columnName.equals(criterionTemp.getName())).findAny().orElse(null);
		}
		return criterionSearched;
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
	 * @return the phenomenon to create
	 */
	public Phenomenon openDialog() {
		if (open() == Window.OK) {
			return phenomenon;
		}

		return null;
	}

}
