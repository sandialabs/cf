/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.User;
import gov.sandia.cf.parts.web.AuthenticationDialog;
import gov.sandia.cf.preferences.PrefTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.WebClientException;
import gov.sandia.cf.web.message.MessageManager;
import gov.sandia.cf.web.services.WebClientManager;
import gov.sandia.cf.web.services.status.PingManager;

/**
 * 
 * The CF web backend loader.
 * 
 * @author Didier Verstraete
 *
 */
public class CFWebBackendLoader {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(CFWebBackendLoader.class);

	/**
	 * Load web backend.
	 *
	 * @param editor the editor to load
	 * @throws CredibilityException the credibility exception
	 * @throws WebClientException   the web client exception
	 */
	public void load(CredibilityEditor editor) throws CredibilityException, WebClientException {

		if (editor == null) {
			logger.error("Impossible to load a null credibility editor"); //$NON-NLS-1$
			return;
		}

		logger.debug("Load web backend services"); //$NON-NLS-1$

		// if the option for web backend is not selected, throw an exception
		if (!PrefTools.getPreferenceBoolean(PrefTools.DEVOPTS_CONCURRENCY_SUPPORT_KEY).booleanValue()) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_CREDEDITOR_LOAD_WEB_CONCURRENCYSUPPORT_NOT_ACTIVATED));
		}

		// TODO replace with Controller manager
		WebClientManager webClientManager = new WebClientManager();
		webClientManager.setBaseURI(editor.getCache().getCFClientSetup().getWebServerURL());
		editor.setAppMgr(webClientManager);

		// load web client manager
		editor.getAppMgr().start();

		// CONCURRENCY SUPPORT: subscribe to the message broker global channel
		editor.setWebMsgMgr(new MessageManager(webClientManager));

		// load authentication
		AuthenticationDialog authenticationDialog = new AuthenticationDialog(editor.getViewMgr(),
				editor.getEditorShell());
		User user = authenticationDialog.openDialog();

		// the user decided to quit
		if (user == null) {
			// the method createPartControl will process toClose field.
			editor.setInError();
		} else {

			// load the message broker
			editor.getWebMsgMgr().start();

			// If the user is loaded, the method will load the views.
			editor.getCache().refreshModel();
			// CONCURRENCY SUPPORT: subscribe to the model channel
			editor.getWebMsgMgr().subscribeToModel(editor.getCache().getModel());

			// ping server to get connection status
			editor.setPingMgr(new PingManager(webClientManager, Display.getCurrent()));
			editor.getPingMgr().start();
			editor.getPingMgr().addListener(editor);

			// TODO
//			cache.refreshGlobalConfiguration();
//			cache.refreshUser();
		}
	}
}
