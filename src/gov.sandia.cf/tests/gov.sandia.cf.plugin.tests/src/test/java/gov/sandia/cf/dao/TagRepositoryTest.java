/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.util.Date;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.impl.TagRepository;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 *         JUnit class to test the TagRepositoryTest
 */
@RunWith(JUnitPlatform.class)
class TagRepositoryTest extends AbstractTestRepository<Tag, Integer, TagRepository> {

	/**
	 * the logger
	 */
	public static Logger logger = LoggerFactory.getLogger(TagRepositoryTest.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<TagRepository> getRepositoryClass() {
		return TagRepository.class;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	Class<Tag> getModelClass() {
		return Tag.class;
	}

	@Override
	Tag getModelFulfilled(Tag model) {
		// get user data
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		fulfillModelStrings(model);
		model.setDateTag(new Date());
		model.setUserCreation(newUser);
		return model;
	}
}
