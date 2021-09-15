/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CredibilityFrameworkConstants;

/**
 * This class is the entry point of the plugin
 * 
 * @author Didier Verstraete
 *
 */
public class Startup implements IStartup {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Startup.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void earlyStartup() {
		// start plugin if it is not automatically started
		Bundle cfBundle = CredibilityFrameworkConstants.getBundle();
		if (cfBundle != null && cfBundle.getState() == Bundle.INSTALLED && cfBundle.getState() != Bundle.ACTIVE) {
			try {
				Version cfVersion = cfBundle.getVersion();
				logger.info("Credibility plugin started by Starter: {}, version={}", //$NON-NLS-1$
						CredibilityFrameworkConstants.CF_PLUGIN_NAME, cfVersion);
				cfBundle.start();

			} catch (BundleException e) {
				logger.error(e.getMessage(), e);
			}
		}
	}

}
