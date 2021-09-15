/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.preferences.PrefTools;

/**
 * This class is the entry point of the plugin
 * 
 * @author Didier Verstraete
 *
 */
public class Activator implements BundleActivator {

	/**
	 * the logger
	 */
	private static Logger logger = LoggerFactory.getLogger(Activator.class);

	/**
	 * context: the context of the activator
	 */
	private BundleContext context;

	/**
	 * @return the bundle context
	 */
	public BundleContext getContext() {
		return context;
	}

	/**
	 * The constructor
	 */
	public Activator() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(BundleContext bundleContext) throws Exception {

		logger.debug("Credibility plugin started"); //$NON-NLS-1$

		context = bundleContext;

		// initialize default preferences if not set
		PrefTools.initializePreferencesToDefault();

		// clear credibility local database on .cf file removing
		ResourcesPlugin.getWorkspace().addResourceChangeListener(event -> {
			try {
				if (event != null && event.getDelta() != null) {
					// catch delete and rename events
					event.getDelta().accept(new ResourceDeltaModifier(), IResourceChangeEvent.POST_CHANGE);
				}
			} catch (CoreException e) {
				logger.error("an error occured on resource change: {}", e.getMessage(), e); //$NON-NLS-1$
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {

		context = null;

		logger.info("Credibility plugin stopped"); //$NON-NLS-1$
	}

}
