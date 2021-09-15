/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.GenericValueTaggable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMPlanningQuestionValue;
import gov.sandia.cf.model.PCMMPlanningTableItem;
import gov.sandia.cf.model.PCMMPlanningTableValue;
import gov.sandia.cf.model.PCMMPlanningValue;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Didier Verstraete
 *
 *         JUnit test class for the PCMM Application Controller
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationTagTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMApplicationTagTest.class);

	@Test
	void testTagCRUDWorking() throws CredibilityException {

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// ********************************
		// Create a Tag
		// ********************************
		Tag newTag = new Tag();
		newTag.setName("My_Tag"); //$NON-NLS-1$
		newTag.setDateTag(new Date());
		newTag.setUserCreation(defaultUser);
		// create tag
		Tag createdTag = getPCMMApp().tagCurrent(newTag);

		// Test
		assertEquals("My_Tag", createdTag.getName()); //$NON-NLS-1$
		assertNotNull(newTag.getId());
		assertNotNull(createdTag.getDateTag());
		assertNull(createdTag.getDescription());
		assertEquals(defaultUser, createdTag.getUserCreation());

		// ********************************
		// Get tags
		// ********************************
		List<Tag> tags = getPCMMApp().getTags();
		assertEquals(1, tags.size());

		// ********************************
		// Update tag
		// ********************************
		Tag tag = tags.get(0);
		tag.setDescription("My_Description_updated"); //$NON-NLS-1$
		// update tag
		Tag updatedTag = getPCMMApp().updateTag(tag);
		assertNotNull(updatedTag);
		assertEquals("My_Description_updated", updatedTag.getDescription()); //$NON-NLS-1$
		assertEquals(tag.getId(), updatedTag.getId());
		assertEquals(tag.getDateTag(), updatedTag.getDateTag());
		assertEquals(tag.getName(), updatedTag.getName());
		assertEquals(tag.getUserCreation(), updatedTag.getUserCreation());

		// ********************************
		// Delete tag
		// ********************************
		getPCMMApp().deleteTag(updatedTag);
		tags = getPCMMApp().getTags();
		assertTrue(tags.isEmpty());

		// ********************************
		// Assessments
		// ********************************
		// create role
		Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(defaultRole);

		// Create assessment
		PCMMLevel level = null;
		PCMMAssessment assessment = null;
		// Create level
		level = new PCMMLevel();
		level.setCode(0);
		level.setName("Level_0"); //$NON-NLS-1$
		level = getPCMMApp().addLevel(level);

		// Create assessment
		assessment = new PCMMAssessment();
		assessment.setLevel(level);
		assessment.setUserCreation(defaultUser);
		assessment.setRoleCreation(defaultRole);
		assessment = getPCMMApp().addAssessment(assessment);
		assertNotNull(assessment);

		// Tag with assessment
		newTag = getPCMMApp().tagCurrent(tag);
		assertNotNull(newTag.getId());
	}

	@Test
	void testTagCurrent_TagNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().tagCurrent(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_TAGCURRENT_TAGNULL), e.getMessage());
	}

	@Test
	void testPCMMPlanning_TagCurrent_TagNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().tagCurrent(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_TAG_NULL), e.getMessage());
	}

	@Test
	void testPCMMPlanning_TagCurrent_TagIdNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().tagCurrent(new Tag());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_TAG_IDNULL), e.getMessage());
	}

	@Test
	void testTagCurrent() throws CredibilityException {

		// ******************************
		// Test with element, sub-element
		// ******************************
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
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(element);

		// create sub-element
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), element);
		assertNotNull(subelement);

		// create evidence
		PCMMEvidence evidence = TestEntityFactory.getNewPCMMEvidence(getDaoManager(), defaultRole, defaultUser,
				subelement);
		assertNotNull(evidence);

		// create evidence
		PCMMAssessment newPCMMAssessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), defaultRole,
				defaultUser, subelement, null);
		assertNotNull(newPCMMAssessment);

		// create planning values
		PCMMPlanningQuestionValue newPCMMPlanningQuestionValue = TestEntityFactory
				.getNewPCMMPlanningQuestionValue(getDaoManager(), null, null, null);
		assertNotNull(newPCMMPlanningQuestionValue);
		PCMMPlanningValue newPCMMPlanningValue = TestEntityFactory.getNewPCMMPlanningValue(getDaoManager(), null,
				subelement, null, null);
		assertNotNull(newPCMMPlanningValue);
		PCMMPlanningTableItem newPCMMPlanningTableItem = TestEntityFactory.getNewPCMMPlanningTableItem(getDaoManager(),
				null, subelement, null, null);
		assertNotNull(newPCMMPlanningTableItem);
		PCMMPlanningTableValue newPCMMPlanningTableValue = TestEntityFactory
				.getNewPCMMPlanningTableValue(getDaoManager(), null, newPCMMPlanningTableItem, null);
		assertNotNull(newPCMMPlanningTableValue);
		PCMMPlanningTableValue newPCMMPlanningTableValue2 = TestEntityFactory
				.getNewPCMMPlanningTableValue(getDaoManager(), null, newPCMMPlanningTableItem, null);
		assertNotNull(newPCMMPlanningTableValue2);
		getPCMMPlanningApp().refreshPlanningTableItem(newPCMMPlanningTableItem);

		// Create tag
		Tag tag = new Tag();
		tag.setName("My_Tag"); //$NON-NLS-1$
		tag.setDateTag(new Date());
		tag.setUserCreation(defaultUser);

		// Tag current without element
		Tag createdTag = getPCMMApp().tagCurrent(tag);
		assertNotNull(createdTag.getId());

		// try to find after tag creation
		List<PCMMEvidence> evidenceByTag = getPCMMApp().getEvidenceByTag(createdTag);
		assertNotNull(evidenceByTag);
		assertFalse(evidenceByTag.isEmpty());

		List<PCMMAssessment> assessmentByTag = getPCMMApp().getAssessmentByTag(createdTag);
		assertNotNull(assessmentByTag);
		assertFalse(assessmentByTag.isEmpty());

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericValueTaggable.Filter.TAG, createdTag);

		List<PCMMPlanningQuestionValue> planningQuestionValueBy = getPCMMPlanningApp()
				.getPlanningQuestionValueBy(filters);
		assertNotNull(planningQuestionValueBy);
		assertFalse(planningQuestionValueBy.isEmpty());

		List<PCMMPlanningValue> planningValueBy = getPCMMPlanningApp().getPlanningValueBy(filters);
		assertNotNull(planningValueBy);
		assertFalse(planningValueBy.isEmpty());

		List<PCMMPlanningTableItem> planningTableItemBy = getPCMMPlanningApp().getPlanningTableItemBy(filters);
		assertNotNull(planningTableItemBy);
		assertFalse(planningTableItemBy.isEmpty());

		// set arg parameters PCMM tag
		ARGParameters argParameters = TestEntityFactory.getNewARGParameters(getDaoManager());
		argParameters.setPcmmTagSelected(tag);
		getAppManager().getService(IReportARGExecutionApp.class).updateARGParameters(argParameters);
		assertEquals(tag, argParameters.getPcmmTagSelected());

		// Delete tag
		getPCMMApp().deleteTag(tag);

		// try to find after tag deletion
		List<PCMMEvidence> evidenceByTag1 = getPCMMApp().getEvidenceByTag(tag);
		assertNotNull(evidenceByTag1);
		assertTrue(evidenceByTag1.isEmpty());

		List<PCMMAssessment> assessmentByTag1 = getPCMMApp().getAssessmentByTag(tag);
		assertNotNull(assessmentByTag1);
		assertTrue(assessmentByTag1.isEmpty());

		filters = new HashMap<>();
		filters.put(GenericValueTaggable.Filter.TAG, createdTag);

		List<PCMMPlanningQuestionValue> planningQuestionValueBy1 = getPCMMPlanningApp()
				.getPlanningQuestionValueBy(filters);
		assertNotNull(planningQuestionValueBy1);
		assertTrue(planningQuestionValueBy1.isEmpty());

		List<PCMMPlanningValue> planningValueBy1 = getPCMMPlanningApp().getPlanningValueBy(filters);
		assertNotNull(planningValueBy1);
		assertTrue(planningValueBy1.isEmpty());

		List<PCMMPlanningTableItem> planningTableItemBy1 = getPCMMPlanningApp().getPlanningTableItemBy(filters);
		assertNotNull(planningTableItemBy1);
		assertTrue(planningTableItemBy1.isEmpty());

		assertNull(getAppManager().getService(IReportARGExecutionApp.class).getARGParameters().getPcmmTagSelected());
	}

	@Test
	void testTagCurrent_ErrorUserNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().tagCurrent(new Tag());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_TAGCURRENT_USERNULL), e.getMessage());
	}

	@Test
	void testUpdateTag_ErrorTagNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().updateTag(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATETAG_TAGNULL), e.getMessage());
	}

	@Test
	void testUpdateTag_ErrorIdNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().updateTag(new Tag());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_UPDATETAG_IDNULL), e.getMessage());
	}

	@Test
	void testDeleteTag_ErrorTagNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().deleteTag(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETETAG_TAGNULL), e.getMessage());
	}

	@Test
	void testDeleteTag_ErrorIdNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMApp().deleteTag(new Tag());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMM_DELETETAG_IDNULL), e.getMessage());
	}

	@Test
	void testPCMMPlanning_DeleteTag_ErrorTagNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().deleteTagged(null);
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_TAG_NULL), e.getMessage());
	}

	@Test
	void testPCMMPlanning_DeleteTag_ErrorIdNull() {
		CredibilityException e = assertThrows(CredibilityException.class, () -> {
			getPCMMPlanningApp().deleteTagged(new Tag());
		});
		assertEquals(RscTools.getString(RscConst.EX_PCMMPLANNING_TAG_IDNULL), e.getMessage());
	}

}
