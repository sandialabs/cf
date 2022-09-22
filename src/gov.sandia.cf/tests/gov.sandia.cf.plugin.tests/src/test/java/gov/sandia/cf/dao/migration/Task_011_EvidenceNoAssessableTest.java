/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.dao.migration.tasks.Task_011_EvidenceNoAssessable;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * The Class Task_011_EvidenceNoAssessableTest.
 *
 * @author Didier Verstraete
 */
class Task_011_EvidenceNoAssessableTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_011_EvidenceNoAssessableTest.class);

	@Test
	void test_MigrationTask_NotMigrated() throws CoreException, CredibilityException, CredibilityMigrationException {

		// Needed to execute a migration task
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		User user = TestEntityFactory.getNewUser(getDaoManager());
		Role role = TestEntityFactory.getNewRole(getDaoManager());

		PCMMElement pcmmElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMSubelement pcmmSubelement1 = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), pcmmElement);
		PCMMSubelement pcmmSubelement2 = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), pcmmElement);

		IFile newFile1 = TestEntityFactory.getNewFile();
		PCMMEvidence newPCMMEvidence1 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), role, user,
				pcmmSubelement1, newFile1);

		PCMMEvidence newPCMMEvidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), role, user,
				pcmmSubelement2);
		newPCMMEvidence2.setURL("http://google.fr"); //$NON-NLS-1$
		getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(newPCMMEvidence2);

		// bad evidence to be deleted
		PCMMEvidence evidenceToDelete1 = new PCMMEvidence();
		evidenceToDelete1.setURL(newPCMMEvidence1.getValue());
		evidenceToDelete1.setUserCreation(user);
		evidenceToDelete1.setRoleCreation(role);
		evidenceToDelete1.setDateCreation(new Date());
		PCMMEvidence evidenceDeleted1 = getDaoManager().getRepository(IPCMMEvidenceRepository.class)
				.create(evidenceToDelete1);

		PCMMEvidence evidenceToDelete2 = new PCMMEvidence();
		evidenceToDelete2.setURL(newPCMMEvidence2.getValue());
		evidenceToDelete2.setUserCreation(user);
		evidenceToDelete2.setRoleCreation(role);
		evidenceToDelete2.setDateCreation(new Date());
		PCMMEvidence evidenceDeleted2 = getDaoManager().getRepository(IPCMMEvidenceRepository.class)
				.create(evidenceToDelete2);

		// search for evidence
		List<PCMMEvidence> found = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findAll();
		assertEquals(4, found.size());
		// search for evidence with value not null
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.ELEMENT, NullParameter.NULL);
		filters.put(PCMMEvidence.Filter.SUBELEMENT, NullParameter.NULL);
		found = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findBy(filters);
		assertEquals(2, found.size());

		// migration
		boolean changed = new Task_011_EvidenceNoAssessable().execute(getDaoManager());
		assertTrue(changed);

		// search for evidence
		found = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findAll();
		assertEquals(2, found.size());
		// search for evidence with value not null
		filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.ELEMENT, NullParameter.NULL);
		filters.put(PCMMEvidence.Filter.SUBELEMENT, NullParameter.NULL);
		found = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findBy(filters);
		assertEquals(0, found.size());
		assertFalse(found.contains(evidenceDeleted1));
		assertFalse(found.contains(evidenceDeleted2));

		// clear
		newFile1.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_MigrationTask_AlreadyMigrated() throws CredibilityMigrationException {

		// initialize data
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement pcmmElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMSubelement pcmmSubelement1 = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), pcmmElement);
		PCMMSubelement pcmmSubelement2 = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), pcmmElement);

		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, pcmmElement);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, pcmmSubelement1);
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, pcmmSubelement2);

		// test
		boolean changed = new Task_011_EvidenceNoAssessable().execute(getDaoManager());
		assertFalse(changed);
	}
}
