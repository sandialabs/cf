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
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IARGParametersQoIOptionRepository;
import gov.sandia.cf.dao.IPhenomenonGroupRepository;
import gov.sandia.cf.dao.IPhenomenonRepository;
import gov.sandia.cf.dao.IQoIHeaderRepository;
import gov.sandia.cf.dao.IQoIPlanningValueRepository;
import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParametersQoIOption;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestTools;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PIRT Application Controller
 */
@RunWith(JUnitPlatform.class)
class PIRTApplicationQoiTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(PIRTApplicationQoiTest.class);

	@Test
	void testQoiCRUDWorking() {

		// create test entities
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		// ***********
		// Create QoI
		// ***********
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setSymbol("My_QOI"); //$NON-NLS-1$
		qoi.setModel(model);
		qoi.setCreationDate(new Date());
		try {
			qoi = getPIRTApp().addQoI(qoi, newUser);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create phenomenon group
		PhenomenonGroup group = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), qoi);
		assertNotNull(group);
		getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(qoi);
		qoi.getPhenomenonGroupList().add(null);

		// create phenomenon
		Phenomenon phenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), group);
		assertNotNull(phenomenon);
		getDaoManager().getRepository(IPhenomenonGroupRepository.class).refresh(group);
		group.getPhenomenonList().add(null);

		// create criterion
		Criterion criterion = TestEntityFactory.getNewCriterion(getDaoManager(), phenomenon);
		assertNotNull(criterion);
		getDaoManager().getRepository(IPhenomenonRepository.class).refresh(phenomenon);
		phenomenon.getCriterionList().add(null);

		// Tests
		assertNotNull(qoi);
		assertNotNull(qoi.getId());
		assertNotNull(qoi.getModel());
		assertEquals("My_QOI", qoi.getSymbol()); //$NON-NLS-1$

		// ****************
		// Retrieve by Id
		// ****************
		try {
			QuantityOfInterest foundQoi = getPIRTApp().getQoIById(qoi.getId());
			assertNotNull(foundQoi);
			assertEquals(foundQoi.getId(), qoi.getId());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ****************
		// Get all
		// ****************
		List<QuantityOfInterest> list = getPIRTApp().getQoIList(model);
		assertFalse(list.isEmpty());

		// **********************************
		// Create with PIRTDescriptionHeader and QoI Planning
		// **********************************

		QuantityOfInterest copy = qoi.copy();
		copy.setSymbol("qoiWithHeader"); //$NON-NLS-1$

		// QoI Headers
		PIRTDescriptionHeader descHeader = new PIRTDescriptionHeader("My_Description_Header", "1"); //$NON-NLS-1$ //$NON-NLS-2$
		List<PIRTDescriptionHeader> descHeaders = new ArrayList<>();
		descHeaders.add(descHeader);

		// QoI Planning
		QoIPlanningValue newQoIPlanningValue1 = new QoIPlanningValue();
		newQoIPlanningValue1.setDateCreation(new Date());
		newQoIPlanningValue1.setParameter(TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), model));
		newQoIPlanningValue1.setQoi(copy);
		newQoIPlanningValue1.setUserCreation(newUser);
		newQoIPlanningValue1.setValue("MYVAL"); //$NON-NLS-1$

		QoIPlanningValue newQoIPlanningValue2 = new QoIPlanningValue();
		newQoIPlanningValue2.setDateCreation(new Date());
		newQoIPlanningValue2.setParameter(TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), model));
		newQoIPlanningValue2.setQoi(copy);
		newQoIPlanningValue2.setUserCreation(newUser);
		newQoIPlanningValue2.setValue("MYVAL2"); //$NON-NLS-1$

		copy.setQoiPlanningList(Arrays.asList(newQoIPlanningValue1, newQoIPlanningValue2));

		QuantityOfInterest qoiWithHeader = null;
		try {
			qoiWithHeader = getPIRTApp().addQoI(copy, newUser, descHeaders);
			assertNotNull(qoiWithHeader);
			assertEquals(1, qoiWithHeader.getQoiHeaderList().size());
			assertEquals(2, qoiWithHeader.getQoiPlanningList().size());
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// construct data
		PhenomenonGroup newPhenomenonGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), qoiWithHeader);
		TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), qoiWithHeader);
		TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), qoiWithHeader);

		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);
		getDaoManager().getRepository(IPhenomenonGroupRepository.class).refresh(newPhenomenonGroup);
		getDaoManager().getRepository(IQoIPlanningValueRepository.class).refresh(newQoIPlanningValue1);
		getDaoManager().getRepository(IQoIPlanningValueRepository.class).refresh(newQoIPlanningValue2);
		getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(qoiWithHeader);

		// ***********
		// Update QoI
		// ***********
		qoi.setSymbol("My_QoI_Updated"); //$NON-NLS-1$
		try {
			QuantityOfInterest qoiUpdated = getPIRTApp().updateQoI(qoi, newUser);
			assertNotNull(qoiUpdated);
			assertEquals(qoiUpdated.getId(), qoi.getId());
			assertEquals("My_QoI_Updated", qoiUpdated.getSymbol()); //$NON-NLS-1$
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// ***********
		// Tag QoI
		// ***********
		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		QuantityOfInterest taggedQoI = null;
		try {
			taggedQoI = getPIRTApp().tagQoI(qoiWithHeader, "My_Tag", defaultUser); //$NON-NLS-1$
			assertNotNull(taggedQoI);
			assertNotNull(taggedQoI.getTag());
			assertNotEquals(taggedQoI.getId(), qoi.getId());
			assertEquals(qoiWithHeader, taggedQoI.getParent());
			assertEquals(1, taggedQoI.getQoiHeaderList().size());
			assertEquals(2, taggedQoI.getQoiPlanningList().size());
			getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(qoiWithHeader);
			assertEquals(1, qoiWithHeader.getChildren().size());
			assertTrue(qoiWithHeader.getChildren().contains(taggedQoI));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// **********
		// Delete QoI with tag
		// **********
		try {
			getPIRTApp().deleteQoI(qoiWithHeader);
			assertNull(getPIRTApp().getQoIById(qoiWithHeader.getId()));
			assertNull(getPIRTApp().getQoIById(taggedQoI.getId()));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// **********
		// Delete QoI and Tag
		// **********
		try {
			getPIRTApp().deleteQoI(qoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		list = getPIRTApp().getQoIList(model);
		assertTrue(list.isEmpty());
	}

	@Test
	void testGetQoIByIdErrors() {
		// *********
		// With null
		// *********
		try {
			getAppManager().getService(IPIRTApplication.class).getQoIById(null);
			fail("Can launch getQoIById with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_GETQOIBYID_IDNULL));
		}
	}

	@Test
	void testAddQoIWithParametersNull() {
		QuantityOfInterest qoi = new QuantityOfInterest();

		// *******************************************************
		// Able to create QoI with no date creation
		// *******************************************************
		try {
			// create model
			Model model = TestEntityFactory.getNewModel(getDaoManager());
			assertNotNull(model);
			User newUser = TestEntityFactory.getNewUser(getDaoManager());

			// *****************************************
			// With date creation null
			// *****************************************
			qoi = new QuantityOfInterest();
			qoi.setModel(model);
			qoi.setSymbol("Test"); //$NON-NLS-1$
			qoi.setCreationDate(new Date());
			qoi = getPIRTApp().addQoI(qoi, newUser);
			assertNotNull(qoi);
			// Remove it
			getPIRTApp().deleteQoI(qoi);

			// ****************************************
			// With PIRTDescriptionHeader null
			// ****************************************
			qoi = getPIRTApp().addQoI(qoi, newUser);
			assertNotNull(qoi);
			// remove it
			getPIRTApp().deleteQoI(qoi);

			// *****************************************
			// With PIRTDescriptionHeader empty
			// *****************************************
			qoi = getPIRTApp().addQoI(qoi, newUser, new ArrayList<>());
			assertNotNull(qoi);
			// remove it
			getPIRTApp().deleteQoI(qoi);

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testAddQoIErrorQoINull() {

		// *********
		// With null
		// *********
		try {
			getPIRTApp().addQoI(null, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Can launch addQoI with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_ADDQOI_QOINULL));
		}
	}

	@Test
	void testAddQoIWithHeadersErrorQoINullDescriptionNull() {

		// ****************************************
		// With null and PIRTDescriptionHeader null
		// ****************************************
		List<PIRTDescriptionHeader> descHeaders = null;
		try {
			getPIRTApp().addQoI(null, TestEntityFactory.getNewUser(getDaoManager()), descHeaders);
			fail("Can launch addQoI with null and description header null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_ADDQOI_QOINULL));
		}
	}

	@Test
	void testAddQoIWithHeaderErrorQoINullDescriptionEmpty() {

		// *****************************************
		// With null and PIRTDescriptionHeader empty
		// *****************************************
		List<PIRTDescriptionHeader> descHeaders = new ArrayList<>();
		try {
			getPIRTApp().addQoI(null, TestEntityFactory.getNewUser(getDaoManager()), descHeaders);
			fail("Can launch addQoI with null and description header empty"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_ADDQOI_QOINULL));
		}
	}

	@Test
	void testAddQoIErrorModelNull() {
		QuantityOfInterest qoi = new QuantityOfInterest();

		// *************
		// With no model
		// *************
		try {
			getPIRTApp().addQoI(qoi, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Can launch addQoI with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOI_MODEL_NULL));
		}
	}

	@Test
	void testAddQoIErrorNameNull() {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// Set data
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(model);
		qoi.setSymbol(null);

		try {
			getPIRTApp().addQoI(qoi, TestEntityFactory.getNewUser(getDaoManager()));
			fail("It should fail. It's impossible to create a qoi with name null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOI_SYMBOL_BLANK));
		}
	}

	@Test
	void testAddQoI_Error_NameEmpty() {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// Set data
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(model);
		qoi.setSymbol(""); //$NON-NLS-1$

		try {
			getPIRTApp().addQoI(qoi, TestEntityFactory.getNewUser(getDaoManager()));
			fail("It should fail. It's impossible to create a qoi with name null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOI_SYMBOL_BLANK));
		}
	}

	@Test
	void testAddQoIErrorNameDuplicated() {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		String qoiName = "Test"; //$NON-NLS-1$

		// Set qoi1 name
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(model);
		qoi.setSymbol(qoiName);
		try {
			qoi = getPIRTApp().addQoI(qoi, TestEntityFactory.getNewUser(getDaoManager()));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		try {
			assertTrue(getPIRTApp().existsQoISymbol(null, qoiName));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Set qoi2 name
		QuantityOfInterest qoi2 = new QuantityOfInterest();
		qoi2.setModel(model);
		qoi2.setSymbol(qoiName);

		try {
			getPIRTApp().addQoI(qoi2, TestEntityFactory.getNewUser(getDaoManager()));
			fail("It should fail. It's impossible to create a qoi with the name as another one."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_ADDQOI_NAMEDUPLICATED));
		}

		try {
			getPIRTApp().deleteQoI(qoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testExistQoINameWorking() {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		String qoiName = "Test"; //$NON-NLS-1$

		// Set data
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(model);
		qoi.setSymbol(qoiName);
		try {
			qoi = getPIRTApp().addQoI(qoi, TestEntityFactory.getNewUser(getDaoManager()));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Check qoi exists
		try {
			assertTrue(getPIRTApp().existsQoISymbol(null, qoiName));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		try {
			getPIRTApp().deleteQoI(qoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testExistQoINameWorkingWithTagName() {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		String qoiName = "Test"; //$NON-NLS-1$

		// Create qoi
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(model);
		qoi.setSymbol(qoiName);
		try {
			qoi = getPIRTApp().addQoI(qoi, TestEntityFactory.getNewUser(getDaoManager()));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// tag it
		QuantityOfInterest taggedQoI = null;
		try {
			taggedQoI = getPIRTApp().tagQoI(qoi, "My_Tag", defaultUser); //$NON-NLS-1$
			assertNotNull(taggedQoI);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// update qoi without changing tag
		qoi.setSymbol(qoiName + "Suffix"); //$NON-NLS-1$
		try {
			getPIRTApp().updateQoI(qoi, TestEntityFactory.getNewUser(getDaoManager()));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Check qoi exists
		try {
			assertTrue(getPIRTApp().existsQoISymbol(null, qoiName));
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		try {
			getPIRTApp().deleteQoI(taggedQoI);
			getPIRTApp().deleteQoI(qoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testUpdateQoI_QoiNull() {
		try {
			getPIRTApp().updateQoI(null, TestEntityFactory.getNewUser(getDaoManager()));
			fail("It should throw a credibility exception if the qoi is null."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_QOINULL));
		}
	}

	@Test
	void testUpdateQoI_IdNull() {
		try {
			getPIRTApp().updateQoI(new QuantityOfInterest(), TestEntityFactory.getNewUser(getDaoManager()));
			fail("It should throw a credibility exception if the qoi id is null."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_IDNULL));
		}
	}

	@Test
	void testUpdateQoI_NameAlreadyExists() {
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), null);
		String name = newQoI.getSymbol();

		QuantityOfInterest qoiToUpdate = TestEntityFactory.getNewQoI(getDaoManager(), null);
		qoiToUpdate.setSymbol(name);

		try {
			getPIRTApp().updateQoI(qoiToUpdate, TestEntityFactory.getNewUser(getDaoManager()));
			fail("It should throw a credibility exception if the qoi name already exists."); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_NAMEDUPLICATED));
		}
	}

	@Test
	void test_deleteQoI_Working() {

		// construct data
		// kept
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		QuantityOfInterest newQoITag = TestEntityFactory.getNewQoIWithParent(getDaoManager(), newQoI);
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI, null);
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoITag, null);
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, newQoI, null);
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, newQoITag, null);
		PhenomenonGroup newPhenomenonGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);
		PhenomenonGroup newPhenomenonGroupTag = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoITag);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroupTag);

		// to delete
		QuantityOfInterest newQoIToDelete = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		QuantityOfInterest newQoIToDeleteTag = TestEntityFactory.getNewQoIWithParent(getDaoManager(), newQoIToDelete);
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoIToDelete, null);
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoIToDeleteTag, null);
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, newQoIToDelete, null);
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(), null, newQoIToDeleteTag, null);
		PhenomenonGroup newPhenomenonToDeleteGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(),
				newQoIToDelete);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonToDeleteGroup);
		PhenomenonGroup newPhenomenonToDeleteGroupTag = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(),
				newQoIToDeleteTag);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonToDeleteGroupTag);
		TestEntityFactory.getNewARGParametersQoIOption(getDaoManager(), null, newQoIToDelete, newQoIToDelete);
		TestEntityFactory.getNewARGParametersQoIOption(getDaoManager(), null, newQoIToDelete, newQoIToDeleteTag);

		// delete
		try {
			getAppManager().getService(IPIRTApplication.class).deleteQoI(newQoIToDelete);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// Test
		List<QuantityOfInterest> findAll = getAppManager().getService(IPIRTApplication.class).getRootQoI(newModel);
		assertNotNull(findAll);
		assertEquals(1, findAll.size());
		assertEquals(newQoI, findAll.iterator().next());
		assertEquals(1, newQoI.getChildren().size());
		assertEquals(newQoITag, newQoI.getChildren().iterator().next());

		List<QoIHeader> findAllHeaders = getDaoManager().getRepository(IQoIHeaderRepository.class).findAll();
		assertNotNull(findAllHeaders);
		assertEquals(2, findAllHeaders.size());

		List<QoIPlanningValue> findAllPlanningValue = getDaoManager().getRepository(IQoIPlanningValueRepository.class)
				.findAll();
		assertNotNull(findAllPlanningValue);
		assertEquals(2, findAllPlanningValue.size());

		List<PhenomenonGroup> findAllPhenGroup = getDaoManager().getRepository(IPhenomenonGroupRepository.class)
				.findAll();
		assertNotNull(findAllPhenGroup);
		assertEquals(2, findAllPhenGroup.size());

		List<Phenomenon> findAllPhen = getDaoManager().getRepository(IPhenomenonRepository.class).findAll();
		assertNotNull(findAllPhen);
		assertEquals(2, findAllPhen.size());

		List<ARGParametersQoIOption> findAllARGParametersQoIOption = getDaoManager()
				.getRepository(IARGParametersQoIOptionRepository.class).findAll();
		assertNotNull(findAllARGParametersQoIOption);
		assertEquals(0, findAllARGParametersQoIOption.size());

	}

	@Test
	void testDeleteQoIErrors() {

		// **********
		// With null
		// **********
		try {
			getPIRTApp().deleteQoI(null);
			fail("Can launch deleteQoI with null and description header null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(e.getMessage(), RscTools.getString(RscConst.EX_PIRT_DELETEQOI_QOINULL));
		}

		try {
			// create model
			Model model = TestEntityFactory.getNewModel(getDaoManager());
			assertNotNull(model);

			// ***********
			// Create QoI
			// ***********
			QuantityOfInterest qoi = new QuantityOfInterest();
			qoi.setSymbol("My_QOI"); //$NON-NLS-1$
			qoi.setModel(model);
			qoi.setCreationDate(new Date());
			qoi = getPIRTApp().addQoI(qoi, TestEntityFactory.getNewUser(getDaoManager()));
			qoi.setPhenomenonGroupList(null);
			qoi.setQoiHeaderList(null);

			getPIRTApp().deleteQoI(qoi);
			assertNull(getPIRTApp().getQoIById(qoi.getId()));
		} catch (CredibilityException e) {
			fail("Can't launch deleteQoI with empty list of header and phenomenon"); //$NON-NLS-1$
		}
	}

	@Test
	void testResetQoI_Working() {

		// Initialize
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		QuantityOfInterest qoi = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		PhenomenonGroup newPhenomenonGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), qoi);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);
		PhenomenonGroup newPhenomenonGroup2 = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), qoi);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup2);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup2);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup2);

		try {
			qoi = getPIRTApp().resetQoI(qoi);
		} catch (CredibilityException e) {
			fail("Can't launch resestQoI with null and description header null"); //$NON-NLS-1$
		}

		assertNotNull(qoi);
		assertTrue(qoi.getPhenomenonGroupList().isEmpty());
	}

	@Test
	void testResetQoIErrors() {

		// **********
		// With null
		// **********
		try {
			getPIRTApp().resetQoI(null);
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_QOINULL), e.getMessage());
		}

		// ************
		// With no data
		// ************
		try {
			getPIRTApp().resetQoI(new QuantityOfInterest());
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATEQOI_IDNULL), e.getMessage());
		}
	}

	@Test
	void testTagQoIErrors() {
		QuantityOfInterest qoi = null;
		String tagDescription = null;

		// create user
		User defaultUser = TestEntityFactory.getNewUser(getDaoManager());
		assertNotNull(defaultUser);

		// **********
		// With null
		// **********
		try {
			getPIRTApp().tagQoI(qoi, tagDescription, defaultUser);
			fail("Can launch tagQoI with null qoi"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_TAG_QOINULL), e.getMessage());
		}

		// *************
		// With no model
		// *************
		qoi = new QuantityOfInterest();
		try {
			getPIRTApp().tagQoI(qoi, tagDescription, defaultUser);
			fail("Can launch tagQoI with null qoi"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_TAG_MODELNULL), e.getMessage());
		}
	}

	@Test
	void testGetRootQoI() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(createdModel);
		qoi.setSymbol("Name"); //$NON-NLS-1$
		qoi.setCreationDate(new Date());
		QuantityOfInterest createdQoi = null;
		try {
			createdQoi = getDaoManager().getRepository(IQuantityOfInterestRepository.class).create(qoi);
			assertNotNull(createdQoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create Qoi2
		QuantityOfInterest qoi2 = new QuantityOfInterest();
		qoi2.setModel(createdModel);
		qoi2.setSymbol("Name"); //$NON-NLS-1$
		qoi2.setCreationDate(new Date());
		QuantityOfInterest createdQoi2 = null;
		try {
			createdQoi2 = getDaoManager().getRepository(IQuantityOfInterestRepository.class).create(qoi2);
			assertNotNull(createdQoi2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create tag1
		QuantityOfInterest tag = qoi.copy();
		tag.setTag("HASHTAG"); //$NON-NLS-1$
		tag.setTagDate(DateTools.getCurrentDate());
		tag.setParent(qoi);
		QuantityOfInterest createdTag1 = null;
		try {
			createdTag1 = getDaoManager().getRepository(IQuantityOfInterestRepository.class).create(tag);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// create tag2
		QuantityOfInterest tag2 = qoi.copy();
		tag2.setTag("HASHTAG"); //$NON-NLS-1$
		tag2.setTagDate(DateTools.getCurrentDate());
		tag2.setParent(qoi);
		QuantityOfInterest createdTag2 = null;
		try {
			createdTag2 = getDaoManager().getRepository(IQuantityOfInterestRepository.class).create(tag2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		List<QuantityOfInterest> foundList = getAppManager().getService(IPIRTApplication.class)
				.getRootQoI(createdModel);
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());
		assertEquals(2, foundList.size());

		assertTrue(foundList.contains(createdQoi));
		assertTrue(foundList.contains(createdQoi2));
		assertFalse(foundList.contains(createdTag1));
		assertFalse(foundList.contains(createdTag2));
	}

	@Test
	void testFindQoiIdByModelId() {

		// create model
		Model model = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(model);
		assertNotNull(model.getId());

		// create QoI
		QuantityOfInterest qoi = TestEntityFactory.getNewQoI(getDaoManager(), model);
		assertNotNull(qoi);

		// test case
		List<Integer> foundList = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.findQoiIdByModelId(model.getId());
		assertNotNull(foundList);
		assertFalse(foundList.isEmpty());

		for (Integer found : foundList) {
			assertNotNull(found);
		}
	}

	@Test
	void testFindQoiIdByModelIdModelIdNull() {

		// test case
		List<Integer> foundList = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.findQoiIdByModelId(null);
		assertNotNull(foundList);
		assertTrue(foundList.isEmpty());
	}

	@Test
	void testDuplicateQoI_Working() {

		// construct data
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI, newUser);
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI, newUser);
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(),
				TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newQoI.getModel()), newQoI, newUser);
		TestEntityFactory.getNewQoIPlanningValue(getDaoManager(),
				TestEntityFactory.getNewQoIPlanningParam(getDaoManager(), newQoI.getModel()), newQoI, newUser);
		PhenomenonGroup newPhenomenonGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);

		getDaoManager().getRepository(IPhenomenonGroupRepository.class).refresh(newPhenomenonGroup);
		getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(newQoI);

		QuantityOfInterest duplicatedQoi = new QuantityOfInterest();
		duplicatedQoi.setSymbol("QOI Name"); //$NON-NLS-1$
		duplicatedQoi.setDescription("New description"); //$NON-NLS-1$

		// test
		try {
			QuantityOfInterest duplicateQoI = getPIRTApp().duplicateQoI(newQoI, duplicatedQoi, newUser);

			assertNotNull(duplicateQoI);
			assertNotNull(duplicateQoI.getId());
			assertEquals(newQoI.getQoiHeaderList().size(), duplicateQoI.getQoiHeaderList().size());
			assertEquals(newQoI.getQoiPlanningList().size(), duplicateQoI.getQoiPlanningList().size());
			assertEquals(newQoI.getPhenomenonGroupList().size(), duplicateQoI.getPhenomenonGroupList().size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	/* ************ duplicateQoI ************* */

	@Test
	void testDuplicateQoI_Working_DuplicateQoiNull() {

		// construct data
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), null);
		User newUser = TestEntityFactory.getNewUser(getDaoManager());
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI, newUser);
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI, newUser);
		PhenomenonGroup newPhenomenonGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);
		TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);

		getDaoManager().getRepository(IPhenomenonGroupRepository.class).refresh(newPhenomenonGroup);
		getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(newQoI);

		// test
		try {
			QuantityOfInterest duplicateQoI = getPIRTApp().duplicateQoI(newQoI, null,
					TestEntityFactory.getNewUser(getDaoManager()));

			assertNotNull(duplicateQoI);
			assertNotNull(duplicateQoI.getId());
			assertEquals(newQoI.getQoiHeaderList().size(), duplicateQoI.getQoiHeaderList().size());
			assertEquals(newQoI.getPhenomenonGroupList().size(), duplicateQoI.getPhenomenonGroupList().size());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testDuplicateQoI_QoiNull() {

		// test
		try {
			getPIRTApp().duplicateQoI(null, null, TestEntityFactory.getNewUser(getDaoManager()));
			fail("It should fail if the qoi is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DUPLICATEQOI_QOINULL), e.getMessage());
		}
	}

	@Test
	void testDuplicateQoI_UserNull() {

		// test
		try {
			getPIRTApp().duplicateQoI(TestEntityFactory.getNewQoI(getDaoManager(), null), null, null);
			fail("It should fail if the user is null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_ADDQOI_USERNULL), e.getMessage());
		}
	}

}
