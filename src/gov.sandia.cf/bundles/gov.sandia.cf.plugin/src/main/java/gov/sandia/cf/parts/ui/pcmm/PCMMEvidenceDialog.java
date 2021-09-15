/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.dialogs.DialogMode;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.LinkWidget;
import gov.sandia.cf.parts.widgets.RichTextWidget;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add/edit/view PCMM evidence
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceDialog extends GenericCFSmallDialog<PCMMViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMEvidenceDialog.class);

	/**
	 * the button name
	 */
	private String buttonName;

	/**
	 * Evidence link widget
	 */
	private LinkWidget evidenceLink;

	/**
	 * Section Text
	 */
	private TextWidget sectionText;

	/**
	 * Input description
	 */
	private RichTextWidget editorDescription;

	/**
	 * The evidence to create/edit
	 */
	private PCMMEvidence evidence;

	/**
	 * Element
	 */
	private IAssessable item;

	private Button fileChangedButton;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 * @param evidence    the evidence to update (if null, put the dialog in create
	 *                    mode)
	 * @param item        the assessable associated
	 * @param mode        the dialog mode
	 */
	public PCMMEvidenceDialog(PCMMViewManager viewManager, Shell parentShell, PCMMEvidence evidence, IAssessable item,
			DialogMode mode) {
		super(viewManager, parentShell);

		// Set mode
		if (mode == null) {
			mode = DialogMode.VIEW;
		}

		// Manage view mode
		switch (mode) {
		case CREATE:
			if (evidence == null) {
				this.evidence = new PCMMEvidence();
			} else {
				this.evidence = evidence;
			}
			this.item = item;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_CREATE);
			this.mode = DialogMode.CREATE;
			break;

		case UPDATE:
			this.evidence = evidence;
			this.item = item;
			this.buttonName = RscTools.getString(RscConst.MSG_BTN_UPDATE);
			this.mode = mode;
			break;

		case VIEW:
			this.evidence = evidence;
			this.item = item;
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
		setTitle(RscTools.getString(RscConst.MSG_DIALOG_PCMMEVIDENCE_TITLE));
		if (mode != DialogMode.VIEW) {
			setMessage(RscTools.getString(RscConst.MSG_DIALOG_PCMMEVIDENCE_DESCRIPTION), IMessageProvider.INFORMATION);
		}
	}

	/**
	 * @return the evidence created
	 */
	public PCMMEvidence openDialog() {
		if (open() == Window.OK) {
			return evidence;
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
			newShell.setText(RscTools.getString(RscConst.MSG_DLG_ADDEVID_TITLE));
			break;
		case UPDATE:
			newShell.setText(RscTools.getString(RscConst.MSG_DLG_EDITEVID_TITLE));
			break;
		case VIEW:
			newShell.setText(RscTools.getString(RscConst.MSG_DLG_VIEWEVID_TITLE));
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

		logger.debug("Create Evidence dialog area"); //$NON-NLS-1$

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
		if (DialogMode.VIEW.equals(mode)) {
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
	 * Load data
	 */
	private void loadData() {

		if (evidence == null) {
			return;
		}

		// set evidence
		evidenceLink.setValue(evidence.getValue());

		// set section
		sectionText.setValue(evidence.getSection());

		// set description
		editorDescription.setValue(evidence.getDescription());

		// check new values
		if (!DialogMode.CREATE.equals(mode) || evidence.getValue() != null) {
			checkEvidence();
		}
	}

	/**
	 * Render Non Editable content
	 * 
	 * @param parent
	 */
	private void renderNonEditableContent(Composite parent) {

		// evidence link
		FormFactory.createFormLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_DLG_ADDEVID_LBL_EVID)));
		evidenceLink = FormFactory.createLinkWidget(parent, getViewManager(), null, false);

		// section text
		FormFactory.createFormLabel(parent, RscTools.getString(RscConst.MSG_DLG_ADDEVID_LBL_SECTION));
		sectionText = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, false, null);

		// editor description
		FormFactory.createFormLabel(parent, RscTools.getString(RscConst.MSG_DLG_ADDEVID_LBL_DESC));
		editorDescription = FormFactory.createRichTextWidget(getViewManager().getRscMgr(), parent, true, false);
	}

	/**
	 * Render Editable content
	 * 
	 * @param parent
	 */
	private void renderEditableContent(Composite parent) {

		// evidence link
		FormFactory.createFormLabel(parent,
				RscTools.getString(RscConst.MSG_LBL_REQUIRED, RscTools.getString(RscConst.MSG_DLG_ADDEVID_LBL_EVID)));
		evidenceLink = FormFactory.createLinkWidget(parent, getViewManager(), null, DialogMode.CREATE.equals(mode));
		evidenceLink.addChangedListener(e -> checkEvidence());

		// render the file changed checkbox
		if (DialogMode.UPDATE.equals(mode)
				&& getViewManager().getAppManager().getService(IPCMMApplication.class).evidenceChanged(evidence)) {
			renderFileChanged(parent);
		}

		// section text
		FormFactory.createFormLabel(parent, RscTools.getString(RscConst.MSG_DLG_ADDEVID_LBL_SECTION));
		sectionText = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, true, null);
		sectionText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				checkEvidence();
			}
		});

		// editor description
		FormFactory.createFormLabel(parent, RscTools.getString(RscConst.MSG_DLG_ADDEVID_LBL_DESC));
		editorDescription = FormFactory.createRichTextWidget(getViewManager().getRscMgr(), parent, true, true);

		// dialog behavior
		evidenceLink.setFocus();
	}

	/**
	 * Render file changed checbox
	 * 
	 * @param parent
	 */
	private void renderFileChanged(Composite parent) {

		// dummy label
		FormFactory.createFormLabel(parent, RscTools.empty());

		// checkbox
		fileChangedButton = new Button(parent, SWT.CHECK);
		fileChangedButton.setText(RscTools.getString(RscConst.MSG_DIALOG_PCMMEVIDENCE_LBL_REMOVEFILECHANGED));
		fileChangedButton.setSelection(true);
		fileChangedButton.setLayoutData(new GridData());
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

		// Save - Validate form and populate the evidence
		else {
			save();
		}
	}

	/**
	 * Save evidence
	 */
	private void save() {

		// Initialize
		boolean formValid = true;
		evidenceLink.clearHelper();

		// check evidence path
		formValid &= evidenceLink.isValid();
		evidenceLink.validateLink();

		// check evidence
		formValid &= checkEvidence();

		// If form not valid
		if (!formValid) {
			return;
		}

		// Create mode
		if (DialogMode.CREATE.equals(mode)) {

			// name
			String name = evidenceLink.getValue();
			if (FormFieldType.LINK_FILE.equals(evidenceLink.getLinkTypeSelected())) {
				name = evidenceLink.getFilename();
			}
			evidence.setName(name);

			// path
			if (FormFieldType.LINK_FILE.equals(evidenceLink.getLinkTypeSelected())) {
				evidence.setFilePath(evidenceLink.getValue());
			} else if (FormFieldType.LINK_URL.equals(evidenceLink.getLinkTypeSelected())) {
				evidence.setURL(evidenceLink.getValue());
			}
		}

		// description
		evidence.setDescription(editorDescription.getValue());

		// section
		evidence.setSection(sectionText.getValue());

		// update the file date for evidence for which file changed notification has
		// been removed
		if (fileChangedButton != null && fileChangedButton.getSelection()) {
			evidence.setDateFile(FileTools.getLastUpdatedDate(new Path(evidence.getPath())));
		}

		// Call super
		super.okPressed();
	}

	/**
	 * Check evidence
	 * 
	 * @return true if the selected evidence is valid (is not associated with
	 *         another evidence), otherwise false
	 */
	private boolean checkEvidence() {

		// Initialize
		boolean isValid = true;
		evidenceLink.clearHelper();

		// Check if evidence value is valid
		isValid &= evidenceLink.isValid();

		// Check if evidence is not empty
		if (StringUtils.isBlank(evidenceLink.getValue())) {
			isValid = false;
			evidenceLink.setHelper(
					NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED,
							RscTools.getString(RscConst.MSG_PCMMEVID_ITEM_TITLE))));
		}
		evidenceLink.validateLink();

		// Check if evidence with the same path were found
		if (isValid) {
			isValid &= checkEvidenceNotification();
		}

		return isValid;
	}

	/**
	 * Chekc evidence duplicated
	 * 
	 * @return true if the evidence duplicated is in error, otherwise false
	 */
	private boolean checkEvidenceNotification() {

		boolean isError = false;

		PCMMEvidence copy = evidence.copy();
		copy.setValue(evidenceLink.getGSONValue());
		copy.setSection(sectionText.getValue());
		if (item instanceof PCMMElement) {
			copy.setElement((PCMMElement) item);
		} else if (item instanceof PCMMSubelement) {
			copy.setSubelement((PCMMSubelement) item);
		}
		Map<NotificationType, List<String>> duplicatedEvidenceNotification = getViewManager().getAppManager()
				.getService(IPCMMApplication.class).getEvidenceNotifications(copy, evidence.getId());

		// Check if evidence with the same path were found
		if (duplicatedEvidenceNotification != null && !duplicatedEvidenceNotification.isEmpty()) {

			boolean hasNotification = false;
			Set<String> setMessage = new HashSet<>();
			for (Entry<NotificationType, List<String>> notification : duplicatedEvidenceNotification.entrySet()) {
				if (notification.getValue() != null && !notification.getValue().isEmpty()) {
					setMessage.addAll(notification.getValue().stream()
							.map(gov.sandia.cf.tools.StringTools::htmlToStringText).collect(Collectors.toSet()));
					hasNotification |= true;
					isError |= NotificationType.ERROR.equals(notification.getKey());
				}
			}

			// Set notification
			if (isError) {
				evidenceLink.setHelper(NotificationFactory.getNewError(setMessage));
			} else if (hasNotification) {
				evidenceLink.setHelper(NotificationFactory.getNewWarning(setMessage));
			}
		}

		return !isError;
	}

}
