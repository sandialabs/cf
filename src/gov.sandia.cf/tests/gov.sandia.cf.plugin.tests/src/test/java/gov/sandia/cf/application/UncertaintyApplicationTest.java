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

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.configuration.uncertainty.UncertaintySpecification;
import gov.sandia.cf.dao.IUncertaintyConstraintRepository;
import gov.sandia.cf.dao.IUncertaintyGroupRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintyRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.dao.IUncertaintyValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the System Uncertainty Application
 */
@RunWith(JUnitPlatform.class)
class UncertaintyApplicationTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(UncertaintyApplicationTest.class);

	/* ************ loadUncertaintyConfiguration ************* */

	@Test
	void test_loadUncertaintyConfiguration_Enabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);

		UncertaintySpecification spec = getAppManager().getService(IUncertaintyApplication.class)
				.loadUncertaintyConfiguration(newModel);

		assertNotNull(spec);
		assertNotNull(spec.getParameters());
		assertEquals(2, spec.getParameters().size());

	}

	@Test
	void test_loadUncertaintyConfiguration_Disabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		UncertaintySpecification spec = getAppManager().getService(IUncertaintyApplication.class)
				.loadUncertaintyConfiguration(newModel);

		assertNull(spec);
	}

	/* ************ getUncertaintyGroupById ************* */

	@Test
	void test_getUncertaintyGroupById_Working() {

		// Construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		UncertaintyGroup uncertaintyGroup = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), newModel);

		// Test
		UncertaintyGroup found = getAppManager().getService(IUncertaintyApplication.class)
				.getUncertaintyGroupById(uncertaintyGroup.getId());
		assertNotNull(found);
		assertEquals(found.getId(), uncertaintyGroup.getId());
	}

	/* ************ getUncertaintyById ************* */

	@Test
	void test_getUncertaintyById_Working() {

		// Construct data
		Uncertainty uncertainty = TestEntityFactory.getNewUncertainty(getDaoManager(), null, null);

		// test
		Uncertainty found = getAppManager().getService(IUncertaintyApplication.class)
				.getUncertaintyById(uncertainty.getId());
		assertNotNull(found);
		assertEquals(found.getId(), uncertainty.getId());
	}

	/* ************ getUncertaintyGroupByModel ************* */

	@Test
	void test_getUncertaintyGroupByModel_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), newModel);
		TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), newModel);

		// test
		List<UncertaintyGroup> groupByModel = getAppManager().getService(IUncertaintyApplication.class)
				.getUncertaintyGroupByModel(newModel);
		assertNotNull(groupByModel);
		assertEquals(2, groupByModel.size());
	}

	/* ************ getUncertaintyParameterByModel ************* */

	@Test
	void test_getUncertaintyParameterByModel_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);

		// test
		List<UncertaintyParam> parameterByModel = getAppManager().getService(IUncertaintyApplication.class)
				.getUncertaintyParameterByModel(newModel);
		assertNotNull(parameterByModel);
		assertEquals(2, parameterByModel.size());
	}

	/* ************ addUncertainty ************* */
	@Test
	void test_addUncertainty_Working() {

		// construct data
		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		UncertaintyParam newUncertaintyParam2 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		Uncertainty uncertainty = new Uncertainty();
		uncertainty.setUserCreation(newUser);

		UncertaintyValue value1 = new UncertaintyValue();
		value1.setDateCreation(new Date());
		value1.setUncertainty(uncertainty);
		value1.setParameter(newUncertaintyParam);
		value1.setUserCreation(newUser);
		value1.setValue("My VALUE"); //$NON-NLS-1$

		UncertaintyValue value2 = new UncertaintyValue();
		value2.setDateCreation(new Date());
		value2.setUncertainty(uncertainty);
		value2.setParameter(newUncertaintyParam2);
		value2.setUserCreation(newUser);
		value2.setValue(null);

		// Set parameter
		uncertainty.setUncertaintyParameterList(Arrays.asList(value1, value2));

		// Group
		UncertaintyGroup group = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), null);
		uncertainty.setGroup(group);

		// test
		try {
			Uncertainty added = getAppManager().getService(IUncertaintyApplication.class).addUncertainty(uncertainty,
					newUser);

			assertNotNull(added);
			assertNotNull(added.getId());

			Uncertainty found = getDaoManager().getRepository(IUncertaintyRepository.class).findById(added.getId());

			assertNotNull(found.getUncertaintyParameterList());
			assertEquals(newUser, found.getUserCreation());
			assertEquals(2, found.getUncertaintyParameterList().size());
			assertTrue(found.getUncertaintyParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newUncertaintyParam)));
			assertTrue(found.getUncertaintyParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newUncertaintyParam2)));
			assertTrue(found.getUncertaintyParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getValue().equals("My VALUE"))); //$NON-NLS-1$
			assertTrue(found.getUncertaintyParameterList().stream().anyMatch(v -> v.getValue() == null));

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_addUncertainty_Null() {
		// Construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).addUncertainty(null, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_addUncertainty_UserNull() {
		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).addUncertainty(new Uncertainty(), null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYROW_USERNULL), e.getMessage());
		}
	}

	@Test
	void test_addUncertaintyGroup_WorkingNoValues() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		UncertaintyGroup uncertaintyGroup = new UncertaintyGroup();
		uncertaintyGroup.setName("GROUP"); //$NON-NLS-1$
		uncertaintyGroup.setUserCreation(newUser);

		// test
		try {
			// Create
			UncertaintyGroup added = getAppManager().getService(IUncertaintyApplication.class)
					.addUncertaintyGroup(uncertaintyGroup, newModel, newUser);
			// Tests
			assertNotNull(added);
			assertNotNull(added.getId());

			// Find
			UncertaintyGroup found = getDaoManager().getRepository(IUncertaintyGroupRepository.class)
					.findById(added.getId());

			// Tests
			assertNotNull(found.getUncertainties());
			assertEquals(newUser, found.getUserCreation());
			assertEquals(0, found.getUncertainties().size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	/* ************ addUncertaintyGroup ************* */
	@Test
	void test_addUncertaintyGroup_Working() {

		// Construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		UncertaintyGroup uncertaintyGroup = new UncertaintyGroup();
		uncertaintyGroup.setName("My Uncertainty Group"); //$NON-NLS-1$
		uncertaintyGroup.setModel(newModel);
		uncertaintyGroup.setUserCreation(newUser);

		Uncertainty value1 = new Uncertainty();
		value1.setGroup(uncertaintyGroup);
		value1.setUserCreation(newUser);

		Uncertainty value2 = new Uncertainty();
		value2.setGroup(uncertaintyGroup);
		value2.setUserCreation(newUser);
		uncertaintyGroup.setUncertainties(Arrays.asList(value1, value2));

		// Actions
		try {
			// Create
			UncertaintyGroup added = getAppManager().getService(IUncertaintyApplication.class)
					.addUncertaintyGroup(uncertaintyGroup, newModel, newUser);

			assertNotNull(added);
			assertNotNull(added.getId());

			// Retrieve
			UncertaintyGroup found = getDaoManager().getRepository(IUncertaintyGroupRepository.class)
					.findById(added.getId());

			// Tests
			assertEquals(added.getName(), found.getName());
			assertEquals(newUser, found.getUserCreation());
			assertEquals(newModel, found.getModel());
			assertNotNull(found.getUncertainties());
			assertEquals(2, found.getUncertainties().size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_addUncertaintyGroup_ModelNull() {
		// Create data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).addUncertaintyGroup(new UncertaintyGroup(), null,
					newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYGROUP_MODELNULL), e.getMessage());
		}
	}

	@Test
	void test_addUncertaintyGroup_Null() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).addUncertaintyGroup(null, newModel, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYGROUP_NULL), e.getMessage());
		}
	}

	@Test
	void test_addUncertaintyGroup_UserNull() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).addUncertaintyGroup(new UncertaintyGroup(),
					newModel, null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYGROUP_USERNULL), e.getMessage());
		}
	}

	/* ************ updateUncertainty ************* */

	@Test
	void test_updateUncertainty_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel,
				null);
		UncertaintyParam newUncertaintyParam2 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel,
				null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		Uncertainty uncertainty = TestEntityFactory.getNewUncertainty(getDaoManager(), null, newUser);

		UncertaintyValue value1 = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), uncertainty,
				newUncertaintyParam, newUser);
		value1.setValue("My VALUE"); //$NON-NLS-1$
		UncertaintyValue value2 = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), uncertainty,
				newUncertaintyParam2, newUser);
		value2.setValue(null);

		getDaoManager().getRepository(IUncertaintyRepository.class).refresh(uncertainty);
		assertEquals(2, uncertainty.getUncertaintyParameterList().size());

		// update
		value1.setValue("UPDATED"); //$NON-NLS-1$
		value2.setValue("UPDATED 2"); //$NON-NLS-1$

		// test
		try {
			Uncertainty updated = getAppManager().getService(IUncertaintyApplication.class)
					.updateUncertainty(uncertainty, newUser);

			assertNotNull(updated);
			assertNotNull(updated.getId());
			assertEquals(uncertainty.getId(), updated.getId());
			assertNotNull(updated.getUncertaintyParameterList());
			assertEquals(uncertainty.getUserCreation(), updated.getUserCreation());
			assertEquals(2, updated.getUncertaintyParameterList().size());
			assertTrue(updated.getUncertaintyParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newUncertaintyParam)));
			assertTrue(updated.getUncertaintyParameterList().stream()
					.anyMatch(v -> v.getParameter() != null && v.getParameter().equals(newUncertaintyParam2)));
			assertTrue(updated.getUncertaintyParameterList().stream().anyMatch(v -> "UPDATED".equals(v.getValue()))); //$NON-NLS-1$
			assertTrue(updated.getUncertaintyParameterList().stream().anyMatch(v -> "UPDATED 2".equals(v.getValue()))); //$NON-NLS-1$

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_updateUncertainty_Null() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).updateUncertainty(null, newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_updateUncertainty_IdNull() {

		// construct data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).updateUncertainty(new Uncertainty(), newUser);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_IDNULL), e.getMessage());
		}
	}

	@Test
	void test_updateUncertainty_UserNull() {

		Uncertainty uncertainty = TestEntityFactory.getNewUncertainty(getDaoManager(), null, null);

		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).updateUncertainty(uncertainty, null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_USERNULL), e.getMessage());
		}
	}

	/* ************ updateUncertainty ************* */

	@Test
	void test_updateUncertaintyGroup_Working() {

		// construct data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// Generate group
		UncertaintyGroup uncertaintyGroup = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), newModel);
		assertNotNull(uncertaintyGroup);

		// Create uncertainty
		Uncertainty uncertainty = TestEntityFactory.getNewUncertainty(getDaoManager(), null, newUser);
		assertNotNull(uncertainty);
		assertNotNull(uncertainty.getGroup());

		// test
		try {
			// Update
			uncertaintyGroup.setName("GROUP UPDATED"); //$NON-NLS-1$
			uncertaintyGroup.setUserCreation(newUser);
			uncertaintyGroup.setUncertainties(Arrays.asList(uncertainty));
			UncertaintyGroup updated = getAppManager().getService(IUncertaintyApplication.class)
					.updateUncertaintyGroup(uncertaintyGroup);

			assertEquals(uncertaintyGroup.getId(), updated.getId());
			assertEquals("GROUP UPDATED", updated.getName()); //$NON-NLS-1$
			assertNotNull(updated.getUncertainties());
			assertEquals(uncertaintyGroup.getUserCreation(), updated.getUserCreation());
			assertEquals(1, updated.getUncertainties().size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void test_updateUncertaintyGroup_Null() {
		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).updateUncertaintyGroup(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYGROUP_NULL), e.getMessage());
		}
	}

	@Test
	void test_updateUncertaintyGroup_IdNull() {
		// test
		try {
			getAppManager().getService(IUncertaintyApplication.class).updateUncertaintyGroup(new UncertaintyGroup());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYGROUP_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteUncertainty************* */

	@Test
	void test_deleteUncertainty_Working() {

		// construct data
		// kept
		Uncertainty newUncertainty = TestEntityFactory.getNewUncertainty(getDaoManager(), null, null);
		UncertaintyValue newUncertaintyValue = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), newUncertainty,
				null, null);

		// to delete
		Uncertainty newUncertainty2 = TestEntityFactory.getNewUncertainty(getDaoManager(), null, null);
		TestEntityFactory.getNewUncertaintyValue(getDaoManager(), newUncertainty2, null, null);
		TestEntityFactory.getNewUncertaintyValue(getDaoManager(), newUncertainty2, null, null);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertainty(newUncertainty2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Test
		List<Uncertainty> findAll = getDaoManager().getRepository(IUncertaintyRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newUncertainty, findAll.iterator().next());

		List<UncertaintyValue> findAllValues = getDaoManager().getRepository(IUncertaintyValueRepository.class)
				.findAll();
		assertNotNull(findAllValues);
		assertEquals(1, findAllValues.size());
		assertEquals(newUncertaintyValue, findAllValues.iterator().next());
	}

	@Test
	void test_deleteUncertainty_Null() {

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertainty(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYROW_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteUncertainty_IdNull() {

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertainty(new Uncertainty());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYROW_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteUncertaintyGroup************* */

	@Test
	void test_deleteUncertaintyGroup_NoUncertaintyWorking() {

		// construct data
		// kept
		UncertaintyGroup newUncertaintyGroup = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), null);

		// to delete
		UncertaintyGroup newUncertaintyGroup2 = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), null);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyGroup(newUncertaintyGroup2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Test
		List<UncertaintyGroup> findAll = getDaoManager().getRepository(IUncertaintyGroupRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newUncertaintyGroup, findAll.iterator().next());
	}

	@Test
	void test_deleteUncertaintyGroup_WithUncertaintiesWorking() {

		// construct data
		// kept
		UncertaintyGroup newUncertaintyGroup = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), null);
		Uncertainty newUncertaintyG1_1 = TestEntityFactory.getNewUncertainty(getDaoManager(), newUncertaintyGroup,
				null);

		// to delete
		UncertaintyGroup newUncertaintyGroup2 = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), null);
		Uncertainty newUncertaintyG2_1 = TestEntityFactory.getNewUncertainty(getDaoManager(), newUncertaintyGroup2,
				null);
		Uncertainty newUncertaintyG2_2 = TestEntityFactory.getNewUncertainty(getDaoManager(), newUncertaintyGroup2,
				null);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyGroup(newUncertaintyGroup2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Test
		// get uncertainty groups
		List<UncertaintyGroup> findAllUncertaintyGroup = getDaoManager()
				.getRepository(IUncertaintyGroupRepository.class).findAll();
		assertNotNull(findAllUncertaintyGroup);
		assertEquals(1, findAllUncertaintyGroup.size());
		assertEquals(newUncertaintyGroup, findAllUncertaintyGroup.iterator().next());

		// get uncertainties
		List<Uncertainty> findAllUncertainties = getDaoManager().getRepository(IUncertaintyRepository.class).findAll();
		assertNotNull(findAllUncertainties);
		assertEquals(1, findAllUncertainties.size());
		assertEquals(newUncertaintyG1_1, findAllUncertainties.iterator().next());

		assertNull(getDaoManager().getRepository(IUncertaintyRepository.class).findById(newUncertaintyG2_1.getId()));
		assertNull(getDaoManager().getRepository(IUncertaintyRepository.class).findById(newUncertaintyG2_2.getId()));

	}

	@Test
	void test_deleteUncertaintyGroup_Null() {

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyGroup(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYGROUP_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteUncertaintyGroup_IdNull() {

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyGroup(new UncertaintyGroup());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYGROUP_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllUncertaintyParam ************* */

	@Test
	void test_deleteAllUncertaintyParam_Working() {

		// construct data
		// Kept
		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		// to delete
		UncertaintyParam newUncertaintyParam2 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		UncertaintyParam newUncertaintyParam3 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class)
					.deleteAllUncertaintyParam(Arrays.asList(newUncertaintyParam2, newUncertaintyParam3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<UncertaintyParam> findAll = getDaoManager().getRepository(IUncertaintyParamRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newUncertaintyParam, findAll.iterator().next());
	}

	/* ************ deleteUncertaintyParam ************* */

	@Test
	void test_deleteUncertaintyParam_Working() {

		// construct data
		// Kept
		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		// to delete
		UncertaintyParam newUncertaintyParam2 = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyParam(newUncertaintyParam2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<UncertaintyParam> findAll = getDaoManager().getRepository(IUncertaintyParamRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newUncertaintyParam, findAll.iterator().next());
	}

	@Test
	void test_deleteUncertaintyParamm_Null() {

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyParam(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYPARAM_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteUncertaintyParam_IdNull() {

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyParam(new UncertaintyParam());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYPARAM_IDNULL), e.getMessage());
		}
	}

	@Test
	void test_deleteUncertaintyParam_WithValuesSelectWorking() {

		// Construct data
		// to delete
		UncertaintyParam newUncertaintyParam = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);

		// select values
		TestEntityFactory.getNewGenericSelectValue(getDaoManager(), UncertaintySelectValue.class, newUncertaintyParam,
				IUncertaintySelectValueRepository.class);
		TestEntityFactory.getNewGenericSelectValue(getDaoManager(), UncertaintySelectValue.class, newUncertaintyParam,
				IUncertaintySelectValueRepository.class);

		// values
		TestEntityFactory.getNewUncertaintyValue(getDaoManager(), null, newUncertaintyParam, null);
		TestEntityFactory.getNewUncertaintyValue(getDaoManager(), null, newUncertaintyParam, null);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyParam(newUncertaintyParam);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<UncertaintyParam> findAllParam = getDaoManager().getRepository(IUncertaintyParamRepository.class)
				.findAll();
		assertNotNull(findAllParam);
		assertTrue(findAllParam.isEmpty());

		List<UncertaintyValue> findAllValues = getDaoManager().getRepository(IUncertaintyValueRepository.class)
				.findAll();
		assertNotNull(findAllValues);
		assertTrue(findAllValues.isEmpty());

		List<UncertaintySelectValue> findAllSelectValues = getDaoManager()
				.getRepository(IUncertaintySelectValueRepository.class).findAll();
		assertNotNull(findAllSelectValues);
		assertTrue(findAllSelectValues.isEmpty());

		List<UncertaintyConstraint> findAllConstraints = getDaoManager()
				.getRepository(IUncertaintyConstraintRepository.class).findAll();
		assertNotNull(findAllConstraints);
		assertTrue(findAllConstraints.isEmpty());
	}

	/* ************ deleteAllUncertaintyValue ************* */

	@Test
	void test_deleteAllUncertaintyValue_Working() {

		// construct data
		// Kept
		UncertaintyValue newUncertaintyValue = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), null, null,
				null);
		// to delete
		UncertaintyValue newUncertaintyValue2 = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), null, null,
				null);
		UncertaintyValue newUncertaintyValue3 = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), null, null,
				null);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class)
					.deleteAllUncertaintyValue(Arrays.asList(newUncertaintyValue2, newUncertaintyValue3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<UncertaintyValue> findAll = getDaoManager().getRepository(IUncertaintyValueRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newUncertaintyValue, findAll.iterator().next());
	}

	/* ************ deleteUncertaintyValue ************* */

	@Test
	void test_deleteUncertaintyValue_Working() {

		// construct data
		// Kept
		UncertaintyValue newUncertaintyValue = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), null, null,
				null);
		// to delete
		UncertaintyValue newUncertaintyValue2 = TestEntityFactory.getNewUncertaintyValue(getDaoManager(), null, null,
				null);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyValue(newUncertaintyValue2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<UncertaintyValue> findAll = getDaoManager().getRepository(IUncertaintyValueRepository.class).findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newUncertaintyValue, findAll.iterator().next());
	}

	@Test
	void test_deleteUncertaintyValue_Null() {
		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyValue(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYVALUE_NULL), e.getMessage());
		}
	}

	@Test
	void test_deleteUncertaintyValue_IdNull() {
		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintyValue(new UncertaintyValue());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYVALUE_IDNULL), e.getMessage());
		}
	}

	/* ************ deleteAllUncertaintySelectValue ************* */

	@Test
	void test_deleteAllUncertaintySelectValue_Working() {

		// construct data
		UncertaintyParam parameter = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		// Kept
		UncertaintySelectValue newUncertaintySelectValue = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				UncertaintySelectValue.class, parameter, IUncertaintySelectValueRepository.class);
		// to delete
		UncertaintySelectValue newUncertaintySelectValue2 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				UncertaintySelectValue.class, parameter, IUncertaintySelectValueRepository.class);
		UncertaintySelectValue newUncertaintySelectValue3 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				UncertaintySelectValue.class, parameter, IUncertaintySelectValueRepository.class);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteAllUncertaintySelectValue(
					Arrays.asList(newUncertaintySelectValue2, newUncertaintySelectValue3));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<UncertaintySelectValue> findAll = getDaoManager().getRepository(IUncertaintySelectValueRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newUncertaintySelectValue, findAll.iterator().next());
	}

	/* ************ deleteUncertaintySelectValue ************* */

	@Test
	void test_deleteUncertaintySelectValue_Working() {

		// construct data
		UncertaintyParam parameter = TestEntityFactory.getNewUncertaintyParam(getDaoManager(), null, null);
		// Kept
		UncertaintySelectValue newUncertaintySelectValue = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				UncertaintySelectValue.class, parameter, IUncertaintySelectValueRepository.class);
		// to delete
		UncertaintySelectValue newUncertaintySelectValue2 = TestEntityFactory.getNewGenericSelectValue(getDaoManager(),
				UncertaintySelectValue.class, parameter, IUncertaintySelectValueRepository.class);

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class)
					.deleteUncertaintySelectValue(newUncertaintySelectValue2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test
		List<UncertaintySelectValue> findAll = getDaoManager().getRepository(IUncertaintySelectValueRepository.class)
				.findAll();
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newUncertaintySelectValue, findAll.iterator().next());
	}

	@Test
	void test_deleteUncertaintySelectValue_Null() {

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class).deleteUncertaintySelectValue(null);
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYSELECTVALUE_NULL),
					e.getMessage());
		}
	}

	@Test
	void test_deleteUncertaintySelectValue_IdNull() {

		// delete
		try {
			getAppManager().getService(IUncertaintyApplication.class)
					.deleteUncertaintySelectValue(new UncertaintySelectValue());
			fail("This shouldn't work"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYSELECTVALUE_IDNULL),
					e.getMessage());
		}
	}

	/* ************ isUncertaintyEnabled ************* */

	@Test
	void test_isUncertaintyEnabled_Enabled() {

		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);

		boolean UncertaintyEnabled = getAppManager().getService(IUncertaintyApplication.class)
				.isUncertaintyEnabled(newModel);
		assertTrue(UncertaintyEnabled);
	}

	@Test
	void test_isUncertaintyEnabled_Disabled() {
		boolean UncertaintyEnabled = getAppManager().getService(IUncertaintyApplication.class)
				.isUncertaintyEnabled(TestEntityFactory.getNewModel(getDaoManager()));
		assertFalse(UncertaintyEnabled);
	}

	@Test
	void test_sameConfiguration() {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		TestEntityFactory.getNewUncertaintyParam(getDaoManager(), newModel, null);

		UncertaintySpecification spec = getAppManager().getService(IUncertaintyApplication.class)
				.loadUncertaintyConfiguration(newModel);

		assertFalse(getAppManager().getService(IUncertaintyApplication.class).sameConfiguration(null, spec));
		assertFalse(getAppManager().getService(IUncertaintyApplication.class).sameConfiguration(spec, null));
		assertTrue(getAppManager().getService(IUncertaintyApplication.class).sameConfiguration(spec, spec));
		assertTrue(getAppManager().getService(IUncertaintyApplication.class).sameConfiguration(null, null));
	}

	/* ************ refresh ************* */

	@Test
	void test_refreshUncertaintyGroup() {

		UncertaintyGroup newUncertaintyGroup = TestEntityFactory.getNewUncertaintyGroup(getDaoManager(), null);

		// uncertainty in the group
		TestEntityFactory.getNewUncertainty(getDaoManager(), newUncertaintyGroup, null);
		TestEntityFactory.getNewUncertainty(getDaoManager(), newUncertaintyGroup, null);

		// uncertainty not in the group
		TestEntityFactory.getNewUncertainty(getDaoManager(), null, null);

		getAppManager().getService(IUncertaintyApplication.class).refresh(newUncertaintyGroup);

		// test
		assertNotNull(newUncertaintyGroup.getUncertainties());
		assertFalse(newUncertaintyGroup.getUncertainties().isEmpty());
		assertEquals(2, newUncertaintyGroup.getUncertainties().size());
	}

}
