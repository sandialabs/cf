/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractTestDao;
import gov.sandia.cf.dao.IQoIHeaderRepository;
import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.dao.IUserRepository;
import gov.sandia.cf.dao.migration.tasks.Task_008_UnknownCreationUserXorDate;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tests.TestEntityFactory;

/**
 * The Class Task_008_UnknownCreationUserXorDateTest.
 *
 * @author Didier Verstraete
 */
class Task_008_UnknownCreationUserXorDateTest extends AbstractTestDao {

	/**
	 * the logger
	 */
	public static final Logger logger = LoggerFactory.getLogger(Task_008_UnknownCreationUserXorDateTest.class);

	@Test
	void test_MigrationTask_NotMigrated() {

		// Needed to execute a migration task
		Model model = TestEntityFactory.getNewModel(getDaoManager());

		QuantityOfInterest newQoI1 = TestEntityFactory.getNewQoI(getDaoManager(), model);
		QuantityOfInterest newQoI2 = TestEntityFactory.getNewQoI(getDaoManager(), model);

		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI1, null);
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI1, null);

		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI2, null);
		TestEntityFactory.getNewQoIHeader(getDaoManager(), newQoI2, null);

		// alter table - inject column to be deleted and migrated
		String sqlUpdate = "UPDATE {0} SET {1}=NULL, {2}=NULL;"; //$NON-NLS-1$

		UnitOfWork unitOfWork = getDaoManager().getEntityManager().unwrap(UnitOfWork.class);

		unitOfWork.executeNonSelectingSQL(MessageFormat.format(sqlUpdate, "QOI", "USER_CREATION_ID", "CREATION_DATE")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(sqlUpdate, "QOIHEADER", "USER_CREATION_ID", "CREATION_DATE")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		// search for qoi without creation user
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.USER_CREATION, NullParameter.NULL);
		List<QuantityOfInterest> foundQoIList = getDaoManager().getRepository(IQuantityOfInterestRepository.class)
				.findBy(filters);
		assertEquals(2, foundQoIList.size());
		// search for qoi without creation user
		filters = new HashMap<>();
		filters.put(QoIHeader.Filter.USER_CREATION, NullParameter.NULL);
		List<QoIHeader> foundQoIHeaderList = getDaoManager().getRepository(IQoIHeaderRepository.class).findBy(filters);
		assertEquals(4, foundQoIHeaderList.size());

		// search for qoi without date creation
		filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.DATE_CREATION, NullParameter.NULL);
		foundQoIList = getDaoManager().getRepository(IQuantityOfInterestRepository.class).findBy(filters);
		assertEquals(2, foundQoIList.size());
		// search for qoi without date creation
		filters = new HashMap<>();
		filters.put(QoIHeader.Filter.DATE_CREATION, NullParameter.NULL);
		foundQoIHeaderList = getDaoManager().getRepository(IQoIHeaderRepository.class).findBy(filters);
		assertEquals(4, foundQoIHeaderList.size());

		// migration
		try {
			boolean changed = new Task_008_UnknownCreationUserXorDate().execute(getDaoManager());
			assertTrue(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}

		// test
		User unknownUser = getDaoManager().getRepository(IUserRepository.class).findByUserId(User.UNKNOWN_USERID);
		assertNotNull(unknownUser);
		assertNotNull(unknownUser.getId());

		// search for qoi without creation user
		filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.USER_CREATION, NullParameter.NULL);
		foundQoIList = getDaoManager().getRepository(IQuantityOfInterestRepository.class).findBy(filters);
		assertEquals(0, foundQoIList.size());
		// search for qoi header without creation user
		filters = new HashMap<>();
		filters.put(QoIHeader.Filter.USER_CREATION, NullParameter.NULL);
		foundQoIHeaderList = getDaoManager().getRepository(IQoIHeaderRepository.class).findBy(filters);
		assertEquals(0, foundQoIHeaderList.size());

		// search for qoi with unknown user
		filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.USER_CREATION, unknownUser);
		foundQoIList = getDaoManager().getRepository(IQuantityOfInterestRepository.class).findBy(filters);
		assertEquals(2, foundQoIList.size());
		// search for qoi header with unknown user
		filters = new HashMap<>();
		filters.put(QoIHeader.Filter.USER_CREATION, unknownUser);
		foundQoIHeaderList = getDaoManager().getRepository(IQoIHeaderRepository.class).findBy(filters);
		assertEquals(4, foundQoIHeaderList.size());

		// search for qoi without date creation
		filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.DATE_CREATION, NullParameter.NULL);
		foundQoIList = getDaoManager().getRepository(IQuantityOfInterestRepository.class).findBy(filters);
		assertEquals(0, foundQoIList.size());
		// search for qoi without date creation
		filters = new HashMap<>();
		filters.put(QoIHeader.Filter.DATE_CREATION, NullParameter.NULL);
		foundQoIHeaderList = getDaoManager().getRepository(IQoIHeaderRepository.class).findBy(filters);
		assertEquals(0, foundQoIHeaderList.size());
	}

	@Test
	void test_MigrationTask_AlreadyMigrated() {

		// initialize data
		TestEntityFactory.getNewModel(getDaoManager());

		// test
		try {
			boolean changed = new Task_008_UnknownCreationUserXorDate().execute(getDaoManager());
			assertFalse(changed);
		} catch (CredibilityMigrationException e) {
			fail(e.getMessage());
		}
	}
}
