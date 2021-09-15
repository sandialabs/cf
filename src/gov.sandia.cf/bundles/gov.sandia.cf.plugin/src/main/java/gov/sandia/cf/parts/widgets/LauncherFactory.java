/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.parts.ui.IViewManager;

/**
 * The launcher component factory
 * 
 * @author Didier Verstraete
 *
 */
public class LauncherFactory {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(LauncherFactory.class);

	private LauncherFactory() {
		// Do not instantiate factory
	}

	/**
	 * @param viewMgr the view manager
	 * @param parent  the parent composite
	 * @return the newly created launcher with default style
	 */
	public static Launcher createLauncher(IViewManager viewMgr, Composite parent) {
		logger.debug("Creating new launcher"); //$NON-NLS-1$
		return createLauncher(viewMgr, parent, SWT.NONE);
	}

	/**
	 * @param viewMgr the view manager
	 * @param parent  the parent composite
	 * @param style   the SWT style
	 * @return the newly created launcher
	 */
	public static Launcher createLauncher(IViewManager viewMgr, Composite parent, int style) {
		logger.debug("Creating new launcher"); //$NON-NLS-1$
		return new Launcher(viewMgr, parent, style);
	}

}
