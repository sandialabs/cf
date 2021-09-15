/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.RoleRepository;
import gov.sandia.cf.model.Role;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the RoleRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class RoleRepositoryTest extends AbstractTestRepository<Role, Integer, RoleRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(RoleRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<RoleRepository> getRepositoryClass() {
		return RoleRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Role> getModelClass() {
		return Role.class;
	}

	@Override
	Role getModelFulfilled(Role model) {
		fulfillModelStrings(model);
		return model;
	}
}
