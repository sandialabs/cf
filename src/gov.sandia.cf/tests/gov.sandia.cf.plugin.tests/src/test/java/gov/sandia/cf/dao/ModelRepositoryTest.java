/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.ModelRepository;
import gov.sandia.cf.model.Model;

/**
 * JUnit class to test the ModelRepositoryTest
 * 
 * @author Didier Verstraete
 *
 */
class ModelRepositoryTest extends AbstractTestRepository<Model, Integer, ModelRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ModelRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<ModelRepository> getRepositoryClass() {
		return ModelRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Model> getModelClass() {
		return Model.class;
	}

	@Override
	Model getModelFulfilled(Model model) {
		fulfillModelStrings(model);
		return model;
	}
}
