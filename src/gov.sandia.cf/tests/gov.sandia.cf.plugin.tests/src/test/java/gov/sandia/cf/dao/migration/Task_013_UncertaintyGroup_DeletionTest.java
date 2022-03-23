/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.hsqldb.cmdline.SqlToolError;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintyRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.dao.migration.tasks.Task_013_UncertaintyGroup_Deletion;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class Task_013_UncertaintyGroup_DeletionTest extends AbstractTestDao {

	private final static String CREATE_UNCERTAINTYGROUP_SCRIPT = "530-create_uncertaintygroup_to-migrate.sql"; //$NON-NLS-1$

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_013_UncertaintyGroup_DeletionTest.class);

	@Test
	void test_MigrationTask_NotMigrated() throws CoreException, CredibilityException, CredibilityMigrationException,
			SqlToolError, SQLException, IOException, URISyntaxException {

		// create data set
		TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewUser(getDaoManager(), User.UNKNOWN_USERID);
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);
		EclipseLinkMigrationManager.executeSQLScript(unitOfWork, CREATE_UNCERTAINTYGROUP_SCRIPT);

		assertTrue(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_PARAM_TABLE));
		assertTrue(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_SELECT_VALUE_TABLE));
		assertTrue(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_GROUP_TABLE));
		assertTrue(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_TABLE));
		assertTrue(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_VALUE_TABLE));

		// migration
		boolean changed = new Task_013_UncertaintyGroup_Deletion().execute(getDaoManager());
		assertTrue(changed);

		// validate
		assertFalse(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_PARAM_TABLE));
		assertFalse(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_SELECT_VALUE_TABLE));
		assertFalse(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_GROUP_TABLE));
		assertFalse(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_TABLE));
		assertFalse(EclipseLinkMigrationManager.existsTable(unitOfWork,
				Task_013_UncertaintyGroup_Deletion.OLD_UNCERTAINTY_VALUE_TABLE));

		// validate Uncertainty Parameters
		List<UncertaintyParam> findAllUncertaintyParams = getDaoManager()
				.getRepository(IUncertaintyParamRepository.class).findAll();
		assertEquals(9, findAllUncertaintyParams.size());

		// validate Uncertainty Select Values
		List<UncertaintySelectValue> findAllUncertaintySelectValues = getDaoManager()
				.getRepository(IUncertaintySelectValueRepository.class).findAll();
		assertEquals(10, findAllUncertaintySelectValues.size());
		assertEquals(3, findAllUncertaintyParams.stream().filter(p -> p.getId().equals(3)).findFirst().get()
				.getParameterValueList().size());
		assertEquals(4, findAllUncertaintyParams.stream().filter(p -> p.getId().equals(4)).findFirst().get()
				.getParameterValueList().size());
		assertEquals(3, findAllUncertaintyParams.stream().filter(p -> p.getId().equals(6)).findFirst().get()
				.getParameterValueList().size());

		// validate Uncertainties
		List<Uncertainty> findAllUncertainties = getDaoManager().getRepository(IUncertaintyRepository.class).findAll();
		assertEquals(6, findAllUncertainties.size());
		assertEquals(2,
				findAllUncertainties.stream().filter(u -> u.getParent() == null).collect(Collectors.toList()).size());
		assertEquals(4,
				findAllUncertainties.stream().filter(u -> u.getParent() != null).collect(Collectors.toList()).size());
		Optional<Uncertainty> uncertaintyId1 = findAllUncertainties.stream().filter(u -> u.getId().equals(1))
				.findFirst();
		Uncertainty parentId1 = uncertaintyId1.get().getParent();
		assertEquals(3, parentId1.getChildren().size());
		Optional<Uncertainty> uncertaintyId2 = findAllUncertainties.stream().filter(u -> u.getId().equals(2))
				.findFirst();
		Uncertainty parentId2 = uncertaintyId2.get().getParent();
		assertEquals(1, parentId2.getChildren().size());

		// validate Uncertainties "Name"
		assertEquals("Variable 1", uncertaintyId1.get().getName()); //$NON-NLS-1$

		// validate Uncertainties "Model"
		assertFalse(findAllUncertainties.stream().anyMatch(u -> u.getModel() == null));

		// validate Uncertainties "User Creation"
		assertFalse(findAllUncertainties.stream().anyMatch(u -> u.getUserCreation() == null));

		// validate Uncertainties "Date Creation"
		assertFalse(findAllUncertainties.stream().anyMatch(u -> u.getCreationDate() == null));
	}

	@Test
	void test_MigrationTask_AlreadyMigrated() throws CredibilityMigrationException {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		boolean changed = new Task_013_UncertaintyGroup_Deletion().execute(getDaoManager());
		assertFalse(changed);
	}
}
