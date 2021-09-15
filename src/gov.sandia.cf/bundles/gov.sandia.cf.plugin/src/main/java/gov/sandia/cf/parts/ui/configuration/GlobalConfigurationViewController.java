/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GlobalConfiguration;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Global Configuration view controller: Used to control the Global
 * Configuration view
 * 
 * @author Didier Verstraete
 *
 */
public class GlobalConfigurationViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GlobalConfigurationViewController.class);

	/**
	 * The view
	 */
	private GlobalConfigurationView view;

	/**
	 * The global configuration data
	 */
	private GlobalConfiguration configuration;

	GlobalConfigurationViewController(GlobalConfigurationView view) {
		Assert.isNotNull(view);
		this.view = view;
		this.configuration = view.getViewManager().getCache().getGlobalConfiguration();

		// Refresh data and Save state
		this.view.refresh();
	}

	/**
	 * Update openLinkOption of the global configuration
	 * 
	 * @param openLinkOption the open Link Option to set
	 */
	void updateGlobalConfigurationAction(OpenLinkBrowserOption openLinkOption) {

		if (configuration == null) {
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_CONF_GLOBALVIEW_TITLE),
					RscTools.getString(RscConst.CARRIAGE_RETURN)
							+ RscTools.getString(RscConst.ERR_CONF_GLOBALVIEW_CONFNULL));
			return;
		}

		OpenLinkBrowserOption optionToUpdate = openLinkOption;
		if (optionToUpdate == null) {
			optionToUpdate = OpenLinkBrowserOption.CF_PREFERENCE;
		}

		// if it is the same, do nothing
		if (configuration.getOpenLinkBrowserOpts().equals(optionToUpdate.name())) {
			return;
		}

		try {

			// set option
			configuration.setOpenLinkBrowserOpts(optionToUpdate.name());

			// Create
			view.getViewManager().getAppManager().getService(IGlobalApplication.class)
					.updateGlobalConfiguration(configuration);

			// Fire view change to save credibility file
			view.getViewManager().viewChanged();

			// Refresh
			view.getViewManager().getCache().refreshGlobalConfiguration();
			view.refresh();

		} catch (CredibilityException e) {
			logger.error(RscTools.getString(RscConst.ERR_CONF_GLOBALVIEW_TITLE),
					RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERR_CONF_GLOBALVIEW_TITLE),
					RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	@SuppressWarnings("javadoc")
	public GlobalConfiguration getConfiguration() {
		return configuration;
	}

}
