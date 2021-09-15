/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import javax.persistence.RollbackException;
import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;
import gov.sandia.cf.tests.TestTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * @author Maxime N.
 *
 *         JUnit test class for the PIRT Application Controller
 */
@RunWith(JUnitPlatform.class)
class PIRTApplicationQoIHeaderTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PIRTApplicationQoIHeaderTest.class);

	@Test
	void testQoiHeaderCRUDWorking() {

		try {

			// create model
			Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
			assertNotNull(createdModel);

			// create QoI
			QuantityOfInterest qoi = TestEntityFactory.getNewQoI(getDaoManager(), createdModel);
			assertNotNull(createdModel);

			// create user
			User newUser = TestEntityFactory.getNewUser(getDaoManager());

			// *****************
			// Create QoI Header
			// *****************
			QoIHeader qoiHeader = new QoIHeader();
			qoiHeader.setName("My_Name"); //$NON-NLS-1$
			qoiHeader.setValue("My_Value"); //$NON-NLS-1$
			qoiHeader.setQoi(qoi);
			getPIRTApp().addQoIHeader(qoiHeader, newUser);
			assertNotNull(qoiHeader);
			assertNotNull(qoiHeader.getId());

			// *****************
			// Get all
			// *****************
			List<QoIHeader> qoIHeaders = getPIRTApp().getQoIHeaders();
			assertNotNull(qoIHeaders);
			assertFalse(qoIHeaders.isEmpty());

			// *****************
			// Update Qoi Header
			// *****************
			qoiHeader.setName("My_Name_Updated"); //$NON-NLS-1$
			QoIHeader qoiHeaderUpdated = getPIRTApp().updateQoIHeader(qoiHeader, newUser);
			assertNotNull(qoiHeaderUpdated);
			assertEquals(qoiHeader.getId(), qoiHeaderUpdated.getId());
			assertEquals("My_Name_Updated", qoiHeaderUpdated.getName()); //$NON-NLS-1$

			// *****************
			// Delete Qoi Header
			// *****************
			getPIRTApp().deleteQoIHeader(qoiHeader);
			qoIHeaders = getPIRTApp().getQoIHeaders();
			assertTrue(qoIHeaders.isEmpty());

		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
	}

	@Test
	void testAddQoIHeader_Error_Null() {

		// **********
		// With null
		// **********
		try {
			getPIRTApp().addQoIHeader(null, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Add a QoI Header null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_ADDQOIHEADER_QOIHEADERNULL), e.getMessage());
		}
	}

	@Test
	void testAddQoIHeader_Error_UserNull() {

		// **********
		// With null
		// **********
		try {
			getPIRTApp().addQoIHeader(TestEntityFactory.getNewQoIHeader(getDaoManager(), null, null), null);
			fail("Add a QoI Header with user null"); //$NON-NLS-1$
		} catch (RollbackException | CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOIHEADER_USERCREATION_NULL));
		}
	}

	@Test
	void testAddQoIHeader_Error_NameNull() {

		// create QoI
		QuantityOfInterest qoi = TestEntityFactory.getNewQoI(getDaoManager(), null);
		assertNotNull(qoi);

		// **************
		// With name null
		// **************
		QoIHeader qoiHeader = new QoIHeader();
		qoiHeader.setQoi(qoi);
		try {
			getPIRTApp().addQoIHeader(qoiHeader, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Add a QoI Header with name null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOIHEADER_NAME_BLANK));
		}
	}

	@Test
	void testAddQoIHeader_Error_NameEmpty() {

		// create QoI
		QuantityOfInterest qoi = TestEntityFactory.getNewQoI(getDaoManager(), null);
		assertNotNull(qoi);

		// ***************
		// With name empty
		// ***************
		QoIHeader qoiHeader = new QoIHeader();
		qoiHeader.setName(""); //$NON-NLS-1$
		qoiHeader.setQoi(qoi);
		try {
			getPIRTApp().addQoIHeader(qoiHeader, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Add a QoI Header with name empty"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOIHEADER_NAME_BLANK));
		}
	}

	@Test
	void testAddQoIHeader_Error_QoINull() {

		// **************
		// With qoi null
		// **************
		QoIHeader qoiHeader = new QoIHeader();
		qoiHeader.setName("My_Name"); //$NON-NLS-1$
		qoiHeader.setQoi(null);
		try {
			getPIRTApp().addQoIHeader(qoiHeader, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Add a QoI Header with qoi null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOIHEADER_QOI_NULL));
		}
	}

	@Test
	void testUpdateQoIHeader_Error_Null() {

		// **********
		// With null
		// **********
		try {
			getPIRTApp().updateQoIHeader(null, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Update a QoI Header with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATEQOIHEADER_QOIHEADERNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateQoIHeader_Error_IdNull() {
		// **************
		// With id null
		// **************
		QoIHeader qoiHeader = new QoIHeader();
		try {
			getPIRTApp().updateQoIHeader(qoiHeader, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Update a QoI Header with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_UPDATEQOIHEADER_IDNULL), e.getMessage());
		}
	}

	@Test
	void testUpdateQoIHeader_Error_NameNull() {

		// create QoIHeader
		QoIHeader qoiHeader = TestEntityFactory.getNewQoIHeader(getDaoManager(), null, null);
		assertNotNull(qoiHeader);

		// **************
		// With name null
		// **************
		qoiHeader.setName(null);
		try {
			getPIRTApp().updateQoIHeader(qoiHeader, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Update a QoI Header with name null"); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOIHEADER_NAME_BLANK));
		}
	}

	@Test
	void testUpdateQoIHeader_Error_NameEmpty() {

		// create QoIHeader
		QoIHeader qoiHeader = TestEntityFactory.getNewQoIHeader(getDaoManager(), null, null);
		assertNotNull(qoiHeader);

		// ***************
		// With name empty
		// ***************
		qoiHeader.setName(""); //$NON-NLS-1$
		try {
			getPIRTApp().updateQoIHeader(qoiHeader, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Update a QoI Header with name empty"); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOIHEADER_NAME_BLANK));
		}

	}

	@Test
	void testUpdateQoIHeader_Error_QoINull() {

		// create QoIHeader
		QoIHeader qoiHeader = TestEntityFactory.getNewQoIHeader(getDaoManager(), null, null);
		assertNotNull(qoiHeader);

		// **************
		// With qoi null
		// **************
		qoiHeader.setQoi(null);
		try {
			getPIRTApp().updateQoIHeader(qoiHeader, TestEntityFactory.getNewUser(getDaoManager()));
			fail("Update a QoI Header with qoi null"); //$NON-NLS-1$
		} catch (CredibilityException | RollbackException e) {
			assertTrue(e.getCause() instanceof ConstraintViolationException);
			assertTrue(TestTools.containsConstraintViolationException(((ConstraintViolationException) e.getCause()),
					RscConst.EX_QOIHEADER_QOI_NULL));
		}
	}

	@Test
	void testDeleteQoIHeader_ErrorIdNull() {

		// **********
		// With null
		// **********
		try {
			getPIRTApp().deleteQoIHeader(null);
			fail("Delete a QoI Header with null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DELETEQOIHEADER_QOIHEADERNULL), e.getMessage());
		}

		// **************
		// With id null
		// **************
		QoIHeader qoiHeader = new QoIHeader();
		try {
			getPIRTApp().deleteQoIHeader(qoiHeader);
			fail("Delete a QoI Header with id null"); //$NON-NLS-1$
		} catch (CredibilityException e) {
			assertEquals(RscTools.getString(RscConst.EX_PIRT_DELETEQOIHEADER_IDNULL), e.getMessage());
		}
	}
}
