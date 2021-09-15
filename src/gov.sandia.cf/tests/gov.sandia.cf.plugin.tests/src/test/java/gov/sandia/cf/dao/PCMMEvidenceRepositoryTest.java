/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.ParameterLinkGson;
import gov.sandia.cf.dao.impl.PCMMEvidenceRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.FormFieldType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMEvidenceRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMEvidenceRepositoryTest extends AbstractTestRepository<PCMMEvidence, Integer, PCMMEvidenceRepository> {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(PCMMEvidenceRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMEvidenceRepository> getRepositoryClass() {
		return PCMMEvidenceRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMEvidence> getModelClass() {
		return PCMMEvidence.class;
	}

	@Override
	PCMMEvidence getModelFulfilled(PCMMEvidence model) {
		// populate PCMM
		Role newRole = TestEntityFactory.getNewRole(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		fulfillModelStrings(model);
		model.setDateCreation(new Date());
		model.setRoleCreation(newRole);
		model.setUserCreation(newUser);
		return model;
	}

	@Test
	void testFindAllActive() {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create evidence
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setDateCreation(new Date());
		evidence.setName("Name"); //$NON-NLS-1$
		evidence.setFilePath("/Paht/fff"); //$NON-NLS-1$
		evidence.setRoleCreation(defaultRole);
		evidence.setUserCreation(defaultUser);
		PCMMEvidence addedEvidence = null;
		try {
			addedEvidence = getRepository().create(evidence);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		List<PCMMEvidence> foundList = getRepository().findAllActive();
		assertNotNull(foundList);
		assertTrue(!foundList.isEmpty());

		for (PCMMEvidence found : foundList) {
			assertNotNull(found);
			assertNotNull(found.getId());
			assertNull(found.getTag());
		}

		// Clear
		if (addedEvidence != null) {
			getRepository().delete(addedEvidence);
		}
	}

	@Test
	void testFindBySubelement() {

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

		// create evidence
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setDateCreation(new Date());
		evidence.setName("Name"); //$NON-NLS-1$
		evidence.setFilePath("/Paht/fff"); //$NON-NLS-1$
		evidence.setRoleCreation(defaultRole);
		evidence.setUserCreation(defaultUser);
		evidence.setSubelement(createdSubelement);
		PCMMEvidence addedEvidence = null;
		try {
			addedEvidence = getRepository().create(evidence);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Clear
		if (addedEvidence != null) {
			getRepository().delete(addedEvidence);
		}
	}

	@Test
	void testFindByTagTagNotNull() {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create evidence
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setTag(createdTag);
		evidence.setDateCreation(new Date());
		evidence.setName("Name"); //$NON-NLS-1$
		evidence.setFilePath("/Paht/fff"); //$NON-NLS-1$
		evidence.setRoleCreation(defaultRole);
		evidence.setUserCreation(defaultUser);
		PCMMEvidence addedEvidence = null;
		try {
			addedEvidence = getRepository().create(evidence);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		List<PCMMEvidence> foundList = getRepository().findByTag(createdTag);
		assertNotNull(foundList);
		assertEquals(1, foundList.size());

		PCMMEvidence found = foundList.get(0);
		assertNotNull(found);
		assertNotNull(found.getId());
		assertEquals(createdTag, found.getTag());

		// Clear
		if (addedEvidence != null) {
			getRepository().delete(addedEvidence);
		}
	}

	@Test
	void testFindByTagTagNull() {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create evidence
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setDateCreation(new Date());
		evidence.setName("Name"); //$NON-NLS-1$
		evidence.setFilePath("/Paht/fff"); //$NON-NLS-1$
		evidence.setRoleCreation(defaultRole);
		evidence.setUserCreation(defaultUser);
		PCMMEvidence addedEvidence = null;
		try {
			addedEvidence = getRepository().create(evidence);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		List<PCMMEvidence> foundList = getRepository().findByTag(null);
		assertNotNull(foundList);
		assertTrue(!foundList.isEmpty());

		for (PCMMEvidence found : foundList) {
			assertNotNull(found);
			assertNotNull(found.getId());
			assertNull(found.getTag());
		}

		// Clear
		if (addedEvidence != null) {
			getRepository().delete(addedEvidence);
		}
	}

	@Test
	void testFindBy() {

		String pathEvidence = "/My/path/text.txt"; //$NON-NLS-1$

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create tag
		Tag evidenceTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(evidenceTag);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// No tag and no role
		PCMMEvidence evidence = new PCMMEvidence();
		evidence.setName("A"); //$NON-NLS-1$
		evidence.setFilePath(pathEvidence);
		evidence.setDateCreation(new Date());
		evidence.setRoleCreation(defaultRole);
		evidence.setUserCreation(defaultUser);
		try {
			evidence = getRepository().create(evidence);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// With tag and no role
		PCMMEvidence evidenceTagAndRole = new PCMMEvidence();
		evidenceTagAndRole.setName("B"); //$NON-NLS-1$
		evidenceTagAndRole.setTag(evidenceTag);
		evidenceTagAndRole.setRoleCreation(defaultRole);
		evidenceTagAndRole.setDateCreation(new Date());
		evidenceTagAndRole.setFilePath("/Paht/fff"); //$NON-NLS-1$
		evidenceTagAndRole.setUserCreation(defaultUser);
		try {
			evidenceTagAndRole = getRepository().create(evidenceTagAndRole);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Init filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, null);

		// Evidence with not flag
		List<PCMMEvidence> result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals("A", result.get(0).getName()); //$NON-NLS-1$

		// Evidence with flag
		filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, evidenceTag);
		result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals("B", result.get(0).getName()); //$NON-NLS-1$

		// Evidence no tag
		filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, null);
		result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals("A", result.get(0).getName()); //$NON-NLS-1$

		// Evidence role and no tag
		filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, null);
		filters.put(PCMMEvidence.Filter.ROLECREATION, defaultRole);
		result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));

		// Evidence no tag and path
		filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, null);
		filters.put(PCMMEvidence.Filter.VALUE, ParameterLinkGson.toGson(FormFieldType.LINK_FILE, pathEvidence));
		result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));

		// Evidence role and tag
		filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, evidenceTag);
		filters.put(PCMMEvidence.Filter.ROLECREATION, defaultRole);
		result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals("B", result.get(0).getName()); //$NON-NLS-1$

		// No filter (null)
		result = getRepository().findBy(null);
		assertEquals(Integer.valueOf(2), Integer.valueOf(result.size()));

		// No filter (empty
		result = getRepository().findBy(new HashMap<>());
		assertEquals(Integer.valueOf(2), Integer.valueOf(result.size()));

		// Clear
		if (evidence != null) {
			getRepository().delete(evidence);
		}
		if (evidenceTagAndRole != null) {
			getRepository().delete(evidenceTagAndRole);
		}
	}

	@Test
	void testClearEvidencePath_File() {

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
		PCMMEvidence evidence1 = new PCMMEvidence();
		evidence1.setName("A"); //$NON-NLS-1$
		evidence1.setFilePath("\\Test\\Test\\repo\\test"); //$NON-NLS-1$
		evidence1.setDateCreation(new Date());
		evidence1.setRoleCreation(defaultRole);
		evidence1.setUserCreation(defaultUser);
		try {
			evidence1 = getRepository().create(evidence1);
			assertNotNull(evidence1);
			assertNotNull(evidence1.getId());
			assertEquals("A", evidence1.getName()); //$NON-NLS-1$
			assertEquals(FormFieldType.LINK_FILE, evidence1.getType());
			assertEquals("\\Test\\Test\\repo\\test", evidence1.getPath()); //$NON-NLS-1$
		} catch (

		CredibilityException e) {
			fail(e.getMessage());
		}

		// With tag and no role
		PCMMEvidence evidence2 = new PCMMEvidence();
		evidence2.setName("B"); //$NON-NLS-1$
		evidence2.setFilePath("/Test/Test/repo/test"); //$NON-NLS-1$
		evidence2.setDateCreation(new Date());
		evidence2.setRoleCreation(defaultRole);
		evidence2.setUserCreation(defaultUser);
		try {
			evidence2 = getRepository().create(evidence2);
			assertNotNull(evidence2);
			assertNotNull(evidence2.getId());
			assertEquals("B", evidence2.getName()); //$NON-NLS-1$
			assertEquals(FormFieldType.LINK_FILE, evidence1.getType());
			assertEquals("/Test/Test/repo/test", evidence2.getPath()); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		boolean clearEvidencePath = getRepository().clearEvidencePath();

		assertTrue(clearEvidencePath);

		PCMMEvidence found1 = getRepository().findById(evidence1.getId());
		assertNotNull(found1);
		assertEquals("/Test/Test/repo/test", found1.getPath()); //$NON-NLS-1$
		PCMMEvidence found2 = getRepository().findById(evidence2.getId());
		assertNotNull(found2);
		assertEquals("/Test/Test/repo/test", found2.getPath()); //$NON-NLS-1$

		clearEvidencePath = getRepository().clearEvidencePath();
		assertFalse(clearEvidencePath);

		getRepository().delete(found1);
		getRepository().delete(found2);
	}

	@Test
	void testClearEvidencePath_URL() {

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
		PCMMEvidence evidence1 = new PCMMEvidence();
		evidence1.setName("A"); //$NON-NLS-1$
		evidence1.setURL("https://myexemple.fr\\"); //$NON-NLS-1$
		evidence1.setDateCreation(new Date());
		evidence1.setRoleCreation(defaultRole);
		evidence1.setUserCreation(defaultUser);
		try {
			evidence1 = getRepository().create(evidence1);
			assertNotNull(evidence1);
			assertNotNull(evidence1.getId());
			assertEquals("A", evidence1.getName()); //$NON-NLS-1$
			assertEquals(FormFieldType.LINK_URL, evidence1.getType());
			assertEquals("https://myexemple.fr\\", evidence1.getPath()); //$NON-NLS-1$
		} catch (

		CredibilityException e) {
			fail(e.getMessage());
		}

		// With tag and no role
		PCMMEvidence evidence2 = new PCMMEvidence();
		evidence2.setName("B"); //$NON-NLS-1$
		evidence2.setURL("https://myexemple.fr"); //$NON-NLS-1$
		evidence2.setDateCreation(new Date());
		evidence2.setRoleCreation(defaultRole);
		evidence2.setUserCreation(defaultUser);
		try {
			evidence2 = getRepository().create(evidence2);
			assertNotNull(evidence2);
			assertNotNull(evidence2.getId());
			assertEquals("B", evidence2.getName()); //$NON-NLS-1$
			assertEquals(FormFieldType.LINK_URL, evidence2.getType());
			assertEquals("https://myexemple.fr", evidence2.getPath()); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		boolean clearEvidencePath = getRepository().clearEvidencePath();

		assertTrue(clearEvidencePath);

		PCMMEvidence found1 = getRepository().findById(evidence1.getId());
		assertNotNull(found1);
		assertEquals("https://myexemple.fr/", found1.getPath()); //$NON-NLS-1$
		PCMMEvidence found2 = getRepository().findById(evidence2.getId());
		assertNotNull(found2);
		assertEquals("https://myexemple.fr", found2.getPath()); //$NON-NLS-1$

		clearEvidencePath = getRepository().clearEvidencePath();
		assertFalse(clearEvidencePath);

		getRepository().delete(found1);
		getRepository().delete(found2);
	}
}
