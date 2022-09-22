/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.home;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.services.global.IModelWebClient;

/**
 * PCMM home controller: Used to control the PCMM Home view
 * 
 * @author Didier Verstraete
 *
 */
public class HomeViewController extends AViewController<HomeViewManager, HomeView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(HomeViewController.class);

	/**
	 * Constructor.
	 *
	 * @param viewManager the view manager
	 */
	HomeViewController(HomeViewManager viewManager) {
		super(viewManager);
		super.setView(new HomeView(this, SWT.NONE));
	}

	/**
	 * WEB only.
	 * 
	 * Delete project.
	 */
	void deleteProject() {
		boolean toDelete = getView().displayQuestion(RscTools.getString(RscConst.MSG_HOMEVIEW_DIALOG_TITLE),
				RscTools.getString(RscConst.MSG_HOMEVIEW_BTN_DELETE_PROJECT_CONFIRM));

		if (toDelete) {
			try {
				// delete remote project
				getViewManager().getWebClient().getService(IModelWebClient.class)
						.delete(getViewManager().getCache().getCFClientSetup().getModelId());

				// delete and close current CF file
				getViewManager().getCredibilityEditor().deleteAndCloseFile();

			} catch (CredibilityException e) {
				logger.error("An error occurs during model deletion", e); //$NON-NLS-1$
				MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.MSG_HOMEVIEW_DIALOG_TITLE),
						e.getMessage());
			}
		}
	}

	/**
	 * @return the origin (creation) version of the cf file.
	 */
	String getVersionOrigin() {

		// Load or create model
		Model model = null;

		// Retrieve the model's version
		try {
			model = getViewManager().getAppManager().getService(IGlobalApplication.class).loadModel();
		} catch (CredibilityException e) {
			logger.error("An error occured while retrieving the version number: {}", e.getMessage(), e); //$NON-NLS-1$
		}

		// get version origin
		String versionOrigin = RscTools.getString(RscConst.MSG_VERSION_ORIGIN_UNDEFINED);
		if (model != null) {
			versionOrigin = RscTools.getString(RscConst.MSG_VERSION_ORIGIN_BEFORE_0_2_0);
			if (null != model.getVersionOrigin()) {
				versionOrigin = model.getVersionOrigin();
			}
		}

		return versionOrigin;
	}

}
