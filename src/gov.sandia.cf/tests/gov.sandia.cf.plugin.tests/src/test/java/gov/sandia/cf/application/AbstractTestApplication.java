/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.Rule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.exports.IExportApplication;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.pcmm.IPCMMAggregateApp;
import gov.sandia.cf.application.pcmm.IPCMMApplication;
import gov.sandia.cf.application.pcmm.IPCMMAssessmentApp;
import gov.sandia.cf.application.pcmm.IPCMMEvidenceApp;
import gov.sandia.cf.application.pcmm.IPCMMPlanningApplication;
import gov.sandia.cf.application.pirt.IPIRTApplication;
import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.tools.FileTools;
import junit.runner.Version;

/**
 * Abstract class to define the squeleton of the Application layer tests
 * 
 * @author Didier Verstraete
 *
 */
class AbstractTestApplication {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractTestApplication.class);
	/**
	 * temporary folder to store the hsqldb database
	 */
	@Rule
	private static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	/**
	 * The application layer manager
	 */
	private static ApplicationManager appMgr;

	/**
	 * A temporary folder under the main temporary folder
	 */
	private File createdFolder;

	/**
	 * Initialize the test
	 */
	@BeforeAll
	public static void initialize() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			TEMP_FOLDER.create();

			// load application layer classes
			appMgr = new ApplicationManager();
			appMgr.getDaoManager().setPersistUnitName(AbstractTestDao.ENTITY_PERSIST_UNIT_NAME_TEST);

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
			createdFolder = TEMP_FOLDER.newFolder();

			appMgr.start();
			getDaoManager().initialize(createdFolder.getPath());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Clean the test
	 */
	@AfterEach
	public void afterTest() {
		appMgr.stop();
		if (createdFolder != null && createdFolder.exists()) {
			try {
				FileTools.deleteDirectoryRecursively(createdFolder);
				assertFalse("Directory still exists", createdFolder.exists()); //$NON-NLS-1$
			} catch (IOException e) {
				fail(e.getMessage());
			}
		}

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
		appMgr.stop();
		TEMP_FOLDER.delete();
		assertFalse("Directory still exists", TEMP_FOLDER.getRoot().exists()); //$NON-NLS-1$
		logger.info("Test ending"); //$NON-NLS-1$
	}

	/**
	 * @return the app manager
	 */
	public ApplicationManager getAppManager() {
		return appMgr;
	}

	/**
	 * @return the dao manager
	 */
	public IDaoManager getDaoManager() {
		return appMgr.getDaoManager();
	}

	/**
	 * @return the test temp folder
	 */
	public File getTestTempFolder() {
		return this.createdFolder;
	}

	/**
	 * @return the pcmm application manager
	 */
	public IPCMMApplication getPCMMApp() {
		return getAppManager().getService(IPCMMApplication.class);
	}

	/**
	 * Gets the PCMM evidence app.
	 *
	 * @return the PCMM evidence app
	 */
	public IPCMMEvidenceApp getPCMMEvidenceApp() {
		return getAppManager().getService(IPCMMEvidenceApp.class);
	}

	/**
	 * Gets the PCMM assessment app.
	 *
	 * @return the PCMM assessment app
	 */
	public IPCMMAssessmentApp getPCMMAssessmentApp() {
		return getAppManager().getService(IPCMMAssessmentApp.class);
	}

	/**
	 * Gets the PCMM aggregate app.
	 *
	 * @return the PCMM aggregate app
	 */
	public IPCMMAggregateApp getPCMMAggregateApp() {
		return getAppManager().getService(IPCMMAggregateApp.class);
	}

	/**
	 * @return the pcmm planning application manager
	 */
	public IPCMMPlanningApplication getPCMMPlanningApp() {
		return getAppManager().getService(IPCMMPlanningApplication.class);
	}

	/**
	 * @return the pirt application manager
	 */
	public IPIRTApplication getPIRTApp() {
		return getAppManager().getService(IPIRTApplication.class);
	}

	/**
	 * @return the import application manager
	 */
	public IImportApplication getImportApp() {
		return getAppManager().getService(IImportApplication.class);
	}

	/**
	 * @return the export application manager
	 */
	public IExportApplication getExportApp() {
		return getAppManager().getService(IExportApplication.class);
	}
}
