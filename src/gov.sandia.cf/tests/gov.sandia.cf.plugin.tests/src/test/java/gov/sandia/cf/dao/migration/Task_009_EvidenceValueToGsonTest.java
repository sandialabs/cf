/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.dao.migration.tasks.Task_009_EvidenceValueToGson;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.FormFieldType;
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
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class Task_009_EvidenceValueToGsonTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_009_EvidenceValueToGsonTest.class);

	@Test
	void test_MigrationTask_NotMigrated() throws CoreException, CredibilityException, CredibilityMigrationException {

		// Needed to execute a migration task
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		User user = TestEntityFactory.getNewUser(getDaoManager());
		Role role = TestEntityFactory.getNewRole(getDaoManager());

		PCMMElement pcmmElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), model);
		PCMMSubelement pcmmSubelement1 = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), pcmmElement);
		PCMMSubelement pcmmSubelement2 = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), pcmmElement);

		IFile newFile = TestEntityFactory.getNewFile();
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), role, user, pcmmElement, newFile);
		IFile newFile1 = TestEntityFactory.getNewFile();
		TestEntityFactory.getNewPCMMEvidence(getDaoManager(), role, user, pcmmSubelement1, newFile1);
		PCMMEvidence newPCMMEvidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), role, user,
				pcmmSubelement2);
		newPCMMEvidence.setURL("http://google.fr"); //$NON-NLS-1$
		getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(newPCMMEvidence);

		// unit of work
		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);

		// alter table - inject columns to be deleted and migrated
		String sqlUpdate = MessageFormat.format("ALTER TABLE {0} ADD {1} VARCHAR(256);", //$NON-NLS-1$
				Task_009_EvidenceValueToGson.EVIDENCE_TABLE, "{0}"); //$NON-NLS-1$

		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, Task_009_EvidenceValueToGson.EVIDENCE_TYPE_COLUMN));
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, Task_009_EvidenceValueToGson.EVIDENCE_PATH_COLUMN));

		// alter data
		unitOfWork.executeNonSelectingSQL("UPDATE PCMMEVIDENCE SET PATH=VALUE;"); //$NON-NLS-1$
		unitOfWork.executeNonSelectingSQL(
				"UPDATE PCMMEVIDENCE SET TYPE='file' WHERE ID <> '" + newPCMMEvidence.getId() + "';"); //$NON-NLS-1$ //$NON-NLS-2$
		unitOfWork.executeNonSelectingSQL(
				"UPDATE PCMMEVIDENCE SET TYPE='url' WHERE ID = '" + newPCMMEvidence.getId() + "';"); //$NON-NLS-1$ //$NON-NLS-2$
		unitOfWork.executeNonSelectingSQL("UPDATE PCMMEVIDENCE SET VALUE=null;"); //$NON-NLS-1$

		// search for evidence
		List<PCMMEvidence> found = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findAll();
		assertEquals(3, found.size());
		// search for evidence with value not null
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.VALUE, NullParameter.NOT_NULL);
		found = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findBy(filters);
		assertEquals(0, found.size());

		// migration
		boolean changed = new Task_009_EvidenceValueToGson().execute(getDaoManager());
		assertTrue(changed);

		// search for evidence
		found = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findAll();
		assertEquals(3, found.size());
		// search for evidence with value not null
		filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.VALUE, NullParameter.NOT_NULL);
		found = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findBy(filters);
		assertEquals(3, found.size());

		assertEquals(2, found.stream().filter(e -> FormFieldType.LINK_FILE.equals(e.getType())).count());
		assertEquals(1, found.stream().filter(e -> FormFieldType.LINK_URL.equals(e.getType())).count());
		assertEquals(1, found.stream().filter(e -> "http://google.fr".equals(e.getPath())).count()); //$NON-NLS-1$
		assertEquals(1, found.stream()
				.filter(e -> "{\"type\":\"LINK_URL\",\"value\":\"http://google.fr\"}".equals(e.getValue())).count()); //$NON-NLS-1$
		assertEquals(2,
				found.stream().filter(e -> e.getValue().startsWith("{\"type\":\"LINK_FILE\",\"value\":")).count()); //$NON-NLS-1$

		// check column existence
		assertFalse(EclipseLinkMigrationManager.existsColumnInTable(unitOfWork,
				Task_009_EvidenceValueToGson.EVIDENCE_TABLE, Task_009_EvidenceValueToGson.EVIDENCE_TYPE_COLUMN));
		assertFalse(EclipseLinkMigrationManager.existsColumnInTable(unitOfWork,
				Task_009_EvidenceValueToGson.EVIDENCE_TABLE, Task_009_EvidenceValueToGson.EVIDENCE_PATH_COLUMN));

		// clear
		newFile.getProject().delete(true, new NullProgressMonitor());
		newFile1.getProject().delete(true, new NullProgressMonitor());
	}

	@Test
	void test_MigrationTask_AlreadyMigrated() throws CredibilityMigrationException {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		boolean changed = new Task_009_EvidenceValueToGson().execute(getDaoManager());
		assertFalse(changed);
	}
}
