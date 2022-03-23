/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.web;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.User;
import gov.sandia.cf.parts.dialogs.GenericCFSmallDialog;
import gov.sandia.cf.parts.ui.MainViewManager;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.parts.widgets.TextWidget;
import gov.sandia.cf.parts.widgets.TextWidget.TextWidgetType;
import gov.sandia.cf.tools.NetTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.services.authentication.IAuthenticationService;
import gov.sandia.cf.web.services.global.IModelWebClient;
import gov.sandia.cf.web.services.status.IStatusService;

/**
 * Dialog to authenticate web project.
 * 
 * @author Didier Verstraete
 *
 */
public class AuthenticationDialog extends GenericCFSmallDialog<MainViewManager> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AuthenticationDialog.class);
	private TextWidget txtServerURL;
	private TextWidget txtUserId;
	private TextWidget txtUserPassword;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 */
	public AuthenticationDialog(MainViewManager viewManager, Shell parentShell) {
		super(viewManager, parentShell);
	}

	@Override
	public void create() {
		super.create();
		setTitle(RscTools.getString(RscConst.MSG_WEB_DIALOG_AUTHENTICATION_TITLE));
		setMessage(RscTools.getString(RscConst.MSG_WEB_DIALOG_AUTHENTICATION_DESCRIPTION),
				IMessageProvider.INFORMATION);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, RscTools.getString(RscConst.MSG_BTN_CONNECT), true);
		createButton(parent, IDialogConstants.CANCEL_ID, RscTools.getString(RscConst.MSG_BTN_QUIT), false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		logger.debug("Create System Requirement dialog area"); //$NON-NLS-1$

		Composite container = (Composite) super.createDialogArea(parent);

		// form container
		Composite formContainer = new Composite(container, SWT.NONE);
		GridData scData = new GridData(SWT.FILL, SWT.FILL, true, true);
		formContainer.setLayoutData(scData);
		GridLayout gridLayout = new GridLayout(2, false);
		formContainer.setLayout(gridLayout);

		// Render form content
		renderForm(formContainer);

		// Load data
		loadData();

		// Return Control
		return container;
	}

	/**
	 * Render Editable content
	 * 
	 * @param parent
	 */
	private void renderForm(Composite parent) {

		// Server URL
		FormFactory.createLabel(parent, RscTools.getString(RscConst.MSG_WEB_DIALOG_AUTHENTICATION_SERVER_LBL));
		txtServerURL = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, false, null);

		// User ID
		FormFactory.createLabel(parent, RscTools.getString(RscConst.MSG_WEB_DIALOG_AUTHENTICATION_USERID_LBL));
		txtUserId = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, true, null);

		// User Password
		FormFactory.createLabel(parent, RscTools.getString(RscConst.MSG_WEB_DIALOG_AUTHENTICATION_USERPASSWORD_LBL));
		txtUserPassword = FormFactory.createTextWidget(getViewManager().getRscMgr(), parent, true,
				TextWidgetType.PASSWORD, null);
		txtUserPassword.setEnabled(false); // TODO to replace by real authentication
		txtUserPassword.setHelper(NotificationFactory.getNewInfo(
				RscTools.getString(RscConst.MSG_WEB_DIALOG_AUTHENTICATION_USERPASSWORD_EXPERIMENTAL_HELPER)));
	}

	/**
	 * Load Requirement data
	 */
	private void loadData() {

		// refresh user
		try {
			getViewManager().getCache().refreshUser();
		} catch (CredibilityException e) {
			MessageDialog.openError(getParentShell(), RscTools.getString(RscConst.MSG_WEB_DIALOG_AUTHENTICATION_TITLE),
					RscTools.getString(RscConst.ERR_WEB_DIALOG_AUTHENTICATION_LOADUSERFAILS));
		}

		// load data
		txtServerURL.setValue(getViewManager().getCache().getCFClientSetup().getWebServerURL());
		txtUserId.setValue(getViewManager().getCache().getUser().getUserID());
		// TODO to replace with real password management
		txtUserPassword.setValue(getViewManager().getCache().getUser().getUserID());
	}

	/**
	 * Open dialog.
	 *
	 * @return the user authenticated
	 */
	public User openDialog() {
		if (open() == Window.OK) {
			// TODO implement
			return getViewManager().getCache().getUser();
		}
		return null;
	}

	@Override
	protected void okPressed() {
		try {
			// Open project - Validate form and open web project
			openProject();
		} catch (WebClientRuntimeException e) {
			logger.error(e.getMessage(), e);
			setErrorMessage(RscTools.getString(RscConst.ERR_WEB_DIALOG_AUTHENTICATION_HOST_UNREACHABLE));
		}
	}

	/**
	 * Open project - Validate form and open web project
	 */
	private void openProject() {

		setMessage(null);
		setErrorMessage(null);

		boolean valid = true;

		// validate connection
		valid &= checkServerConnection();

		// validate authentication
		if (valid)
			valid &= checkUserAuthentication();

		// validate model existence
		if (valid)
			valid &= checkRemoteProject();

		// Call super
		if (valid)
			super.okPressed();
	}

	/**
	 * Check server connection.
	 *
	 * @return true, if successful
	 */
	private boolean checkServerConnection() {

		boolean valid = NetTools.isValidURL(txtServerURL.getValue());

		// ping server REST API
		valid &= getViewManager().getWebClient().getService(IStatusService.class).ping();

		if (!valid) {
			setErrorMessage(RscTools.getString(RscConst.ERR_WEB_DIALOG_AUTHENTICATION_HOST_UNREACHABLE));
		}

		return valid;
	}

	/**
	 * Check user authentication.
	 *
	 * @return true, if successful
	 */
	private boolean checkUserAuthentication() {

		// check connection
		boolean valid = false;
		try {
			valid = getViewManager().getWebClient().getService(IAuthenticationService.class)
					.connect(txtServerURL.getValue(), txtUserId.getValue(), txtUserPassword.getValue());
		} catch (CredibilityException e) {
			logger.error("The credentials are not valid {}", e.getMessage(), e); //$NON-NLS-1$
		}

		if (!valid) {
			setErrorMessage(RscTools.getString(RscConst.ERR_WEB_DIALOG_AUTHENTICATION_INVALID_CREDENTIALS));
		}

		return valid;
	}

	/**
	 * Check remote project.
	 *
	 * @return true, if successful
	 */
	private boolean checkRemoteProject() {

		// check connection
		boolean valid = false;
		try {
			Model model = getViewManager().getWebClient().getService(IModelWebClient.class)
					.loadModel(getViewManager().getCache().getCFClientSetup().getModelId());
			valid = (model != null && model.getId() != null);
		} catch (WebClientRuntimeException | CredibilityException e) {
			valid = false;
		}

		// Enable/disable ok button
		setEnableOkButton(valid);

		if (!valid) {
			setErrorMessage(RscTools.getString(RscConst.ERR_WEB_DIALOG_AUTHENTICATION_INVALID_MODEL));
		}

		return valid;
	}
}
