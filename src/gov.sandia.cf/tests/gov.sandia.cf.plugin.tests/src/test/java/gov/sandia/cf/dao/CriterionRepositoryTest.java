/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.CriterionRepository;
import gov.sandia.cf.model.Criterion;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the CriterionRepository
 */
@RunWith(JUnitPlatform.class)
class CriterionRepositoryTest extends AbstractTestRepository<Criterion, Integer, CriterionRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(CriterionRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<CriterionRepository> getRepositoryClass() {
		return CriterionRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Criterion> getModelClass() {
		return Criterion.class;
	}

	@Override
	Criterion getModelFulfilled(Criterion model) {
		// populate PIRT
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		PhenomenonGroup newPhenomenonGroup = TestEntityFactory.getNewPhenomenonGroup(getDaoManager(), newQoI);
		Phenomenon newPhenomenon = TestEntityFactory.getNewPhenomenon(getDaoManager(), newPhenomenonGroup);

		fulfillModelStrings(model);
		model.setPhenomenon(newPhenomenon);
		return model;
	}

}
