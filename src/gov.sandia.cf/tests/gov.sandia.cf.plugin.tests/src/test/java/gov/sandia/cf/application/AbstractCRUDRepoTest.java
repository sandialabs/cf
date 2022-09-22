/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit test class for the Abstract CRUD repository
 * 
 * @author Didier Verstraete
 *
 */
class AbstractCRUDRepoTest extends AbstractTestApplication {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(AbstractCRUDRepoTest.class);

	@Test
	void test_isUnique_True_WithoutExceptId() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		String qoiName = "My Name of QoI"; //$NON-NLS-1$
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(createdModel);
		qoi.setSymbol(qoiName);
		qoi.setCreationDate(new Date());
		try {
			QuantityOfInterest createdQoi = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
					.create(qoi);
			assertNotNull(createdQoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		boolean unique = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.isUnique(QuantityOfInterest.Filter.SYMBOL, "My name"); //$NON-NLS-1$
		assertTrue(unique);
	}

	@Test
	void test_isUnique_Empty_WithoutExceptId() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		String qoiName = "My Name of QoI"; //$NON-NLS-1$
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(createdModel);
		qoi.setSymbol(qoiName);
		qoi.setCreationDate(new Date());
		try {
			QuantityOfInterest createdQoi = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
					.create(qoi);
			assertNotNull(createdQoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		boolean unique = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.isUnique(QuantityOfInterest.Filter.SYMBOL, ""); //$NON-NLS-1$
		assertTrue(unique);
	}

	@Test
	void test_isUnique_False_WithoutExceptId() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		String qoiName = "My Name of QoI"; //$NON-NLS-1$
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(createdModel);
		qoi.setSymbol(qoiName);
		qoi.setCreationDate(new Date());
		try {
			QuantityOfInterest createdQoi = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
					.create(qoi);
			assertNotNull(createdQoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		boolean unique = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.isUnique(QuantityOfInterest.Filter.SYMBOL, qoiName);
		assertFalse(unique);
	}

	@Test
	void test_isUnique_True_WithExceptId() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);
		assertNotNull(createdModel.getId());

		// create Qoi
		String qoiName = "My Name of QoI"; //$NON-NLS-1$
		QuantityOfInterest qoi = new QuantityOfInterest();
		qoi.setModel(createdModel);
		qoi.setSymbol(qoiName);
		qoi.setCreationDate(new Date());
		try {
			QuantityOfInterest createdQoi = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
					.create(qoi);
			assertNotNull(createdQoi);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}

		// test case
		boolean unique = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.isUniqueExcept(QuantityOfInterest.Filter.SYMBOL, new Integer[] { qoi.getId() }, qoiName);
		assertTrue(unique);
	}

	@Test
	void test_isUnique_ValueNull() {

		// test case
		boolean unique = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.isUnique(QuantityOfInterest.Filter.SYMBOL, null);
		assertTrue(unique);
	}

	@Test
	void test_isUnique_FieldNull() {

		// test case
		boolean unique = getDaoManager().getRepository(IQuantityOfInterestRepository.class).isUnique(null, "Test"); //$NON-NLS-1$
		assertTrue(unique);
	}
}
