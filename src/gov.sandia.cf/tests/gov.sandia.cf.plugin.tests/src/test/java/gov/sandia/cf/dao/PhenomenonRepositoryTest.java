/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.PhenomenonRepository;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the PhenomenonRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class PhenomenonRepositoryTest extends AbstractTestRepository<Phenomenon, Integer, PhenomenonRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(PhenomenonRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<PhenomenonRepository> getRepositoryClass() {
		return PhenomenonRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Phenomenon> getModelClass() {
		return Phenomenon.class;
	}

	@Override
	Phenomenon getModelFulfilled(Phenomenon model) {
		// populate PIRT
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		PhenomenonGroup newPhenomenonGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);

		fulfillModelStrings(model);
		model.setPhenomenonGroup(newPhenomenonGroup);
		return model;
	}
}
