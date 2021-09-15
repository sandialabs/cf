/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.eclipse.core.runtime.CoreException;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IPCMMAssessmentRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * JUnit test class for the PCMM Application Controller - Assessment
 * 
 * @author Maxime N.
 *
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationAssessmentTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationAssessmentTest.class);

	@Test
	void testAssessmentCRUDWorking() throws CredibilityException {
		// Initialize
		PCMMAssessment assessment = new PCMMAssessment();
		User user = null;
		Role role = null;
		PCMMLevel level0 = null;
		PCMMLevel level1 = null;
		Tag tag = null;
		PCMMElement element = null;
		PCMMSubelement subelement = null;

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// ************************
		// Create assessment
		// ************************
		// Create User for assessment
		user = new User();
		user.setUserID("My_User_Id"); //$NON-NLS-1$
		user = getAppManager().getService(IUserApplication.class).addUser(user);

		// Create role for assessment
		role = new Role();
		role.setName("My_Role"); //$NON-NLS-1$
		role = getPCMMApp().addRole(role);

		// Create level for assessment
		level0 = new PCMMLevel();
		level0.setCode(0);
		level0.setName("Level_0"); //$NON-NLS-1$
		level1 = new PCMMLevel();
		level1.setCode(1);
		level1.setName("Level_1"); //$NON-NLS-1$
		level0 = getPCMMApp().addLevel(level0);
		level1 = getPCMMApp().addLevel(level1);

		// Create tag
		tag = new Tag();
		tag.setName("My_Tag"); //$NON-NLS-1$
		tag.setUserCreation(user);
		getPCMMApp().tagCurrent(tag);

		// Create element
		element = new PCMMElement();
		element.setName("My_Element"); //$NON-NLS-1$
		element.setAbbreviation("Abbrev"); //$NON-NLS-1$
		element.setModel(createdModel);
		getPCMMApp().addElement(element);

		// Create subelement
		subelement = new PCMMSubelement();
		subelement.setName("My_Subelement"); //$NON-NLS-1$
		subelement.setCode("Code"); //$NON-NLS-1$
		subelement.setElement(element);
		getPCMMApp().addSubelement(subelement);

		// Create assessment
		assessment.setLevel(level0);
		assessment.setComment("My_comment"); //$NON-NLS-1$
		assessment.setUserCreation(user);
		assessment.setRoleCreation(role);

		// Create
		assessment = getPCMMApp().addAssessment(assessment);

		// Apply changes
		assessment.setElement(element);
		assessment.setSubelement(subelement);
		assessment.setTag(tag);
		assessment = getPCMMApp().updateAssessment(assessment, user, role);

		// Test creation
		assertNotNull(assessment);
		assertNotNull(assessment.getId());

		// ************************
		// Get Assessment by Id
		// ************************
		PCMMAssessment assessmentById = getPCMMApp().getAssessmentById(assessment.getId());

		// Check
		assertNotNull(assessmentById);
		assertEquals(assessmentById.getId(), assessment.getId());

		// ******************************
		// Update assessment
		// ******************************
		PCMMAssessment updatedAssessment = null;
		assessment.setLevel(level1);
		updatedAssessment = getPCMMApp().updateAssessment(assessment, user, role);
		assertEquals(updatedAssessment.getUserCreation().getId(), user.getId());
		assertEquals(updatedAssessment.getRoleCreation().getId(), role.getId());
		assertEquals(updatedAssessment.getLevel().getId(), level1.getId());
		assertNotNull(updatedAssessment);

		// *************************
		// Get by methods
		// *************************
		List<PCMMAssessment> assessmentBy;
		// Filters
		Map<EntityFilter, Object> filters = new HashMap<EntityFilter, Object>();
		filters.put(PCMMAssessment.Filter.TAG, tag);

		// Get by element
		assessmentBy = getPCMMApp().getAssessmentByElement(element, filters);
		assertFalse(assessmentBy.isEmpty());
		assertEquals(1, assessmentBy.size());

		// Get by sub-element
		assessmentBy = getPCMMApp().getAssessmentBySubelement(subelement, filters);
		assertFalse(assessmentBy.isEmpty());
		assertEquals(1, assessmentBy.size());

		// Get by sub-element in element
		assessmentBy = getPCMMApp().getAssessmentByElementInSubelement(element, tag);
		assertFalse(assessmentBy.isEmpty());
		assertEquals(1, assessmentBy.size());

		// Get by role user element tag
		assessmentBy = getPCMMApp().getAssessmentByRoleAndUserAndEltAndTag(role, user, element, tag);
		assertFalse(assessmentBy.isEmpty());
		assertEquals(1, assessmentBy.size());

		// Get by role user sub-element tag
		assessmentBy = getPCMMApp().getAssessmentByRoleAndUserAndSubeltAndTag(role, user, subelement, tag);
		assertFalse(assessmentBy.isEmpty());
		assertEquals(1, assessmentBy.size());

		// Get by tag
		assessmentBy = getPCMMApp().getAssessmentByTag(tag);
		assertFalse(assessmentBy.isEmpty());
		assertEquals(1, assessmentBy.size());

		// ******************************
		// Delete assessment
		// ******************************
		getPCMMApp().deleteAssessment(updatedAssessment);
		assertNull(getPCMMApp().getAssessmentById(updatedAssessment.getId()));

		// ************************
		// Create assessment list
		// ************************
		assessment = new PCMMAssessment();
		assessment.setLevel(level0);
		assessment.setComment("My_comment"); //$NON-NLS-1$
		assessment.setUserCreation(user);
		assessment.setRoleCreation(role);
		assessment = getPCMMApp().addAssessment(assessment);

		// Get list
		List<PCMMAssessment> assessments = getPCMMApp().getActiveAssessmentList();
		assertNotNull(assessments);
		assertFalse(assessments.isEmpty());
		assertEquals(1, assessments.size());

		// ******************************
		// Delete assessment List
		// ******************************
		getPCMMApp().deleteAssessment(assessments);
		assertNull(getPCMMApp().getAssessmentById(assessments.get(0).getId()));
	}

	/* ************** getAssessmentById ************* */

	@Test
	void test_getAssessmentById_Errors() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().getAssessmentById(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_GETASSESSTBYID_IDNULL), e.getMessage());
	}

	/* ************** addAssessment ************* */

	@Test
	void test_addAssessment_Error_Null() {
		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().addAssessment(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_ASSESSTNULL), e.getMessage());
	}

	@Test
	void test_addAssessment_Error_UserNull() {

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().addAssessment(assessment);
		});
		assertTrue(e.getCause() instanceof ConstraintViolationException);
		assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
				RscConst.EX_PCMMASSESSMENT_USER_NULL));
	}

	@Test
	void test_addAssessment_Error_RoleNull() {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setDateCreation(new Date());
		assessment.setUserCreation(defaultUser);
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().addAssessment(assessment);
		});
		assertTrue(e.getCause() instanceof ConstraintViolationException);
		assertFalse(((ConstraintViolationException) e.getCause()).getConstraintViolations().isEmpty());
		assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
				RscConst.EX_PCMMASSESSMENT_ROLE_NULL));
	}

	/* ************** updateAssessment ************* */

	@Test
	void test_updateAssessment_Error_Null() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().updateAssessment(null, null, null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_ASSESSTNULL), e.getMessage());
	}

	@Test
	void test_updateAssessment_Error_IdNull() {
		PCMMAssessment assessment = new PCMMAssessment();
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().updateAssessment(assessment, null, null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_IDNULL), e.getMessage());
	}

	@Test
	void test_updateAssessment_Error_UserNull() {

		// create assessment
		PCMMAssessment assessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), null, null, null, null);
		assertNotNull(assessment);

		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().updateAssessment(assessment, null, assessment.getRoleCreation());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_DIFFUSERNULL), e.getMessage());
	}

	@Test
	void test_updateAssessment_Error_DifferentUser() {

		// create assessment
		PCMMAssessment assessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), null, null, null, null);
		assertNotNull(assessment);

		// create user
		User otherUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(otherUser);

		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().updateAssessment(assessment, otherUser, assessment.getRoleCreation());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_DIFFUSERNULL), e.getMessage());
	}

	@Test
	void test_updateAssessment_Error_RoleNull() {

		// create assessment
		PCMMAssessment assessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), null, null, null, null);
		assertNotNull(assessment);

		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().updateAssessment(assessment, assessment.getUserCreation(), null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_DIFFROLENULL), e.getMessage());
	}

	@Test
	void test_updateAssessment_Error_DifferentRole() {

		// create assessment
		PCMMAssessment assessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), null, null, null, null);
		assertNotNull(assessment);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().updateAssessment(assessment, assessment.getUserCreation(), defaultRole);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_DIFFROLENULL), e.getMessage());
	}

	/* ************** deleteAssessment ************* */

	@Test
	void test_deleteAssessment_Error_Null() {
		PCMMAssessment assessment = null;
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().deleteAssessment(assessment);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEASSESSTBYID_ASSESSTNULL), e.getMessage());
	}

	@Test
	void test_deleteAssessment_Error_IdNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().deleteAssessment(new PCMMAssessment());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETEASSESSTBYID_IDNULL), e.getMessage());
	}

	@Test
	void test_deleteAssessment_Error_ListNull() {
		List<PCMMAssessment> addedAssessmentList = null;
		try {
			getPCMMApp().deleteAssessment(addedAssessmentList);
		} catch (CredibilityException e) {
			fail("Must not throw an exception"); //$NON-NLS-1$
		}
	}

	/* ************** findBy ************* */

	@Test
	void test_findBy_Element() throws CredibilityException {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// create tag
		Tag createdTag = TestEntityFactory.getNewTag(getDaoManager(), defaultUser);
		assertNotNull(createdTag);
		assertNotNull(createdTag);

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create element
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(createdElement);

		// create assessment not tagged
		PCMMAssessment assessment = new PCMMAssessment();
		assessment.setElement(createdElement);
		assessment.setTag(null);
		assessment.setDateCreation(new Date());
		assessment.setRoleCreation(defaultRole);
		assessment.setUserCreation(defaultUser);
		PCMMAssessment addedAssessment = getDaoManager().getRepository(IPCMMAssessmentRepository.class)
				.create(assessment);
		assertNotNull(addedAssessment);
		assertNotNull(addedAssessment.getId());

		// create assessment tagged
		PCMMAssessment assessmentTagged = new PCMMAssessment();
		assessmentTagged.setElement(createdElement);
		assessmentTagged.setTag(createdTag);
		assessmentTagged.setDateCreation(new Date());
		assessmentTagged.setRoleCreation(defaultRole);
		assessmentTagged.setUserCreation(defaultUser);
		PCMMAssessment addedAssessmentTagged = getDaoManager().getRepository(IPCMMAssessmentRepository.class)
				.create(assessmentTagged);
		assertNotNull(addedAssessmentTagged);
		assertNotNull(addedAssessmentTagged.getId());

		// find by element
		List<PCMMAssessment> findByElement = getPCMMApp().getAssessmentByElement(createdElement, null);
		assertNotNull(findByElement);
		assertEquals(2, findByElement.size());
	}

	/* ************** getAssessmentByTag ************* */

	@Test
	void test_getAssessmentByTag_Ok() throws CoreException, CredibilityException {

		Tag tag = TestEntityFactory.getNewTag(getDaoManager(), null);
		User user = TestEntityFactory.getNewUser(getDaoManager());
		Role role = TestEntityFactory.getNewRole(getDaoManager());
		PCMMAssessment assess = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role, user, null, null);
		PCMMAssessment assessTag = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role, user, null, null);
		assessTag.setTag(tag);
		getPCMMApp().updateAssessment(assessTag, user, role);

		// test
		List<PCMMAssessment> evidenceByTag = getPCMMApp().getAssessmentByTag(tag);
		assertTrue(evidenceByTag.contains(assessTag));
		assertFalse(evidenceByTag.contains(assess));
	}

	@Test
	void test_getAssessmentByTag_Ok_TagList() throws CoreException, CredibilityException {

		Tag tag1 = TestEntityFactory.getNewTag(getDaoManager(), null);
		Tag tag2 = TestEntityFactory.getNewTag(getDaoManager(), null);
		User user = TestEntityFactory.getNewUser(getDaoManager());
		Role role = TestEntityFactory.getNewRole(getDaoManager());
		PCMMAssessment evidence = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role, user, null, null);
		PCMMAssessment evidenceTag1 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role, user, null, null);
		evidenceTag1.setTag(tag1);
		getPCMMApp().updateAssessment(evidenceTag1, user, role);
		PCMMAssessment evidenceTag2 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role, user, null, null);
		evidenceTag2.setTag(tag2);
		getPCMMApp().updateAssessment(evidenceTag2, user, role);

		// test
		List<PCMMAssessment> evidenceByTag = getPCMMApp().getAssessmentByTag(Arrays.asList(tag1, tag2));
		assertTrue(evidenceByTag.contains(evidenceTag1));
		assertTrue(evidenceByTag.contains(evidenceTag2));
		assertFalse(evidenceByTag.contains(evidence));
	}
}
