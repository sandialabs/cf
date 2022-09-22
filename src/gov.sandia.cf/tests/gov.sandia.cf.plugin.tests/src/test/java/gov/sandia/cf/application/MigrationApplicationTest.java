/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.migration.IMigrationApplication;
import gov.sandia.cf.dao.IPCMMAssessmentRepository;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit test class for the Migration Application Controller
 */
class MigrationApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(MigrationApplicationTest.class);

	/*******************************************
	 * TEST: clearMulipleAssessment
	 ********************************************/

	@Test
	void test_clearMulipleAssessment_PCMMSpecsNull() {
		boolean wasDirty = getAppManager().getService(IMigrationApplication.class).clearMultipleAssessment(null);
		assertFalse(wasDirty);
	}

	@Test
	void test_clearMulipleAssessment_PCMMModeDEFAULT() {

		// create PCMMSpecification - DEFAULT
		PCMMSpecification pcmmSpecs = new PCMMSpecification();
		pcmmSpecs.setMode(PCMMMode.DEFAULT);

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create element
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(createdElement);

		// create subelement
		PCMMSubelement createdSubelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), createdElement);
		assertNotNull(createdSubelement);
		assertEquals(createdElement, createdSubelement.getElement());

		// create assessment
		PCMMAssessment assessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), defaultRole, defaultUser,
				createdSubelement, null);

		// create assessment
		PCMMAssessment assessment2 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), defaultRole, defaultUser,
				createdSubelement, null);

		assertNotEquals(assessment.getId(), assessment2.getId());
		assertEquals(assessment.getRoleCreation(), assessment2.getRoleCreation());
		assertEquals(assessment.getSubelement(), assessment2.getSubelement());
		assertEquals(assessment.getUserCreation(), assessment2.getUserCreation());
		assertEquals(assessment.getTag(), assessment2.getTag());

		// check assessment non-unicity
		List<PCMMAssessment> assessments = getDaoManager().getRepository(IPCMMAssessmentRepository.class)
				.findByRoleAndUserAndSubeltAndTag(defaultRole, defaultUser, createdSubelement, null);
		assertNotNull(assessments);
		assertEquals(2, assessments.size());

		// check multiple assessment
		boolean wasDirty = getAppManager().getService(IMigrationApplication.class).clearMultipleAssessment(pcmmSpecs);
		assertTrue(wasDirty);

		// check assessment unicity
		assessments = getDaoManager().getRepository(IPCMMAssessmentRepository.class)
				.findByRoleAndUserAndSubeltAndTag(defaultRole, defaultUser, createdSubelement, null);
		assertNotNull(assessments);
		assertEquals(1, assessments.size());
	}

	@Test
	void test_clearMulipleAssessment_PCMMModeSIMPLIFIED() {

		// create PCMMSpecification - DEFAULT
		PCMMSpecification pcmmSpecs = new PCMMSpecification();
		pcmmSpecs.setMode(PCMMMode.SIMPLIFIED);

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create element
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(createdElement);

		// create assessment
		PCMMAssessment assessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), defaultRole, defaultUser,
				createdElement, null);

		// create assessment
		PCMMAssessment assessment2 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), defaultRole, defaultUser,
				createdElement, null);

		assertNotEquals(assessment.getId(), assessment2.getId());
		assertEquals(assessment.getRoleCreation(), assessment2.getRoleCreation());
		assertEquals(assessment.getSubelement(), assessment2.getSubelement());
		assertEquals(assessment.getUserCreation(), assessment2.getUserCreation());
		assertEquals(assessment.getTag(), assessment2.getTag());

		// check assessment non-unicity
		List<PCMMAssessment> assessments = getDaoManager().getRepository(IPCMMAssessmentRepository.class)
				.findByRoleAndUserAndEltAndTag(defaultRole, defaultUser, createdElement, null);
		assertNotNull(assessments);
		assertEquals(2, assessments.size());

		// check multiple assessment
		boolean wasDirty = getAppManager().getService(IMigrationApplication.class).clearMultipleAssessment(pcmmSpecs);
		assertTrue(wasDirty);

		// check assessment unicity
		assessments = getDaoManager().getRepository(IPCMMAssessmentRepository.class)
				.findByRoleAndUserAndEltAndTag(defaultRole, defaultUser, createdElement, null);
		assertNotNull(assessments);
		assertEquals(1, assessments.size());
	}

	/*******************************************
	 * TEST: clearEvidencePath
	 ********************************************/

	@Test
	void test_clearEvidencePath() {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// evidence 1
		PCMMEvidence evidence1 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), defaultRole, defaultUser, null);
		evidence1.setName("A"); //$NON-NLS-1$
		evidence1.setFilePath("\\Test\\Test\\repo\\test"); //$NON-NLS-1$
		try {
			evidence1 = getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(evidence1);
			assertNotNull(evidence1);
			assertNotNull(evidence1.getId());
			assertEquals("A", evidence1.getName()); //$NON-NLS-1$
			assertEquals("\\Test\\Test\\repo\\test", evidence1.getPath()); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// With tag and no role
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), defaultRole, defaultUser, null);
		evidence2.setName("B"); //$NON-NLS-1$ <
		evidence2.setFilePath("/Test/Test/repo/test"); //$NON-NLS-1$
		try {
			evidence2 = getDaoManager().getRepository(IPCMMEvidenceRepository.class).update(evidence2);
			assertNotNull(evidence2);
			assertNotNull(evidence2.getId());
			assertEquals("B", evidence2.getName()); //$NON-NLS-1$
			assertEquals("/Test/Test/repo/test", evidence2.getPath()); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		boolean clearEvidencePath = getAppManager().getService(IMigrationApplication.class).clearEvidencePath();
		assertTrue(clearEvidencePath);

		PCMMEvidence found1 = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findById(evidence1.getId());
		assertNotNull(found1);
		assertEquals("/Test/Test/repo/test", found1.getPath()); //$NON-NLS-1$
		PCMMEvidence found2 = getDaoManager().getRepository(IPCMMEvidenceRepository.class).findById(evidence2.getId());
		assertNotNull(found2);
		assertEquals("/Test/Test/repo/test", found2.getPath()); //$NON-NLS-1$

		clearEvidencePath = getDaoManager().getRepository(IPCMMEvidenceRepository.class).clearEvidencePath();
		assertFalse(clearEvidencePath);

		getDaoManager().getRepository(IPCMMEvidenceRepository.class).delete(found1);
		getDaoManager().getRepository(IPCMMEvidenceRepository.class).delete(found2);
	}

}
