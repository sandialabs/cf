/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.IPCMMAssessmentRepository;
import gov.sandia.cf.dao.IPCMMElementRepository;
import gov.sandia.cf.dao.IPCMMSubelementRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMAggregation;
import gov.sandia.cf.model.PCMMAggregationLevel;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PCMM Application Controller
 */
@RunWith(JUnitPlatform.class)
class PCMMApplicationAggregateTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(PCMMApplicationAggregateTest.class);

	protected PCMMSpecification pcmmConfiguration;

	@BeforeEach
	void mochPCMMSpecification() {
		// PCMMSpecification
		pcmmConfiguration = mock(PCMMSpecification.class);
		Map<Integer, PCMMLevelColor> colors = new HashMap<>();
		colors.put(0, new PCMMLevelColor(1, "My_Color_0", "255, 0, 0")); //$NON-NLS-1$ //$NON-NLS-2$
		colors.put(1, new PCMMLevelColor(1, "My_Color_1", "0, 255, 0")); //$NON-NLS-1$ //$NON-NLS-2$
		colors.put(2, new PCMMLevelColor(2, "My_Color_2", "0, 0, 255")); //$NON-NLS-1$ //$NON-NLS-2$
		when(pcmmConfiguration.getLevelColors()).thenReturn(colors);
	}

	@Test
	void testAggregateCRUDWorking() {

		// ************************
		// Test is Complete
		// ************************
		try {
			// create user
			User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
			assertNotNull(defaultUser);

			// create role
			Role defaultRole = TestEntityFactory.getNewRole(getDaoManager());
			assertNotNull(defaultRole);

			// create model
			Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
			assertNotNull(createdModel);

			// Create level for assessment
			PCMMLevel level0 = new PCMMLevel();
			level0.setCode(0);
			level0.setName("Level_0"); //$NON-NLS-1$
			level0 = getPCMMApp().addLevel(level0);
			assertNotNull(level0);
			assertEquals(Integer.valueOf(0), level0.getCode());
			assertEquals("Level_0", level0.getName()); //$NON-NLS-1$

			PCMMLevel level1 = new PCMMLevel();
			level1.setCode(1);
			level1.setName("Level_1"); //$NON-NLS-1$
			level1 = getPCMMApp().addLevel(level1);
			assertNotNull(level1);
			assertEquals(Integer.valueOf(1), level1.getCode());
			assertEquals("Level_1", level1.getName()); //$NON-NLS-1$

			// Level aggregation
			PCMMAggregationLevel aggLevel0 = new PCMMAggregationLevel();
			aggLevel0.setName(level0.getName());
			aggLevel0.setCode(level0.getCode());

			// Level aggregation
			PCMMAggregationLevel aggLevel1 = new PCMMAggregationLevel();
			aggLevel1.setName(level1.getName());
			aggLevel1.setCode(level1.getCode());

			// create element
			PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
			assertNotNull(element);

			// create sub-element
			PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), element);
			assertNotNull(subelement);

			PCMMSubelement subelement2 = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), element);
			assertNotNull(subelement2);

			// Create tag
			Tag tag = new Tag();
			tag.setName("My_Tag"); //$NON-NLS-1$
			tag.setUserCreation(defaultUser);
			getPCMMApp().tagCurrent(tag);

			// create assessment
			PCMMAssessment assessment = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), defaultRole,
					defaultUser, subelement, level0);
			assertNotNull(assessment);
			assessment.setComment("My_comment"); //$NON-NLS-1$
			assessment.setTag(tag);
			assessment = getPCMMAssessmentApp().updateAssessment(assessment, defaultUser, defaultRole);

			// Refresh all
			getDaoManager().getRepository(IModelRepository.class).refresh(createdModel);
			getDaoManager().getRepository(IPCMMElementRepository.class).refresh(element);
			getDaoManager().getRepository(IPCMMSubelementRepository.class).refresh(subelement);
			getDaoManager().getRepository(IPCMMAssessmentRepository.class).refresh(assessment);

			// Check is complete simplified
			Boolean isCompleteSimplified = getPCMMAggregateApp().isCompleteAggregationSimplified(createdModel, null);
			assertFalse(isCompleteSimplified);

			// Check is complete
			Boolean isComplete = getPCMMAggregateApp().isCompleteAggregation(createdModel, null);
			assertFalse(isComplete);

			// Assessment list
			List<PCMMAssessment> assessments = new ArrayList<>();
			assessments.add(assessment);

			// Aggregate
			Map<PCMMElement, PCMMAggregation<PCMMElement>> aggegationElementMap = getPCMMAggregateApp()
					.aggregateAssessmentSimplified(pcmmConfiguration, getPCMMApp().getElementList(createdModel), null);
			assertNotNull(aggegationElementMap);

			Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggegationSubelementMap = getPCMMAggregateApp()
					.aggregateAssessments(pcmmConfiguration, getPCMMApp().getElementList(createdModel), null);
			assertNotNull(aggegationSubelementMap);

			PCMMAggregation<PCMMElement> aggegationElement = getPCMMAggregateApp()
					.aggregateAssessments(pcmmConfiguration, element, assessments);
			assertNotNull(aggegationElement);

			// Element
			// *******
			aggegationElementMap = getPCMMAggregateApp().aggregateSubelements(pcmmConfiguration,
					getPCMMApp().getElementList(createdModel), null);
			assertNotNull(aggegationElementMap);

			// Sub-element
			// ***********
			// Element null
			PCMMAggregation<PCMMSubelement> aggregation = new PCMMAggregation<>();
			subelement.setElement(null);
			aggregation.setItem(subelement);
			aggegationSubelementMap = new HashMap<>();
			aggegationSubelementMap.put(subelement, aggregation);
			aggegationElementMap = getPCMMAggregateApp().aggregateSubelements(pcmmConfiguration,
					aggegationSubelementMap);
			assertNotNull(aggegationElementMap);

			// Item null
			aggregation = new PCMMAggregation<>();
			subelement.setElement(element);
			aggregation.setItem(null);
			aggegationSubelementMap = new HashMap<>();
			aggegationSubelementMap.put(subelement, aggregation);
			aggegationElementMap = getPCMMAggregateApp().aggregateSubelements(pcmmConfiguration,
					aggegationSubelementMap);
			assertNotNull(aggegationElementMap);

			// With level
			aggregation = new PCMMAggregation<>();
			subelement.setElement(element);
			aggregation.setItem(subelement);
			aggregation.setLevel(aggLevel0);
			aggegationSubelementMap = new HashMap<>();
			aggegationSubelementMap.put(subelement, aggregation);
			aggegationElementMap = getPCMMAggregateApp().aggregateSubelements(pcmmConfiguration,
					aggegationSubelementMap);
			assertNotNull(aggegationElementMap);

			// With level
			aggregation = new PCMMAggregation<>();
			subelement.setElement(element);
			aggregation.setItem(subelement);
			aggregation.getItem().setElement(element);
			aggregation.setLevel(aggLevel1);
			aggegationSubelementMap = new HashMap<>();
			aggegationSubelementMap.put(subelement, aggregation);
			aggegationElementMap = getPCMMAggregateApp().aggregateSubelements(pcmmConfiguration,
					aggegationSubelementMap);
			assertNotNull(aggegationElementMap);

			// *********************
			// With no level
			// *********************
			// Level null
			assessments.clear();
			assessment.setLevel(null);
			assessments.add(assessment);
			getPCMMAggregateApp().aggregateAssessments(pcmmConfiguration, element, assessments);

			// Level code null
			assessments.clear();
			assessment.setLevel(level1);
			assessments.add(assessment);
			getPCMMAggregateApp().aggregateAssessments(pcmmConfiguration, element, assessments);

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

	}

	@Test
	void testIsCompleteAggregationErrors() {

		// *********
		// No model
		// *********
		try {
			getPCMMAggregateApp().isCompleteAggregation(null, null);
			fail("Can launch IsCompleteAggregation with a null model."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_GETELTLIST_MODELNULL), e.getMessage());
		}

		// *********
		// No Tag
		// *********
		Boolean isComplete;
		try {
			isComplete = getPCMMAggregateApp().isCompleteAggregation(new Model(), null);
			assertTrue(isComplete);
		} catch (CredibilityException e) {
			fail("Can't launch IsCompleteAggregation with a null tag."); //$NON-NLS-1$
		}
	}

	@Test
	void testIsCompleteAggregationSimplifiedErrors() {
		// *********
		// No model
		// *********
		try {
			getPCMMAggregateApp().isCompleteAggregationSimplified(null, null);
			fail("Can launch IsCompleteAggregation with a null model."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_GETELTLIST_MODELNULL), e.getMessage());
		}

		// *********
		// No Tag
		// *********
		Boolean isComplete;
		try {
			isComplete = getPCMMAggregateApp().isCompleteAggregationSimplified(new Model(), null);
			assertTrue(isComplete);
		} catch (CredibilityException e) {
			fail("Can't launch IsCompleteAggregation with a null tag."); //$NON-NLS-1$
		}
	}

	@Test
	void testAggregateSubelementsErrors() {
		// Initialize
		PCMMSpecification configuration = null;
		List<PCMMElement> elements = null;
		Tag tag = null;

		// Filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMAssessment.Filter.TAG, tag);

		// ********
		// Elements
		// ********
		// elements null
		try {
			getPCMMAggregateApp().aggregateSubelements(configuration, elements, filters);
			fail("Can call AggregateSubelements with a null configuration."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL), e.getMessage());
		}

		// configuration null
		elements = new ArrayList<>();
		try {
			getPCMMAggregateApp().aggregateSubelements(configuration, elements, filters);
			fail("Can call AggregateSubelements with a null configuration."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL), e.getMessage());
		}

		// configuration PCMMSpecification
		configuration = new PCMMSpecification();
		try {
			getPCMMAggregateApp().aggregateSubelements(configuration, elements, filters);
			fail("Can call AggregateSubelements with a null configuration."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL), e.getMessage());
		}

		// ************
		// Sub-elements
		// ************
		configuration = null;
		Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> mapAggregationBySubelement = null;

		// mapAggregationBySubelement null
		try {
			getPCMMAggregateApp().aggregateSubelements(configuration, mapAggregationBySubelement);
			fail("Can call AggregateSubelements with a null configuration."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_AGGREGMAPNULL), e.getMessage());
		}

		// configuration null
		mapAggregationBySubelement = new HashMap<>();
		try {
			getPCMMAggregateApp().aggregateSubelements(configuration, mapAggregationBySubelement);
			fail("Can call AggregateSubelements with a null configuration."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL), e.getMessage());
		}

		// configuration PCMMSpecification
		configuration = new PCMMSpecification();
		try {
			getPCMMAggregateApp().aggregateSubelements(configuration, mapAggregationBySubelement);
			fail("Can call AggregateSubelements with a null configuration."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_CONFLEVELCOLORLISTNULL), e.getMessage());
		}

	}

	@Test
	void testAggregateAssessmentsErrors() {
		// Initialize
		PCMMElement element = null;
		List<PCMMElement> elements = null;
		Tag tag = null;

		// Filters
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMAssessment.Filter.TAG, tag);

		// *************
		// Elements
		// *************
		// Standard - elements null
		try {
			getPCMMAggregateApp().aggregateAssessments(pcmmConfiguration, elements, filters);
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL), e.getMessage());
		}
		// Simplified - elements null
		try {
			getPCMMAggregateApp().aggregateAssessmentSimplified(pcmmConfiguration, elements, filters);
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTLISTNULL), e.getMessage());
		}
		// *************
		// Element
		// *************
		// element null
		try {
			getPCMMAggregateApp().aggregateAssessments(pcmmConfiguration, element, filters);
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ELTNULL), e.getMessage());
		}

		// *************
		// Assessment
		// *************
		// element null
		try {
			IAssessable type = null;
			getPCMMAggregateApp().aggregateAssessments(pcmmConfiguration, type, null);
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PCMM_AGGREGATESUBELT_ITEMNULL), e.getMessage());
		}
	}

	@Test
	void testAggregateLevelValueModeSimplified() {
		if (null == pcmmConfiguration) {
			mochPCMMSpecification();
		}

		final int EXPECTED_CODE = 2;

		/**
		 * Test Simplified mode
		 */
		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create defaultUser
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role for assessment
		Role role0 = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(role0);

		Role role1 = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(role1);

		Role role2 = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(role2);

		// create element
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(element);

		// Create level for assessment
		PCMMLevel level0 = TestEntityFactory.getNewPCMMLevel(getDaoManager(), element, 0);
		assertNotNull(level0);

		PCMMLevel level1 = TestEntityFactory.getNewPCMMLevel(getDaoManager(), element, 2);
		assertNotNull(level1);

		PCMMLevel level2 = TestEntityFactory.getNewPCMMLevel(getDaoManager(), element, 3);
		assertNotNull(level2);

		// Create assessments
		PCMMAssessment assessment0 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role0, defaultUser,
				element, level0);
		assertNotNull(assessment0);

		PCMMAssessment assessment1 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role1, defaultUser,
				element, level1);
		assertNotNull(assessment1);

		PCMMAssessment assessment2 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role2, defaultUser,
				element, level2);
		assertNotNull(assessment2);
		assessment2.setComment("My_comment"); //$NON-NLS-1$
		try {
			getPCMMAssessmentApp().updateAssessment(assessment2, defaultUser, role2);
		} catch (CredibilityException e) {
			fail("Test Aggregate Level Value: " + e.getMessage());//$NON-NLS-1$
		}

		// Get Element in to list
		List<PCMMElement> elements = null;
		try {
			elements = getPCMMApp().getElementList(createdModel);
			assertNotNull(elements);
		} catch (CredibilityException e) {
			fail("Test Aggregate Level Value: " + e.getMessage());//$NON-NLS-1$
		}

		// Aggregate Simplified
		Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregatedElementsMap = null;
		try {
			aggregatedElementsMap = getPCMMAggregateApp().aggregateAssessmentSimplified(pcmmConfiguration, elements,
					null);
			assertNotNull(aggregatedElementsMap);
		} catch (CredibilityException e) {
			fail("Test Aggregate Level Value: " + e.getMessage());//$NON-NLS-1$
		}

		// Test aggregation ok
		assertFalse(aggregatedElementsMap.isEmpty());

		// Test level code is correct
		aggregatedElementsMap.forEach((elt, agg) -> {
			assertNotNull(agg.getLevel());
			assertEquals(Integer.valueOf(EXPECTED_CODE), agg.getLevel().getCode());
		});

	}

	@Test
	void testAggregateLevelValueModeDefault() {
		if (null == pcmmConfiguration) {
			mochPCMMSpecification();
		}

		final int EXPECTED_CODE = 2;

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create defaultUser
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// create role for assessment
		Role role0 = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(role0);

		Role role1 = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(role1);

		Role role2 = TestEntityFactory.getNewRole(getDaoManager());
		assertNotNull(role2);

		// create element
		PCMMElement element = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(element);

		// create sub-element
		PCMMSubelement subelement = TestEntityFactory.getNewPCMMSubelement(getDaoManager(), element);
		assertNotNull(subelement);

		// Create level for assessment
		PCMMLevel level0 = TestEntityFactory.getNewPCMMLevel(getDaoManager(), subelement, 0);
		assertNotNull(level0);

		PCMMLevel level1 = TestEntityFactory.getNewPCMMLevel(getDaoManager(), subelement, 2);
		assertNotNull(level1);

		PCMMLevel level2 = TestEntityFactory.getNewPCMMLevel(getDaoManager(), subelement, 3);
		assertNotNull(level2);

		// Create assessments
		PCMMAssessment assessment0 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role0, defaultUser,
				subelement, level0);
		assertNotNull(assessment0);

		PCMMAssessment assessment1 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role1, defaultUser,
				subelement, level1);
		assertNotNull(assessment1);

		PCMMAssessment assessment2 = TestEntityFactory.getNewPCMMAssessment(getDaoManager(), role2, defaultUser,
				subelement, level2);
		assertNotNull(assessment2);
		assessment2.setComment("My_comment"); //$NON-NLS-1$
		try {
			getPCMMAssessmentApp().updateAssessment(assessment2, defaultUser, role2);
		} catch (CredibilityException e) {
			fail("Test Aggregate Level Value: " + e.getMessage());//$NON-NLS-1$
		}

		/**
		 * Test Default mode
		 */
		// Get Element in to list
		List<PCMMElement> elements = null;
		try {
			elements = getPCMMApp().getElementList(createdModel);
			assertNotNull(elements);
		} catch (CredibilityException e) {
			fail("Test Aggregate Level Value: " + e.getMessage());//$NON-NLS-1$
		}

		// Aggregate
		Map<PCMMSubelement, PCMMAggregation<PCMMSubelement>> aggregatedSubelementsMap = null;
		try {
			aggregatedSubelementsMap = getPCMMAggregateApp().aggregateAssessments(pcmmConfiguration, elements, null);
		} catch (CredibilityException e) {
			fail("Test Aggregate Level Value: " + e.getMessage());//$NON-NLS-1$
		}
		assertNotNull(aggregatedSubelementsMap);
		assertFalse(aggregatedSubelementsMap.isEmpty());

		// Test level code is correct
		aggregatedSubelementsMap.forEach((elt, agg) -> {
			assertNotNull(agg.getLevel());
			assertEquals(Integer.valueOf(EXPECTED_CODE), agg.getLevel().getCode());
		});

		Map<PCMMElement, PCMMAggregation<PCMMElement>> aggregatedElementsMap = null;
		try {
			aggregatedElementsMap = getPCMMAggregateApp().aggregateSubelements(pcmmConfiguration,
					aggregatedSubelementsMap);
		} catch (CredibilityException e) {
			fail("Test Aggregate Level Value: " + e.getMessage());//$NON-NLS-1$
		}
		assertNotNull(aggregatedElementsMap);
		assertFalse(aggregatedElementsMap.isEmpty());

		// Test level code is correct
		aggregatedElementsMap.forEach((elt, agg) -> {
			assertNotNull(agg.getLevel());
			assertEquals(Integer.valueOf(EXPECTED_CODE), agg.getLevel().getCode());
		});
	}
}
