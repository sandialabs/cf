/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IARGParametersRepository;
import gov.sandia.cf.dao.migration.tasks.Task_010_ARGParam_Variable;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * The Class Task_010_ARGParam_VariableTest.
 *
 * @author Didier Verstraete
 */
class Task_010_ARGParam_VariableTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_010_ARGParam_VariableTest.class);

	@Test
	void test_MigrationTask_NotMigrated() {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// Needed to execute a migration task
		ARGParameters newARGParameters = TestEntityFactory.getNewARGParameters(getDaoManager());
		newARGParameters.setParametersFilePath(Task_010_ARGParam_Variable.OLD_VAR_WORKSPACE + "/parameters.yml"); //$NON-NLS-1$
		newARGParameters.setStructureFilePath(Task_010_ARGParam_Variable.OLD_VAR_WORKSPACE + "/structure.yml"); //$NON-NLS-1$
		newARGParameters.setOutput(Task_010_ARGParam_Variable.OLD_VAR_WORKSPACE + "/myOutput/"); //$NON-NLS-1$

		try {
			ARGParameters updated = getDaoManager().getRepository(IARGParametersRepository.class)
					.update(newARGParameters);
			assertEquals(Task_010_ARGParam_Variable.OLD_VAR_WORKSPACE + "/parameters.yml", //$NON-NLS-1$
					updated.getParametersFilePath());
			assertEquals(Task_010_ARGParam_Variable.OLD_VAR_WORKSPACE + "/structure.yml", //$NON-NLS-1$
					updated.getStructureFilePath());
			assertEquals(Task_010_ARGParam_Variable.OLD_VAR_WORKSPACE + "/myOutput/", updated.getOutput()); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// migration
		try {
			boolean changed = new Task_010_ARGParam_Variable().execute(getDaoManager());
			assertTrue(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}

		// search for ARG Param
		List<ARGParameters> found = getDaoManager().getRepository(IARGParametersRepository.class).findAll();
		assertEquals(1, found.size());
		ARGParameters param = found.get(0);
		assertNotNull(param);
		assertEquals(CFVariable.WORKSPACE.get() + "/parameters.yml", param.getParametersFilePath()); //$NON-NLS-1$
		assertEquals(CFVariable.WORKSPACE.get() + "/structure.yml", param.getStructureFilePath()); //$NON-NLS-1$
		assertEquals(CFVariable.WORKSPACE.get() + "/myOutput/", param.getOutput()); //$NON-NLS-1$
	}

	@Test
	void test_MigrationTask_AlreadyMigrated() {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			boolean changed = new Task_010_ARGParam_Variable().execute(getDaoManager());
			assertFalse(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}
}
