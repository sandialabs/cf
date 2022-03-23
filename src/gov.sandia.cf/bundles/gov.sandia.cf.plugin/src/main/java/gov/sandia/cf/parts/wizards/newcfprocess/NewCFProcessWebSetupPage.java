/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import gov.sandia.cf.parts.constants.PartsResourceConstants;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.ColorTools;
import gov.sandia.cf.tools.NetTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;
import gov.sandia.cf.web.services.status.IStatusService;

/**
 * A newWizard page to create a new credibility process with a text and a
 * FileDialog to select the credibility link file
 * 
 * @author Didier Verstraete
 *
 */
public class NewCFProcessWebSetupPage extends WizardPage implements INewCFProcessWebSetupPage {
	/**
	 * the parent wizard
	 */
	private NewCFProcessWizard parent;

	/** The text server URL. */
	private Text textServerURL;

	/** The default server URL. */
	private String defaultServerURL;

	/** The btn test connection. */
	private Button btnTestConnection;

	/** The lbl test connection. */
	private CLabel lblTestConnection;
	/**
	 * check box to generate evidence folder structure
	 */
	private Button chbxGenerateFolderStructure;
	/**
	 * the path to generate evidence folder structure
	 */
	private IPath evidenceFolderStructurePath;
	/**
	 * check box to generate evidence folder structure
	 */
	private Label lblGenerateEvidenceFolderStructure;

	/**
	 * the error message lists
	 */
	private Set<String> errMsgCommon;

	/** The is host validated. */
	private boolean isHostValidated;

