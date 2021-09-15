/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMElementRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMElementRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMElementRepositoryTest extends AbstractTestRepository<PCMMElement, Integer, PCMMElementRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMElementRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMElementRepository> getRepositoryClass() {
		return PCMMElementRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMElement> getModelClass() {
		return PCMMElement.class;
	}

	@Override
	PCMMElement getModelFulfilled(PCMMElement model) {
		// populate PCMM
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());

		fulfillModelStrings(model);
		model.setModel(newModel);
		return model;
	}

	@Test
	void testFindByModel() {

		// create model
		Model createdModel = TestEntityFactory.getNewModel(getDaoManager());
		assertNotNull(createdModel);

		// create element
		PCMMElement createdElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), createdModel);
		assertNotNull(createdElement);

		// test case
		List<PCMMElement> foundList = getRepository().findByModel(createdModel);
		assertNotNull(foundList);
		assertEquals(1, foundList.size());

		PCMMElement found = foundList.get(0);
		assertNotNull(found);
		assertNotNull(found.getId());
		assertEquals(createdModel, found.getModel());
	}

	@Test
	void testFindByModel_ModelNull() {

		// test case
		List<PCMMElement> foundList = getRepository().findByModel(null);
		assertNotNull(foundList);
		assertEquals(0, foundList.size());
	}

}
