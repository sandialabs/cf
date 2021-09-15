/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.dao.migration.tasks.Task_001_QoIITag;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * @author Didier Verstraete
 *
 */
@RunWith(JUnitPlatform.class)
class Task_001_QoIITagTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_001_QoIITagTest.class);

	@Test
	void test_MigrationTask_NotMigrated() {

		// initialize data
		Model newModel = TestEntityFactory.getNewModel(getDaoManager());
		User newUser = TestEntityFactory.getNewUser(getDaoManager());

		QuantityOfInterest newQoI = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		QuantityOfInterest newQoITag1 = TestEntityFactory.getNewQoI(getDaoManager(), newModel);
		QuantityOfInterest newQoITag2 = TestEntityFactory.getNewQoI(getDaoManager(), newModel);

		newQoITag1.setTag("TAG1"); //$NON-NLS-1$
		newQoITag1.setTagUserCreation(newUser);
		newQoITag1.setCreationDate(newQoI.getCreationDate());
		newQoITag1.setTagDate(newQoI.getCreationDate());
		try {
			newQoITag1 = getDaoManager().getRepository(IQuantityOfInterestRepository.class).update(newQoITag1);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNull(newQoITag1.getParent());
		assertNotNull(newQoITag1.getTagDate());

		newQoITag2.setTag("TAG2"); //$NON-NLS-1$
		newQoITag2.setTagUserCreation(newUser);
		newQoITag2.setCreationDate(newQoI.getCreationDate());
		newQoITag2.setTagDate(newQoI.getCreationDate());
		try {
			newQoITag2 = getDaoManager().getRepository(IQuantityOfInterestRepository.class).update(newQoITag2);
		} catch (CredibilityException e) {
			fail(e.getMessage());
		}
		assertNull(newQoITag2.getParent());
		assertNotNull(newQoITag2.getTagDate());

		// test
		try {
			boolean changed = new Task_001_QoIITag().execute(getDaoManager());
			assertTrue(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.DATE_TAG, NullParameter.NOT_NULL);
		filters.put(QuantityOfInterest.Filter.PARENT, NullParameter.NULL);
		List<QuantityOfInterest> findBy = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.findBy(filters);
		assertFalse(findBy != null && !findBy.isEmpty());

		getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(newQoITag1);
		assertEquals(newQoI, newQoITag1.getParent());

		getDaoManager().getRepository(IQuantityOfInterestRepository.class).refresh(newQoITag2);
		assertEquals(newQoI, newQoITag2.getParent());
	}

	@Test
	void test_MigrationTask_AlreadyMigrated() {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			boolean changed = new Task_001_QoIITag().execute(getDaoManager());
			assertFalse(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}
}
