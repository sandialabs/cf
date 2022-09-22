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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import gov.sandia.cf.model.dto.configuration.ParameterLinkGson;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the PCMMEvidenceRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
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
	void test_findBy_no_tag() {

		// No tag
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com"); //$NON-NLS-1$

		// Init filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, null);

		// Evidence with no tag
		List<PCMMEvidence> result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals("http://test.com", result.get(0).getPath()); //$NON-NLS-1$
		assertNull(result.get(0).getTag());

		// clean
		getRepository().delete(evidence);
	}

	@Test
	void test_findBy_tag() {

		// Tag and no role
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com", newTag); //$NON-NLS-1$

		// Init filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, newTag);

		// Evidence with tag
		List<PCMMEvidence> result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals(newTag, result.get(0).getTag());

		// clean
		getRepository().delete(evidence);
		getDaoManager().getRepository(ITagRepository.class).delete(newTag);
	}

	@Test
	void test_findBy_role_no_tag() {

		// Tag and no role
		Role newRole = TestEntityFactory.getNewRole(getDaoManager());
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), newRole, null, null,
				"http://test.com"); //$NON-NLS-1$

		// Init filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, null);
		filters.put(PCMMEvidence.Filter.ROLECREATION, newRole);

		// Evidence with tag
		List<PCMMEvidence> result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals(newRole, result.get(0).getRoleCreation());
		assertNull(result.get(0).getTag());

		// clean
		getRepository().delete(evidence);
		getDaoManager().getRepository(IRoleRepository.class).delete(newRole);
	}

	@Test
	void test_findBy_path_no_tag() {

		// Tag and no role
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com"); //$NON-NLS-1$

		// Init filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, null);
		filters.put(PCMMEvidence.Filter.VALUE, ParameterLinkGson.toGson(FormFieldType.LINK_URL, "http://test.com")); //$NON-NLS-1$

		// Evidence with tag
		List<PCMMEvidence> result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals("http://test.com", result.get(0).getPath()); //$NON-NLS-1$
		assertNull(result.get(0).getTag());

		// clean
		getRepository().delete(evidence);
	}

	@Test
	void test_findBy_path_and_tag() {

		// Tag and path
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com", newTag); //$NON-NLS-1$

		// Init filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, newTag);
		filters.put(PCMMEvidence.Filter.VALUE, ParameterLinkGson.toGson(FormFieldType.LINK_URL, "http://test.com")); //$NON-NLS-1$

		// Evidence with tag
		List<PCMMEvidence> result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals("http://test.com", result.get(0).getPath()); //$NON-NLS-1$
		assertEquals(newTag, result.get(0).getTag());

		// clean
		getRepository().delete(evidence);
		getDaoManager().getRepository(ITagRepository.class).delete(newTag);
	}

	@Test
	void test_findBy_role_and_tag() {

		// Tag and no role
		Tag newTag = TestEntityFactory.getNewTag(getDaoManager(), null);
		Role newRole = TestEntityFactory.getNewRole(getDaoManager());
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), newRole, null, null,
				"http://test.com", newTag); //$NON-NLS-1$

		// Init filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.TAG, newTag);
		filters.put(PCMMEvidence.Filter.ROLECREATION, newRole);

		// Evidence with tag
		List<PCMMEvidence> result = getRepository().findBy(filters);
		assertEquals(Integer.valueOf(1), Integer.valueOf(result.size()));
		assertEquals(newRole, result.get(0).getRoleCreation());
		assertEquals(newTag, result.get(0).getTag());

		// clean
		getRepository().delete(evidence);
		getDaoManager().getRepository(ITagRepository.class).delete(newTag);
		getDaoManager().getRepository(IRoleRepository.class).delete(newRole);
	}

	@Test
	void test_findBy_null() {

		// list
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com"); //$NON-NLS-1$
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com"); //$NON-NLS-1$

		// No filter (null)
		List<PCMMEvidence> result = getRepository().findBy(null);
		assertEquals(Integer.valueOf(2), Integer.valueOf(result.size()));

		// No filter (empty)
		result = getRepository().findBy(new HashMap<>());
		assertEquals(Integer.valueOf(2), Integer.valueOf(result.size()));

		// Clear
		getRepository().delete(evidence);
		getRepository().delete(evidence2);
	}

	@Test
	void test_findBy_empty() {

		// list
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com"); //$NON-NLS-1$
		PCMMEvidence evidence2 = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), null, null, null,
				"http://test.com"); //$NON-NLS-1$

		// No filter (null)
		List<PCMMEvidence> result = getRepository().findBy(new HashMap<>());
		assertEquals(Integer.valueOf(2), Integer.valueOf(result.size()));

		// Clear
		getRepository().delete(evidence);
		getRepository().delete(evidence2);
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
