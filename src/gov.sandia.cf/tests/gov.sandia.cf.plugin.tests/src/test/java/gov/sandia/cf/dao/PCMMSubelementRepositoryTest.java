/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PCMMSubelementRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PCMMSubelementRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PCMMSubelementRepositoryTest extends AbstractTestRepository<PCMMSubelement, Integer, PCMMSubelementRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PCMMSubelementRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMSubelementRepository> getRepositoryClass() {
		return PCMMSubelementRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PCMMSubelement> getModelClass() {
		return PCMMSubelement.class;
	}

	@Override
	PCMMSubelement getModelFulfilled(PCMMSubelement model) {
		// populate PCMM
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		PCMMElement newPCMMElement = TestEntityFactory.getNewPCMMElement(getDaoManager(), newModel);

		fulfillModelStrings(model);
		model.setElement(newPCMMElement);
		return model;
	}
}
