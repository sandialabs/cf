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
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.NotificationType;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.parts.constants.ViewMode;
import gov.sandia.cf.parts.dialogs.GenericCFScrolledDialog;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.LinkWidget;
import gov.sandia.cf.parts.widgets.RichTextWidget;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Dialog to add/edit/view PCMM evidence
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceDialog extends GenericCFScrolledDialog<PCMMViewManager> {

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
	 * Caption Text
	 */
	private TextWidget imageCaptionText;

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
			ViewMode mode) {
		super(viewManager, parentShell);

		// Set mode
		if (mode == null) {
			mode = ViewMode.VIEW;
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
			this.mode = ViewMode.CREATE;
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
		if (mode != ViewMode.VIEW) {
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
		if (!ViewMode.VIEW.equals(this.mode)) {
			createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Composite createDialogScrolledContent(Composite parent) {

		logger.debug("Create Evidence dialog area"); //$NON-NLS-1$

		int evidenceDialogWidth = 1200;

		Composite content = createDefaultDialogScrolledContent(parent);
		GridData gridData = (GridData) content.getLayoutData();
		gridData.widthHint = evidenceDialogWidth;

		// Select content type
		if (ViewMode.VIEW.equals(mode)) {
			renderNonEditableContent(content);
		} else {
			renderEditableContent(content);
		}

		return content;
	}

	/** {@inheritDoc} */
	@Override
	protected void loadDataAfterCreation() {

		if (evidence == null) {
			return;
		}

		// set evidence
		// if evidence value is null or empty, set last file path preference
		if (evidence.getValue() == null || StringUtils.isBlank(evidence.getValue())) {
			String lastFilePath = PrefTools.getPreference(PrefTools.PCMM_EVIDENCE_FILE_LAST_PATH_KEY);
			if (!StringUtils.isBlank(lastFilePath)) {
				evidenceLink.setFileDefaultBrowserValue(lastFilePath);
			}
		} else {
			// else set evidence value
			evidenceLink.setValue(evidence.getValue());
		}

		// set section
		sectionText.setValue(evidence.getSection());

		// set section
		if (imageCaptionText != null) {
			imageCaptionText.setValue(evidence.getImageCaption());
		}

		// set description
		editorDescription.setValue(evidence.getDescription());

		// check new values
		if (!ViewMode.CREATE.equals(mode) || evidence.getValue() != null) {
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

		// caption text
		if (FormFieldType.LINK_FILE.equals(evidence.getType()) && FileTools.isImage(evidence.getPath())) {
			FormFactory.createFormLabel(parent, RscTools.getString(RscConst.MSG_DLG_ADDEVID_LBL_IMG_CAPTION));
			imageCaptionText = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, false, null);
		}

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
		evidenceLink = FormFactory.createLinkWidget(parent, getViewManager(), null, true);
		evidenceLink.addChangedListener(e -> checkEvidence());

		// render the file changed checkbox
		if (ViewMode.UPDATE.equals(mode)
				&& getViewManager().getAppManager().getService(IPCMMEvidenceApp.class).evidenceChanged(evidence)) {
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

		// caption text
		if (FormFieldType.LINK_FILE.equals(evidence.getType()) && FileTools.isImage(evidence.getPath())) {
			FormFactory.createFormLabel(parent, RscTools.getString(RscConst.MSG_DLG_ADDEVID_LBL_IMG_CAPTION));
			imageCaptionText = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, true, null);
		}

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
		if (mode == ViewMode.VIEW) {
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

		// name
		String name = evidenceLink.getValue();
		if (FormFieldType.LINK_FILE.equals(evidenceLink.getLinkTypeSelected())) {
			name = evidenceLink.getFilename();
		}
		evidence.setName(name);

		// set path, type and caption
		evidence.setValue(evidenceLink.getGSONValue());

		// description
		evidence.setDescription(editorDescription.getValue());

		// section
		evidence.setSection(sectionText.getValue());

		// caption
		if (imageCaptionText != null) {
			evidence.setImageCaption(imageCaptionText.getValue());
		}

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

		// Check if evidence is not empty
		if (StringUtils.isBlank(evidenceLink.getValue())) {
			isValid = false;
			evidenceLink.setHelper(
					NotificationFactory.getNewError(RscTools.getString(RscConst.ERR_GENERICPARAM_PARAMETER_REQUIRED,
							RscTools.getString(RscConst.MSG_PCMMEVID_ITEM_TITLE))));
		} else {
			// Check if evidence value is valid
			evidenceLink.validateLink();
			isValid &= evidenceLink.isValid();
		}

		// Check if evidence with the same path were found
		if (isValid) {
			isValid &= checkEvidenceNotification();

			// set last file path selection preference
			PrefTools.setPreference(PrefTools.PCMM_EVIDENCE_FILE_LAST_PATH_KEY, evidenceLink.getValue());
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
				.getService(IPCMMEvidenceApp.class).getEvidenceNotifications(copy, evidence.getId());

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
