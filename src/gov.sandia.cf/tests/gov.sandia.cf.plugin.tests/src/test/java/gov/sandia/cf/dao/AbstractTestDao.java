/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

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

import gov.sandia.cf.dao.hsqldb.HSQLDBDaoManager;
import junit.runner.Version;

/**
 * @author Didier Verstraete
 *
 *         Abstract DAO class to load/clean database
 */
public abstract class AbstractTestDao {

	/**
	 * the logger
	 */
	private static Logger logger = LoggerFactory.getLogger(AbstractTestDao.class);

	/**
	 * The test persist entity in the persistence.xml file
	 */
	public static final String ENTITY_PERSIST_UNIT_NAME_TEST = "credibility-test"; //$NON-NLS-1$

	/**
	 * temporary folder to store the hsqldb database
	 */
	@Rule
	private static TemporaryFolder TEMP_FOLDER = new TemporaryFolder();

	/**
	 * the bean validator
	 */
	private static Validator VALIDATOR;

	/**
	 * the dao manager
	 */
	private static DaoManager daoManager = new DaoManager(ENTITY_PERSIST_UNIT_NAME_TEST);

	/**
	 * Create migration table script
	 */
	public static final String TABLE_MIGRATION_SCRIPT = "create_table_migration.sql"; //$NON-NLS-1$
	/**
	 * Script in error name
	 */
	public static final String SCRIPT_IN_ERROR = "script_with_error.sql"; //$NON-NLS-1$

	/**
	 * @return the dao manager
	 */
	public DaoManager getDaoManager() {
		return daoManager;
	}

	/**
	 * @return the temporary folder
	 */
	public TemporaryFolder getTempFolder() {
		return TEMP_FOLDER;
	}

	/**
	 * Initialize the test
	 */
	@BeforeAll
	public static void initialize() {
		logger.info("JUnit version is: " + Version.id()); //$NON-NLS-1$
		try {
			logger.info("Test started"); //$NON-NLS-1$

			TEMP_FOLDER.create();

			ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
			VALIDATOR = factory.getValidator();

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Initialize the test
	 */
	@BeforeEach
	public void beforeTest() {
		try {
			logger.info("Test started"); //$NON-NLS-1$
			File createdFolder = TEMP_FOLDER.getRoot();
			daoManager.start();
			daoManager.initialize(createdFolder.getPath());

		} catch (Exception e) {
			fail(e.getMessage());
		}
	}

	/**
	 * Clean the test
	 */
	@AfterEach
	public void afterTest() {
		daoManager.stop();
		if (daoManager.getDatabaseDirectoryPath() != null && new File(daoManager.getDatabaseDirectoryPath()).exists()) {
			try {
				File createdFolder = new File(daoManager.getDatabaseDirectoryPath());
				HSQLDBDaoManager.dropDatabaseFiles(daoManager.getDatabaseDirectoryPath());
				Files.walk(createdFolder.toPath()).sorted(Comparator.reverseOrder()).map(Path::toFile)
						.forEach(File::delete);
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
		daoManager.stop();
		try {
			HSQLDBDaoManager.dropDatabaseFiles(TEMP_FOLDER.getRoot().toString());
		} catch (IOException e) {
			fail("Exception during temporary folders deletion" + e.getMessage()); //$NON-NLS-1$
		}
		TEMP_FOLDER.delete();
		logger.info("Test ending"); //$NON-NLS-1$
	}

	/**
	 * @return the bean validator
	 */
	public Validator getValidator() {
		return VALIDATOR;
	}

}
