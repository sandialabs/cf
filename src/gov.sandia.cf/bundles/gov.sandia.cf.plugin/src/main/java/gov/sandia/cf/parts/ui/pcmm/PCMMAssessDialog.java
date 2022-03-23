/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.GenericCFDialog;
import gov.sandia.cf.parts.listeners.ComboDropDownKeyListener;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.RichTextWidget;
import gov.sandia.cf.parts.widgets.SelectWidget;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to assess a PCMM subelement
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessDialog extends GenericCFDialog<PCMMViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAssessDialog.class);

	/**
	 * the level combo box
	 */
	private SelectWidget<PCMMLevel> cbxLevel;

	/**
	 * the comments text
	 */
	private RichTextWidget editorComments;

	/**
	 * the data
	 */
	private PCMMAssessment assessment;
	private List<PCMMLevel> levelList = null;
	private List<PCMMEvidence> evidenceList = null;
	private PCMMLevel levelRoot;

	/**
	 * Use this constructor to update @param subElement
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 * @param assessment  the assessment to create/update
	 */
	public PCMMAssessDialog(PCMMViewManager viewManager, Shell parentShell, PCMMAssessment assessment) {
		super(viewManager, parentShell);
		this.assessment = assessment;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void create() {
		super.create();
		String title = RscTools.empty();
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			title = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_TITLE);
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			title = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_SIMPLIFIED_TITLE);
		}
		setTitle(title);
		setMessage(RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_SUBTITLE), IMessageProvider.INFORMATION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// Set the new title of the dialog
		newShell.setText(RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_PAGE_NAME));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		String okButtonName = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_BTN_ASSESS);
		createButton(parent, IDialogConstants.OK_ID, okButtonName, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		logger.debug("Create Assess dialog area"); //$NON-NLS-1$

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
		gridLayout.verticalSpacing = PartsResourceConstants.DEFAULT_GRIDDATA_V_INDENT;

		// render PCMM code
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			renderPCMMSubelementCode(formContainer);
		}

		// label name
		Label lblName = new Label(formContainer, SWT.ON_TOP);
		lblName.setText(getNameLabel());
		lblName.setLayoutData(new GridData(SWT.LEFT, SWT.ON_TOP, false, false));

		// text name
		Text txtName = new Text(formContainer, SWT.LEFT | SWT.BORDER);
		GridData dataLabel = new GridData();
		dataLabel.grabExcessHorizontalSpace = true;
		dataLabel.horizontalAlignment = GridData.FILL;
		dataLabel.heightHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT * 3;
		txtName.setLayoutData(dataLabel);
		txtName.setText(getNameValue());
		txtName.setEditable(false);

		// label Level
		FormFactory.createFormLabel(formContainer,
				RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_LBL_LEVELACHIEVED));

		// combo-box Level
		cbxLevel = FormFactory.createSelectWidget(getViewManager().getRscMgr(), formContainer, true, null);
		cbxLevel.addKeyListener(new ComboDropDownKeyListener());
		cbxLevel.addSelectionChangedListener(event -> checkLevel());

		// label comments
		FormFactory.createFormLabel(formContainer,
				RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_LBL_COMMENTS));

		// Text comments - editor
		editorComments = FormFactory.createRichTextWidget(getViewManager().getRscMgr(), formContainer, true, true);
		editorComments.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkComments();
			}
		});
		editorComments.addModifyListener(e -> checkComments());

		// dialog behavior
		cbxLevel.setFocus();

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

		return container;
	}

	/**
	 * Load Decision data
	 */
	private void loadData() {

		if (this.assessment == null) {
			return;
		}

		// get evidence and level list for the assessment
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			levelList = this.assessment.getSubelement() != null ? this.assessment.getSubelement().getLevelList() : null;
			evidenceList = this.assessment.getSubelement() != null ? this.assessment.getSubelement().getEvidenceList()
					: null;
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			levelList = this.assessment.getElement() != null ? this.assessment.getElement().getLevelList() : null;
			evidenceList = this.assessment.getElement() != null ? this.assessment.getElement().getEvidenceList() : null;
		}

		// Get root level
		levelRoot = levelList != null && !levelList.isEmpty() ? levelList.get(0) : null;

		// set the level
		cbxLevel.setSelectValues(levelList);
		if (this.assessment.getLevel() != null) {
			cbxLevel.setValue(this.assessment.getLevel());
		}

		// set the comment
		editorComments.setValue(assessment.getComment() != null ? assessment.getComment() : RscTools.empty());
	}

	/**
	 * Render the PCMM subelement code
	 * 
	 * @param parent the parent composite
	 */
	private void renderPCMMSubelementCode(Composite parent) {

		// label code
		Label lblCode = new Label(parent, SWT.NONE);
		lblCode.setText(RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_LBL_CODE));

		// text code
		Text txtCode = new Text(parent, SWT.LEFT | SWT.BORDER);
		GridData dataCode = new GridData();
		dataCode.grabExcessHorizontalSpace = true;
		dataCode.horizontalAlignment = GridData.FILL;
		dataCode.heightHint = PartsResourceConstants.DIALOG_TXT_INPUT_HEIGHT;
		txtCode.setLayoutData(dataCode);
		txtCode.setText(
				this.assessment.getSubelement() != null ? this.assessment.getSubelement().getCode() : RscTools.empty());
		txtCode.setEditable(false);
	}

	/**
	 * @return the name label
	 */
	private String getNameLabel() {
		String nameLabel = RscTools.empty();
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			nameLabel = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_LBL_NAME);
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			nameLabel = RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_SIMPLIFIED_LBL_NAME);
		}
		return nameLabel;
	}

	/**
	 * @return the name value
	 */
	private String getNameValue() {
		String nameValue = RscTools.empty();
		if (PCMMMode.DEFAULT.equals(getViewManager().getPCMMConfiguration().getMode())) {
			nameValue = assessment.getSubelement() != null ? assessment.getSubelement().getName() : RscTools.empty();
		} else if (PCMMMode.SIMPLIFIED.equals(getViewManager().getPCMMConfiguration().getMode())) {
			nameValue = assessment.getElement() != null ? assessment.getElement().getName() : RscTools.empty();
		}
		return nameValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void okPressed() {

		// Initialize
		boolean formValid = true;
		cbxLevel.clearHelper();
		editorComments.clearHelper();

		// check level field
		formValid &= checkLevel();

		// check editor field
		formValid &= checkComments();

		if (!formValid) {
			return;
		}

		// Get selected level
		PCMMLevel level = cbxLevel.getValue();

		// set fields
		this.assessment.setLevel(level);
		this.assessment.setComment(editorComments.getValue());

		super.okPressed();
	}

	/**
	 * Check the level
	 */
	private boolean checkLevel() {

		// Initialize
		boolean valid = true;
		cbxLevel.clearHelper();
		Set<String> errorMessage = new HashSet<>();

		// check if there is a level available
		if (levelRoot == null) {
			valid = false;
			errorMessage.add(RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_NO_LEVELS));
		}

		// check if the value is fulfilled
		if (levelRoot != null && null == cbxLevel.getValue()) {
			valid = false;
			errorMessage.add(RscTools.getString(RscConst.ERR_PCMMASSESS_DIALOG_ASSESS_LEVELACHIEVED_MANDATORY));
		}

		// Check no evidence - first level only
		if (evidenceList == null || evidenceList.isEmpty()) {

			// Get selected level
			PCMMLevel level = cbxLevel.getValue();

			if (levelRoot != null && level != null && !levelRoot.getId().equals(level.getId())) {
				valid = false;
				errorMessage.add(
						RscTools.getString(RscConst.MSG_PCMMASSESS_DIALOG_ASSESS_NO_EVIDENCE, levelRoot.getName()));
			}
		}

		if (!valid) {
			cbxLevel.setHelper(NotificationFactory.getNewError(errorMessage));
		}

		return valid;
	}

	/**
	 * Check the comments
	 */
	private boolean checkComments() {

		// Initialize
		boolean valid = true;
		editorComments.clearHelper();

		// defines if the editor comment field is valid or not
		if (null == editorComments.getValue() || RscTools.empty().equals(editorComments.getValue())) {
			valid = false;
			editorComments.setHelper(NotificationFactory
					.getNewError(RscTools.getString(RscConst.ERR_PCMMASSESS_DIALOG_ASSESS_COMMENT_REQUIRED)));
		}

		return valid;
	}

	/**
	 * @return the assessment to create/update
	 */
	public PCMMAssessment openDialog() {
		if (open() == Window.OK) {
			return this.assessment;
		}

		return null;
	}

}
