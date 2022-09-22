/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.ConfigurationFileRepository;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.ConfigurationFile;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * JUnit class to test the ConfigurationFileRepository
 * 
 * @author Didier Verstraete
 *
 */
class ConfigurationFileRepositoryTest
		extends AbstractTestRepository<ConfigurationFile, Integer, ConfigurationFileRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(ConfigurationFileRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<ConfigurationFileRepository> getRepositoryClass() {
		return ConfigurationFileRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<ConfigurationFile> getModelClass() {
		return ConfigurationFile.class;
	}

	@Override
	ConfigurationFile getModelFulfilled(ConfigurationFile model) {
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		fulfillModelStrings(model);
		model.setModel(newModel);
		model.setDateImport(new Date());
		model.setFeature(CFFeature.DECISION);
		return model;
	}
}
