/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
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

import gov.sandia.cf.dao.impl.PCMMAssessmentRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the PCMMAssessmentRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class PCMMAssessmentRepositoryTest extends AbstractTestRepository<PCMMAssessment, Integer, PCMMAssessmentRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMAssessmentRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMAssessmentRepository> getRepositoryClass() {
		return PCMMAssessmentRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMAssessment> getModelClass() {
		return PCMMAssessment.class;
	}

	@Override
	PCMMAssessment getModelFulfilled(PCMMAssessment model) {
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

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findAllActive();
		assertNotNull(findByElementAndTag);
		assertTrue(!findByElementAndTag.isEmpty());

		for (PCMMAssessment pcmmAssessment : findByElementAndTag) {
			assertNotNull(pcmmAssessment);
			assertNotNull(pcmmAssessment.getId());
			assertEquals(null, pcmmAssessment.getTag());
		}
	}

	@Test
	void testFindBy_Element() {

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

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create assessment not tagged
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setElement(createdElement);
		assessment.setTag(null);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create assessment tagged
		PCMMAssessment assessmentTagged = new PCMMAssessment();
		assessmentTagged.setElement(createdElement);
		assessmentTagged.setTag(createdTag);
		assessmentTagged.setDateCreation(new Date());
		assessmentTagged.setRoleCreation(defaultRole);
		assessmentTagged.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessmentTagged = getRepository().create(assessmentTagged);
			assertNotNull(addedAssessmentTagged);
			assertNotNull(addedAssessmentTagged.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.ELEMENT, createdElement);
		List<PCMMAssessment> findByElement = getRepository().findBy(filters);
		assertNotNull(findByElement);
		assertEquals(2, findByElement.size());

	}

	@Test
	void testFindBy_ElementAndTag_TagNull() {

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
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setElement(createdElement);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.ELEMENT, createdElement);
		filters.put(PCMMAssessment.Filter.TAG, null);
		List<PCMMAssessment> findByElementAndTag = getRepository().findBy(filters);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdElement, pcmmAssessment.getElement());
		assertEquals(null, pcmmAssessment.getTag());

	}

	@Test
	void testFindBy_ElementAndTag_TagNotNull() {

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

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setElement(createdElement);
		assessment.setTag(createdTag);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.ELEMENT, createdElement);
		filters.put(PCMMAssessment.Filter.TAG, createdTag);
		List<PCMMAssessment> findByElementAndTag = getRepository().findBy(filters);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdElement, pcmmAssessment.getElement());
		assertEquals(createdTag, pcmmAssessment.getTag());
		assertEquals(createdTag, pcmmAssessment.getTag());

	}

	@Test
	void testFindBy_ElementAndTag_EltNull() {
		// find by element and tag
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.ELEMENT, null);
		filters.put(PCMMAssessment.Filter.TAG, new Tag());
		List<PCMMAssessment> findByElementAndTag = getRepository().findBy(filters);
		assertNotNull(findByElementAndTag);
		assertTrue(findByElementAndTag.isEmpty());
	}

	@Test
	void testFindByElementAndTagInSubelement_TagNotNull() {

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

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setSubelement(createdSubelement);
		assessment.setTag(createdTag);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findByElementAndTagInSubelement(createdElement,
				createdTag);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdSubelement, pcmmAssessment.getSubelement());
		assertEquals(createdTag, pcmmAssessment.getTag());
	}

	@Test
	void testFindByElementAndTagInSubelement_TagNull() {

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
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setSubelement(createdSubelement);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findByElementAndTagInSubelement(createdElement,
				null);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdSubelement, pcmmAssessment.getSubelement());
		assertEquals(null, pcmmAssessment.getTag());
	}

	@Test
	void testFindByElementAndTagInSubelement_EltNull() {
		// find by element and tag
		List<PCMMAssessment> findByElementAndTag = getRepository().findByElementAndTagInSubelement(null, new Tag());
		assertNotNull(findByElementAndTag);
		assertTrue(findByElementAndTag.isEmpty());
	}

	@Test
	void testFindBy_SubelementAndTag_TagNotNull() {

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

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setSubelement(createdSubelement);
		assessment.setTag(createdTag);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.SUBELEMENT, createdSubelement);
		filters.put(PCMMAssessment.Filter.TAG, createdTag);
		List<PCMMAssessment> findByElementAndTag = getRepository().findBy(filters);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdSubelement, pcmmAssessment.getSubelement());
		assertEquals(createdTag, pcmmAssessment.getTag());
	}

	@Test
	void testFindBy_Subelement() {

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

		// create assessment not tagged
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setSubelement(createdSubelement);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create assessment tagged
		PCMMAssessment assessmentTagged = new PCMMAssessment();
		assessmentTagged.setSubelement(createdSubelement);
		assessmentTagged.setDateCreation(new Date());
		assessmentTagged.setRoleCreation(defaultRole);
		assessmentTagged.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessmentTagged = getRepository().create(assessmentTagged);
			assertNotNull(addedAssessmentTagged);
			assertNotNull(addedAssessmentTagged.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by subelement
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.SUBELEMENT, createdSubelement);
		List<PCMMAssessment> findByElementAndTag = getRepository().findBy(filters);
		assertNotNull(findByElementAndTag);
		assertEquals(2, findByElementAndTag.size());
	}

	@Test
	void testFindBy_SubelementAndTag_TagNull() {

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
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setSubelement(createdSubelement);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.SUBELEMENT, createdSubelement);
		filters.put(PCMMAssessment.Filter.TAG, null);
		List<PCMMAssessment> findByElementAndTag = getRepository().findBy(filters);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdSubelement, pcmmAssessment.getSubelement());
		assertEquals(null, pcmmAssessment.getTag());
	}

	@Test
	void testFindBy_SubelementAndTag_SubeltNull() {
		// find by element and tag
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.SUBELEMENT, null);
		filters.put(PCMMAssessment.Filter.TAG, new Tag());
		List<PCMMAssessment> findBySubelementAndTag = getRepository().findBy(filters);
		assertNotNull(findBySubelementAndTag);
		assertTrue(findBySubelementAndTag.isEmpty());
	}

	@Test
	void testFindByRoleAndUserAndEltAndTag_TagNull() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create element
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(createdElement);

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setElement(createdElement);
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		assessment.setDateCreation(new Date());
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findByRoleAndUserAndEltAndTag(defaultRole,
				defaultUser, createdElement, null);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdElement, pcmmAssessment.getElement());
		assertEquals(defaultRole, pcmmAssessment.getRoleCreation());
		assertEquals(defaultUser, pcmmAssessment.getUserCreation());
		assertEquals(null, pcmmAssessment.getTag());
	}

	@Test
	void testFindByRoleAndUserAndEltAndTag_TagNotNull() {

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

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setElement(createdElement);
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		assessment.setTag(createdTag);
		assessment.setDateCreation(new Date());
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findByRoleAndUserAndEltAndTag(defaultRole,
				defaultUser, createdElement, createdTag);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdElement, pcmmAssessment.getElement());
		assertEquals(defaultRole, pcmmAssessment.getRoleCreation());
		assertEquals(defaultUser, pcmmAssessment.getUserCreation());
		assertEquals(createdTag, pcmmAssessment.getTag());
	}

	@Test
	void testFindByRoleAndUserAndEltAndTag_EltNull() {
		List<PCMMAssessment> findBySubelementAndTag = getRepository().findByRoleAndUserAndEltAndTag(new Role(),
				new User(), null, new Tag());
		assertNotNull(findBySubelementAndTag);
		assertTrue(findBySubelementAndTag.isEmpty());
	}

	@Test
	void testFindByRoleAndUserAndEltAndTag_RoleNull() {
		List<PCMMAssessment> findBySubelementAndTag = getRepository().findByRoleAndUserAndEltAndTag(null, new User(),
				new PCMMElement(), new Tag());
		assertNotNull(findBySubelementAndTag);
		assertTrue(findBySubelementAndTag.isEmpty());
	}

	@Test
	void testFindByRoleAndUserAndEltAndTag_UserNull() {
		List<PCMMAssessment> findBySubelementAndTag = getRepository().findByRoleAndUserAndEltAndTag(new Role(), null,
				new PCMMElement(), new Tag());
		assertNotNull(findBySubelementAndTag);
		assertTrue(findBySubelementAndTag.isEmpty());
	}

	@Test
	void testFindByRoleAndUserAndSubeltAndTag_TagNull() {

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
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setSubelement(createdSubelement);
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		assessment.setDateCreation(new Date());
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findByRoleAndUserAndSubeltAndTag(defaultRole,
				defaultUser, createdSubelement, null);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdSubelement, pcmmAssessment.getSubelement());
		assertEquals(defaultRole, pcmmAssessment.getRoleCreation());
		assertEquals(defaultUser, pcmmAssessment.getUserCreation());
		assertEquals(null, pcmmAssessment.getTag());
	}

	@Test
	void testFindByRoleAndUserAndSubeltAndTag_TagNotNull() {

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

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setSubelement(createdSubelement);
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		assessment.setTag(createdTag);
		assessment.setDateCreation(new Date());
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findByRoleAndUserAndSubeltAndTag(defaultRole,
				defaultUser, createdSubelement, createdTag);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdSubelement, pcmmAssessment.getSubelement());
		assertEquals(defaultRole, pcmmAssessment.getRoleCreation());
		assertEquals(defaultUser, pcmmAssessment.getUserCreation());
		assertEquals(createdTag, pcmmAssessment.getTag());
	}

	@Test
	void testFindByRoleAndUserAndSubeltAndTag_SubeltNull() {
		List<PCMMAssessment> findBySubelementAndTag = getRepository().findByRoleAndUserAndSubeltAndTag(new Role(),
				new User(), null, new Tag());
		assertNotNull(findBySubelementAndTag);
		assertTrue(findBySubelementAndTag.isEmpty());
	}

	@Test
	void testFindByRoleAndUserAndSubeltAndTag_RoleNull() {
		List<PCMMAssessment> findBySubelementAndTag = getRepository().findByRoleAndUserAndSubeltAndTag(null, new User(),
				new PCMMSubelement(), new Tag());
		assertNotNull(findBySubelementAndTag);
		assertTrue(findBySubelementAndTag.isEmpty());
	}

	@Test
	void testFindByRoleAndUserAndSubeltAndTag_UserNull() {
		List<PCMMAssessment> findBySubelementAndTag = getRepository().findByRoleAndUserAndSubeltAndTag(new Role(), null,
				new PCMMSubelement(), new Tag());
		assertNotNull(findBySubelementAndTag);
		assertTrue(findBySubelementAndTag.isEmpty());
	}

	@Test
	void testFindByTag_TagNull() {

		// create user
		User createdUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(createdUser);

		// create role
		Role createdRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(createdRole);

		// create assessment
		PCMMAssessment addedAssessment = new PCMMAssessment();
		addedAssessment.setDateCreation(new Date());
		addedAssessment.setRoleCreation(createdRole);
		addedAssessment.setUserCreation(createdUser);
		addedAssessment.setDateCreation(new Date());
		try {
			addedAssessment = getRepository().create(addedAssessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findByTag(null);
		assertNotNull(findByElementAndTag);
		assertTrue(!findByElementAndTag.isEmpty());

		for (PCMMAssessment pcmmAssessment : findByElementAndTag) {
			assertNotNull(pcmmAssessment);
			assertNotNull(pcmmAssessment.getId());
			assertEquals(null, pcmmAssessment.getTag());
		}
	}

	@Test
	void testFindByTag_TagNotNull() {

		// create user
		User createdUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(createdUser);

		// create role
		Role createdRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(createdRole);

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), createdUser);
		assertNotNull(createdTag);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setTag(createdTag);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(createdRole);
		assessment.setUserCreation(createdUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// find by element and tag in subelement
		List<PCMMAssessment> findByElementAndTag = getRepository().findByTag(createdTag);
		assertNotNull(findByElementAndTag);
		assertEquals(1, findByElementAndTag.size());

		PCMMAssessment pcmmAssessment = findByElementAndTag.get(0);
		assertNotNull(pcmmAssessment);
		assertNotNull(pcmmAssessment.getId());
		assertEquals(createdTag, pcmmAssessment.getTag());
	}

	@Test
	void testClearMultipleAssessment_DefaultMode_NoAssessment() {

		boolean wasDirty = getRepository().clearMultipleAssessment(PCMMMode.DEFAULT);
		assertFalse(wasDirty);
	}

	@Test
	void testClearMultipleAssessment_DefaultMode_NoMultipleAssessment() {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setTag(null);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// check multiple assessment
		boolean wasDirty = getRepository().clearMultipleAssessment(PCMMMode.DEFAULT);
		assertFalse(wasDirty);
	}

	@Test
	void testClearMultipleAssessment_DefaultMode_MultipleAssessments_TagNull() {

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
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setDateCreation(new Date());
		assessment.setTag(null);
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		assessment.setSubelement(createdSubelement);
		try {
			assessment = getRepository().create(assessment);
			assertNotNull(assessment);
			assertNotNull(assessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create assessment
		PCMMAssessment assessment2 = new PCMMAssessment();
		assessment2.setTag(null);
		assessment2.setRoleCreation(defaultRole);
		assessment2.setUserCreation(defaultUser);
		assessment2.setSubelement(createdSubelement);
		assessment2.setDateCreation(new Date());
		try {
			assessment2 = getRepository().create(assessment2);
			assertNotNull(assessment2);
			assertNotNull(assessment2.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		assertNotEquals(assessment.getId(), assessment2.getId());
		assertEquals(assessment.getRoleCreation(), assessment2.getRoleCreation());
		assertEquals(assessment.getSubelement(), assessment2.getSubelement());
		assertEquals(assessment.getUserCreation(), assessment2.getUserCreation());
		assertEquals(assessment.getTag(), assessment2.getTag());

		// check multiple assessment
		boolean wasDirty = getRepository().clearMultipleAssessment(PCMMMode.DEFAULT);
		assertTrue(wasDirty);

		// check assessment unicity
		List<PCMMAssessment> assessments = getRepository().findByRoleAndUserAndSubeltAndTag(defaultRole, defaultUser,
				createdSubelement, null);
		assertNotNull(assessments);
		assertEquals(1, assessments.size());
	}

	@Test
	void testClearMultipleAssessment_DefaultMode_MultipleAssessments_TagNotNull() {

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

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setTag(createdTag);
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		assessment.setSubelement(createdSubelement);
		assessment.setDateCreation(new Date());
		try {
			assessment = getRepository().create(assessment);
			assertNotNull(assessment);
			assertNotNull(assessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create assessment
		PCMMAssessment assessment2 = new PCMMAssessment();
		assessment2.setTag(createdTag);
		assessment2.setRoleCreation(defaultRole);
		assessment2.setUserCreation(defaultUser);
		assessment2.setSubelement(createdSubelement);
		assessment2.setDateCreation(new Date());
		try {
			assessment2 = getRepository().create(assessment2);
			assertNotNull(assessment2);
			assertNotNull(assessment2.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		assertNotEquals(assessment.getId(), assessment2.getId());
		assertEquals(assessment.getRoleCreation(), assessment2.getRoleCreation());
		assertEquals(assessment.getSubelement(), assessment2.getSubelement());
		assertEquals(assessment.getUserCreation(), assessment2.getUserCreation());
		assertEquals(assessment.getTag(), assessment2.getTag());

		// check multiple assessment
		boolean wasDirty = getRepository().clearMultipleAssessment(PCMMMode.DEFAULT);
		assertTrue(wasDirty);

		// check assessment unicity
		List<PCMMAssessment> assessments = getRepository().findByRoleAndUserAndSubeltAndTag(defaultRole, defaultUser,
				createdSubelement, createdTag);
		assertNotNull(assessments);
		assertEquals(1, assessments.size());
	}

	@Test
	void testClearMultipleAssessment_SimplifiedMode_NoAssessment() {

		boolean wasDirty = getRepository().clearMultipleAssessment(PCMMMode.SIMPLIFIED);
		assertFalse(wasDirty);
	}

	@Test
	void testClearMultipleAssessment_SimplifiedMode_NoMultipleAssessment() {

		// create user
		User createdUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(createdUser);

		// create role
		Role createdRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(createdRole);

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create element
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(createdElement);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setTag(null);
		assessment.setDateCreation(new Date());
		assessment.setElement(createdElement);
		assessment.setRoleCreation(createdRole);
		assessment.setUserCreation(createdUser);
		try {
			PCMMAssessment addedAssessment = getRepository().create(assessment);
			assertNotNull(addedAssessment);
			assertNotNull(addedAssessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// check multiple assessment
		boolean wasDirty = getRepository().clearMultipleAssessment(PCMMMode.SIMPLIFIED);
		assertFalse(wasDirty);
	}

	@Test
	void testClearMultipleAssessment_SimplifiedMode_MultipleAssessments_TagNull() {

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
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setTag(null);
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		assessment.setElement(createdElement);
		assessment.setDateCreation(new Date());
		try {
			assessment = getRepository().create(assessment);
			assertNotNull(assessment);
			assertNotNull(assessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create assessment
		PCMMAssessment assessment2 = new PCMMAssessment();
		assessment2.setTag(null);
		assessment2.setRoleCreation(defaultRole);
		assessment2.setUserCreation(defaultUser);
		assessment2.setElement(createdElement);
		assessment2.setDateCreation(new Date());
		try {
			assessment2 = getRepository().create(assessment2);
			assertNotNull(assessment2);
			assertNotNull(assessment2.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		assertNotEquals(assessment.getId(), assessment2.getId());
		assertEquals(assessment.getRoleCreation(), assessment2.getRoleCreation());
		assertEquals(assessment.getSubelement(), assessment2.getSubelement());
		assertEquals(assessment.getUserCreation(), assessment2.getUserCreation());
		assertEquals(assessment.getTag(), assessment2.getTag());

		// check multiple assessment
		boolean wasDirty = getRepository().clearMultipleAssessment(PCMMMode.SIMPLIFIED);
		assertTrue(wasDirty);

		// check assessment unicity
		List<PCMMAssessment> assessments = getRepository().findByRoleAndUserAndEltAndTag(defaultRole, defaultUser,
				createdElement, null);
		assertNotNull(assessments);
		assertEquals(1, assessments.size());
	}

	@Test
	void testClearMultipleAssessment_SimplifiedMode_MultipleAssessments_TagNotNull() {

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

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);

		// create assessment
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setTag(createdTag);
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		assessment.setElement(createdElement);
		assessment.setDateCreation(new Date());
		try {
			assessment = getRepository().create(assessment);
			assertNotNull(assessment);
			assertNotNull(assessment.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create assessment
		PCMMAssessment assessment2 = new PCMMAssessment();
		assessment2.setTag(createdTag);
		assessment2.setRoleCreation(defaultRole);
		assessment2.setUserCreation(defaultUser);
		assessment2.setElement(createdElement);
		assessment2.setDateCreation(new Date());
		try {
			assessment2 = getRepository().create(assessment2);
			assertNotNull(assessment2);
			assertNotNull(assessment2.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		assertNotEquals(assessment.getId(), assessment2.getId());
		assertEquals(assessment.getRoleCreation(), assessment2.getRoleCreation());
		assertEquals(assessment.getSubelement(), assessment2.getSubelement());
		assertEquals(assessment.getUserCreation(), assessment2.getUserCreation());
		assertEquals(assessment.getTag(), assessment2.getTag());

		// check multiple assessment
		boolean wasDirty = getRepository().clearMultipleAssessment(PCMMMode.SIMPLIFIED);
		assertTrue(wasDirty);

		// check assessment unicity
		List<PCMMAssessment> assessments = getRepository().findByRoleAndUserAndEltAndTag(defaultRole, defaultUser,
				createdElement, createdTag);
		assertNotNull(assessments);
		assertEquals(1, assessments.size());
	}

	@Test
	void testClearMultipleAssessment_ModeNull() {

		// check multiple assessment
		boolean wasDirty = getRepository().clearMultipleAssessment(null);
		assertFalse(wasDirty);
	}

}
