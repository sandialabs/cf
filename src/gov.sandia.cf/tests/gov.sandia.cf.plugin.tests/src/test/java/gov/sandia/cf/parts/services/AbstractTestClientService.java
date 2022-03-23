/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services;

import static org.junit.Assert.fail;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.runner.Version;

/**
 * Abstract class to define the squeleton of the Application layer tests
 * 
 * @author Didier Verstraete
 *
 */
class AbstractTestClientService {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractTestClientService.class);

	/**
	 * The client service layer manager
	 */
	private static IClientServiceManager clientSrvMgr;

	/**
	 * Initialize the test
	 */
	@BeforeAll
	public static void initialize() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			// load application layer classes
			clientSrvMgr = new ClientServiceManager();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Initialize the test
	 */
	@BeforeEach
	public void beforeTest() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			clientSrvMgr.start();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Clean the test
	 */
	@AfterEach
	public void afterTest() {
		clientSrvMgr.stop();

		// delete workspace files
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (project.exists()) {
				try {
					project.delete(true, new NullProgressMonitor());
				} catch (CoreException e) {
					logger.warn(e.getMessage());
				}
			}
		}
	}

	/**
	 * Clean the test
	 */
	@AfterAll
	public static void clean() {
		clientSrvMgr.stop();
		logger.info("Test ending"); //$NON-NLS-1$
	}

	/**
	 * Gets the client srv mgr.
	 *
	 * @return the client srv mgr
	 */
	public static IClientServiceManager getClientSrvMgr() {
		return clientSrvMgr;
	}
}
