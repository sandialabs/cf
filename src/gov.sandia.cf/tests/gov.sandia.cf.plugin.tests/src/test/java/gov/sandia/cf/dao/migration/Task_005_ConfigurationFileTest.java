/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.eclipse.persistence.exceptions.DatabaseException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IConfigurationFileRepository;
import gov.sandia.cf.dao.migration.tasks.Task_005_ConfigurationFileTable;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.ConfigurationFile;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class Task_005_ConfigurationFileTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_005_ConfigurationFileTest.class);

	/* ****** TEST: ConfFileMigrationTask ***** */

	@Test
	void test_ConfFileMigrationTask_NotMigrated() {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);

		unitOfWork.executeNonSelectingSQL("ALTER TABLE MODEL ADD COLUMN PIRT_SCHEMA_PATH VARCHAR(1000);"); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL("UPDATE MODEL SET PIRT_SCHEMA_PATH='MyPathtoPIRT'"); //$NON-NLS-1$

		unitOfWork.executeNonSelectingSQL("ALTER TABLE MODEL ADD COLUMN PCMM_SCHEMA_PATH VARCHAR(1000);"); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL("UPDATE MODEL SET PCMM_SCHEMA_PATH='MyPathtoPCMM'"); //$NON-NLS-1$

		unitOfWork.executeNonSelectingSQL("ALTER TABLE MODEL ADD COLUMN COM_UNCERTAINTY_SCHEMA_PATH VARCHAR(1000);"); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL("UPDATE MODEL SET COM_UNCERTAINTY_SCHEMA_PATH='MyPathtoUNCERTAINTY'"); //$NON-NLS-1$

		unitOfWork.executeNonSelectingSQL("ALTER TABLE MODEL ADD COLUMN COM_REQUIREMENT_SCHEMA_PATH VARCHAR(1000);"); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL("UPDATE MODEL SET COM_REQUIREMENT_SCHEMA_PATH='MyPathtoSYSREQ'"); //$NON-NLS-1$

		// test
		try {
			boolean changed = new Task_005_ConfigurationFileTable().execute(getDaoManager());
			assertTrue(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}

		// check conf file model columns deletion
		try {
			unitOfWork.executeSQL("SELECT PIRT_SCHEMA_PATH FROM MODEL"); //$NON-NLS-1$
			fail("The column PIRT_SCHEMA_PATH should have been deleted from table MODEL."); //$NON-NLS-1$
		} catch (DatabaseException e) {
			assertNotNull(e);
		}
		try {
			unitOfWork.executeSQL("SELECT PCMM_SCHEMA_PATH FROM MODEL"); //$NON-NLS-1$
			fail("The column PCMM_SCHEMA_PATH should have been deleted from table MODEL."); //$NON-NLS-1$
		} catch (DatabaseException e) {
			assertNotNull(e);
		}
		try {
			unitOfWork.executeSQL("SELECT COM_UNCERTAINTY_SCHEMA_PATH FROM MODEL"); //$NON-NLS-1$
			fail("The column COM_UNCERTAINTY_SCHEMA_PATH should have been deleted from table MODEL."); //$NON-NLS-1$
		} catch (DatabaseException e) {
			assertNotNull(e);
		}
		try {
			unitOfWork.executeSQL("SELECT COM_REQUIREMENT_SCHEMA_PATH FROM MODEL"); //$NON-NLS-1$
			fail("The column COM_REQUIREMENT_SCHEMA_PATH should have been deleted from table MODEL."); //$NON-NLS-1$
		} catch (DatabaseException e) {
			assertNotNull(e);
		}

		// check conf file existence in CONFIGURATION_FILE table
		List<ConfigurationFile> findAll = getDaoManager().getRepository(IConfigurationFileRepository.class).findAll();
		assertNotNull(findAll);
		assertFalse(findAll.isEmpty());
		assertTrue(findAll.stream().anyMatch(c -> CFFeature.PIRT.equals(c.getFeature())));
		assertTrue(findAll.stream().anyMatch(c -> CFFeature.PCMM.equals(c.getFeature())));
		assertTrue(findAll.stream().anyMatch(c -> CFFeature.UNCERTAINTY.equals(c.getFeature())));
		assertTrue(findAll.stream().anyMatch(c -> CFFeature.SYSTEM_REQUIREMENTS.equals(c.getFeature())));
	}

	@Test
	void test_ConfFileMigrationTask_AlreadyMigrated() {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			boolean changed = new Task_005_ConfigurationFileTable().execute(getDaoManager());
			assertFalse(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}
}
