/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GlobalConfiguration;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Global Configuration view controller: Used to control the Global
 * Configuration view
 * 
 * @author Didier Verstraete
 *
 */
public class GlobalConfigurationViewController
		extends AViewController<ConfigurationViewManager, GlobalConfigurationView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(GlobalConfigurationViewController.class);

	/**
	 * The global configuration data
	 */
	private GlobalConfiguration configuration;

	/**
	 * Instantiates a new global configuration view controller.
	 *
	 * @param viewManager the view manager
	 * @param parent      the parent
	 */
	GlobalConfigurationViewController(ConfigurationViewManager viewManager, Composite parent) {
		super(viewManager);
		super.setView(new GlobalConfigurationView(this, parent, SWT.NONE));
		this.configuration = getViewManager().getCache().getGlobalConfiguration();

		// Refresh data and Save state
		refresh();
	}

	/**
	 * Reload data.
	 */
	void reloadData() {
		getView().reloadOpenLinkBrowserOptions();
	}

	/**
	 * Update openLinkOption of the global configuration
	 * 
	 * @param openLinkOption the open Link Option to set
	 */
	void updateGlobalConfigurationAction(OpenLinkBrowserOption openLinkOption) {

		if (configuration == null) {
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_CONF_GLOBALVIEW_TITLE),
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
			getViewManager().getAppManager().getService(IGlobalApplication.class)
					.updateGlobalConfiguration(configuration);

			// Fire view change to save credibility file
			getViewManager().viewChanged();

			// Refresh
			getViewManager().getCache().refreshGlobalConfiguration();
			getView().refresh();

		} catch (CredibilityException e) {
			logger.error(RscTools.getString(RscConst.ERR_CONF_GLOBALVIEW_TITLE),
					RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERR_CONF_GLOBALVIEW_TITLE),
					RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	@SuppressWarnings("javadoc")
	public GlobalConfiguration getConfiguration() {
		return configuration;
	}
}
