/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
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

	private static final String LOGBACK_FILENAME = "logback.xml"; //$NON-NLS-1$

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

		// configure logger
		configureLogbackInBundle(bundleContext.getBundle());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(BundleContext bundleContext) throws Exception {

		context = null;

		logger.info("Credibility plugin stopped"); //$NON-NLS-1$
	}

	/**
	 * Configure logback in bundle.
	 *
	 * @param bundle the bundle
	 * @throws JoranException the joran exception
	 * @throws IOException    Signals that an I/O exception has occurred.
	 */
	private void configureLogbackInBundle(Bundle bundle) throws JoranException, IOException {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		if (loggerContext == null) {
			logger.warn("Impossible to load the logger context"); //$NON-NLS-1$
			return;
		}

		JoranConfigurator jc = new JoranConfigurator();
		jc.setContext(loggerContext);
		loggerContext.reset();

		Location configurationLocation = Platform.getInstallLocation();
		if (configurationLocation != null
				&& new File(configurationLocation.getURL().getPath(), LOGBACK_FILENAME).exists()) {
			jc.doConfigure(new File(configurationLocation.getURL().getPath(), LOGBACK_FILENAME));
		} else if (bundle != null) {
			URL logbackConfigFileUrl = bundle.getResource(LOGBACK_FILENAME);
			if (logbackConfigFileUrl == null) {
				logbackConfigFileUrl = FileLocator.find(bundle, new Path(LOGBACK_FILENAME), null);
			}
			if (logbackConfigFileUrl == null) {
				logger.error("Impossible to find log configuration file ({}).", LOGBACK_FILENAME); //$NON-NLS-1$
			} else {
				jc.doConfigure(logbackConfigFileUrl.openStream());
			}
		} else {
			logger.error("Impossible to find log configuration file ({}).", LOGBACK_FILENAME); //$NON-NLS-1$
		}

		// set configured logger level
		ch.qos.logback.classic.Logger cfLogger = loggerContext.getLogger(CredibilityFrameworkConstants.CF_PACKAGE_ROOT);
		String logLevel = PrefTools.getPreference(PrefTools.DEVOPTS_LOG_LEVEL_KEY);
		if (cfLogger != null && !StringUtils.isBlank(logLevel)) {
			Level level = Level.toLevel(logLevel);
			if (level != null) {
				cfLogger.setLevel(level);
			}
		}
	}

}