	/**
	 * The constructor
	 * 
	 * @param parent the parent wizard
	 */
	public NewCFProcessWebSetupPage(NewCFProcessWizard parent) {
		super(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBSETUP_PAGE_PAGENAME));
		setTitle(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBSETUP_PAGE_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBSETUP_PAGE_DESCRIPTION));

		this.parent = parent;

		this.errMsgCommon = new HashSet<>();

		// Get server URL from preferences
		defaultServerURL = PrefTools.getPreference(PrefTools.WEB_SERVER_URL);
		isHostValidated = false;
	}

	/** {@inheritDoc} */
	@Override
	public void createControl(Composite parent) {

		Composite container = new Composite(parent, SWT.NONE);
		
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;

		// label
		FormFactory.createLabel(container, RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_LBL));

		// text path
		textServerURL = new Text(container, SWT.BORDER | SWT.SINGLE);
		textServerURL.setText(defaultServerURL);
		textServerURL.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				validateURL();

				// recall page complete process
				setPageComplete(errMsgCommon.isEmpty());
			}
		});
		GridData gdTxtPath = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);
		textServerURL.setLayoutData(gdTxtPath);

		// add Test label
		lblTestConnection = FormFactory.createFormLabel(container, RscTools.empty());

		// add Test button
		btnTestConnection = new Button(container, SWT.NONE);
		btnTestConnection.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_TEST_BTN));
		btnTestConnection.setLayoutData(new GridData(SWT.RIGHT, SWT.FILL, true, false, 1, 1));
		((GridData) btnTestConnection.getLayoutData()).heightHint = 40;
		btnTestConnection.addListener(SWT.Selection, event -> {

			// test connection
			handleTestConnection();

			// recall page complete process
			setPageComplete(true);
		});

		// generate credibility evidence folder structure check-box
		// set by default to true
		chbxGenerateFolderStructure = new Button(container, SWT.CHECK);
		chbxGenerateFolderStructure
				.setText(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WEBSETUP_PAGE_CHBX_EVID_STRUCT));
		chbxGenerateFolderStructure.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				String text = btn.getSelection()
						? RscTools.getString(RscConst.ERR_NEWCFPROCESS_WEBSETUP_PAGE_EVID_PARENT_PATH,
								evidenceFolderStructurePath)
						: RscTools.empty();
				lblGenerateEvidenceFolderStructure.setText(text);
				checkPathToGenerateFolderStructure();
			}
		});
		GridData gdBtnGenEvidSchema = new GridData(GridData.FILL_HORIZONTAL);
		gdBtnGenEvidSchema.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		gdBtnGenEvidSchema.verticalIndent = 15;
		chbxGenerateFolderStructure.setLayoutData(gdBtnGenEvidSchema);

		// label
		lblGenerateEvidenceFolderStructure = new Label(container, SWT.NONE);
		GridData gdLabelGenEvidSchema = new GridData(GridData.FILL_HORIZONTAL);
		gdLabelGenEvidSchema.horizontalSpan = PartsResourceConstants.CREDCONFWIZARD_NUM_COLUMNS;
		lblGenerateEvidenceFolderStructure.setLayoutData(gdLabelGenEvidSchema);

		// global
		setControl(container);
	}

	/**
	 * Initializes data from previous page
	 */
	private void initDataFromPreviousPage() {

		// default setup
		evidenceFolderStructurePath = parent.getPageNewCredibilityFile().getContainerFullPath();
		chbxGenerateFolderStructure.setSelection(true);
		chbxGenerateFolderStructure.notifyListeners(SWT.Selection, new Event());

		// validate URL and connection
		validateURL();
		handleTestConnection();

		// validate data
		checkPathToGenerateFolderStructure();
		updateMessages(false);
	}

	/**
	 * Handle test connection.
	 */
	private void handleTestConnection() {

		// change web client base URI
		parent.getWebClientManager().setBaseURI(textServerURL.getText());

		// test URL
		validateURL();
		pingServer();

		boolean hostReached = errMsgCommon.isEmpty();

		if (hostReached) {
			btnTestConnection.setImage(getImage());
			btnTestConnection.setImage(IconTheme.getIconImage(this.parent.getResourceManager(),
					IconTheme.ICON_NAME_UPTODATE, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN)));

			lblTestConnection
					.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_TEST_VALID_LBL));
			lblTestConnection.setForeground(ColorTools.toColor(this.parent.getResourceManager(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_GREEN)));

			isHostValidated = true;
		} else {
			btnTestConnection.setImage(getImage());
			btnTestConnection.setImage(IconTheme.getIconImage(this.parent.getResourceManager(),
					IconTheme.ICON_NAME_FAIL, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_RED)));

			lblTestConnection
					.setText(RscTools.getString(RscConst.MSG_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_TEST_NOTVALID_LBL));
			lblTestConnection.setForeground(ColorTools.toColor(this.parent.getResourceManager(),
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_RED)));

			isHostValidated = false;
		}

		btnTestConnection.requestLayout();
	}

	/**
	 * Validate URL.
	 */
	private void validateURL() {

		boolean error = false;
		errMsgCommon.clear();

		if (!NetTools.isValidURL(textServerURL.getText())) {
			error = true;
			errMsgCommon.add(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_URL_NOTVALID));
		}

		isHostValidated = false;

		updateMessages(error);
	}

	/**
	 * Ping server.
	 */
	private void pingServer() {

		boolean error = false;
		errMsgCommon.clear();

		if (!parent.getWebClientManager().getService(IStatusService.class).ping()) {
			error = true;
			errMsgCommon.add(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WEBSETUP_PAGE_SERVER_UNREACHABLE));
		}

		updateMessages(error);
	}

	/**
	 * Validate the generate folder structure path (from previous page) if
	 * chbxGenerateFolderStructure is checked
	 */
	private void checkPathToGenerateFolderStructure() {
		// Initialize
		boolean valid = false;

		// if chbxGenerateFolderStructure is checked, validate folder path, otherwise do
		// not validate
		if (chbxGenerateFolderStructure.getSelection()) {
			if (evidenceFolderStructurePath != null) {
				IContainer evidenceFolderStructure = null;
				// if it is a project
				if (evidenceFolderStructurePath.segmentCount() == 1) {
					evidenceFolderStructure = WorkspaceTools
							.getProjectInWorkspaceForPath(evidenceFolderStructurePath.segment(0));
				} else if (evidenceFolderStructurePath.segmentCount() > 1) {
					// maybe it is a folder and not a project
					evidenceFolderStructure = WorkspaceTools.getFolderInWorkspaceForPath(evidenceFolderStructurePath);
				}
				valid = evidenceFolderStructure != null && evidenceFolderStructure.exists();
			}
		} else {
			valid = true;
		}

		if (valid) {
			errMsgCommon.remove(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WEBSETUP_PAGE_BAD_STRUCT_FOLDER_PATH));
		} else {
			errMsgCommon.add(RscTools.getString(RscConst.ERR_NEWCFPROCESS_WEBSETUP_PAGE_BAD_STRUCT_FOLDER_PATH));
		}

		updateMessages(true);
	}

	/**
	 * Update the error messages of wizard and the page complete field
	 */
	private void updateMessages(boolean error) {

		final StringBuilder errorString = new StringBuilder();

		if (!errMsgCommon.isEmpty()) {
			errorString.append(errorString.length() == 0 ? RscTools.empty() : RscTools.CARRIAGE_RETURN);
			errorString.append(
					errMsgCommon.stream().map(Object::toString).collect(Collectors.joining(RscTools.CARRIAGE_RETURN)));
		}

		// if empty, set the message string to null because of the wizard
		// if it is an error message
		boolean emptyMsgError = errorString.length() == 0;
		if (error) {
			setMessage(null);
			setErrorMessage(emptyMsgError ? null : errorString.toString());
		} else { // otherwise
			setErrorMessage(null);
			setMessage(emptyMsgError ? null : errorString.toString());
		}
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			initDataFromPreviousPage();
		}
	}

	@Override
	public void setPageComplete(boolean complete) {

		// change web client base URI
		if (complete) {
			parent.getWebClientManager().setBaseURI(textServerURL.getText());
		}

		super.setPageComplete(complete);
	}

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && isPageValid();
	}

	/**
	 * Checks if is page valid.
	 *
	 * @return true, if is page valid
	 */
	private boolean isPageValid() {
		return isHostValidated && !StringUtils.isBlank(this.textServerURL.getText()) && errMsgCommon.isEmpty();
	}

	@Override
	public IWizardPage getPreviousPage() {
		return parent.getPageBackendSelection();
	}

	/** {@inheritDoc} */
	@Override
	public boolean getGenerateFolderStructure() {
		return chbxGenerateFolderStructure.getSelection();
	}

	@Override
	public String getServerURL() {
		return this.textServerURL.getText();
	}
}
