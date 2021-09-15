/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.IQoIHeaderRepository;
import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.dao.IUserRepository;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIHeader;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Migration of entities where creation user is missing. This can occur when the
 * entity has been created with an old version of CF &lt;= 0.2.0.
 * 
 * A new user UNKNOWN_USER is created and associated to the PIRT entities who
 * need.
 * 
 * @author Didier Verstraete
 */
public class Task_008_UnknownCreationUserXorDate implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_008_UnknownCreationUserXorDate.class);

	private static final String TASK_NAME = "0.6.0-iwfcf-422-unknowncreationuser-task8"; //$NON-NLS-1$

	@Override
	public String getName() {
		return TASK_NAME;
	}

	@Override
	public boolean execute(DaoManager daoManager) throws CredibilityMigrationException {

		if (daoManager == null || daoManager.getEntityManager() == null) {
			throw new CredibilityMigrationException(RscTools.getString(RscConst.EX_MIGRATIONDAO_DAOMGR_NULL));
		}

		// get current model - if not found do nothing
		Model model = daoManager.getRepository(IModelRepository.class).getFirst();
		if (model == null) {
			return false;
		}

		boolean changed = false;

		// create unknown user
		User unknownUser = daoManager.getRepository(IUserRepository.class).findByUserId(User.UNKNOWN_USERID);
		if (unknownUser == null) {
			logger.info("Creating 'Unknown User' in database"); //$NON-NLS-1$

			unknownUser = new User();
			unknownUser.setUserID(User.UNKNOWN_USERID);
			try {
				unknownUser = daoManager.getRepository(IUserRepository.class).create(unknownUser);
			} catch (CredibilityException e) {
				logger.error(e.getMessage(), e);
				return false;
			}
		}

		if (unknownUser == null) {
			throw new CredibilityMigrationException(
					RscTools.getString(RscConst.EX_MIGRATIONDAO_TASK8_UNKNOWNUSER_NOTCREATED));
		}

		// set unknown user to QoI with possible user creation null
		try {
			changed |= setQoICreationUserMissing(daoManager, unknownUser);
		} catch (CredibilityException e) {
			throw new CredibilityMigrationException(e);
		}

		// set unknown user to QoIHeader with possible user creation null
		try {
			changed |= setQoIHeaderCreationUserMissing(daoManager, unknownUser);
		} catch (CredibilityException e) {
			throw new CredibilityMigrationException(e);
		}

		// set current date to QoI with possible date creation null
		try {
			changed |= setQoICreationDateMissing(daoManager);
		} catch (CredibilityException e) {
			throw new CredibilityMigrationException(e);
		}

		// set current date to QoIHeader with possible date creation null
		try {
			changed |= setQoIHeaderCreationDateMissing(daoManager);
		} catch (CredibilityException e) {
			throw new CredibilityMigrationException(e);
		}

		return changed;
	}

	/**
	 * Set unknown user to qoi with creation user null
	 * 
	 * @param daoManager  the dao manager
	 * @param unknownUser the unknown user to set
	 * @return true if the database changed, otherwise false
	 * @throws CredibilityException if an exception occurs during update
	 */
	private boolean setQoICreationUserMissing(DaoManager daoManager, User unknownUser) throws CredibilityException {

		// search for qoi without creation user
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.USER_CREATION, NullParameter.NULL);
		List<QuantityOfInterest> foundList = daoManager.getRepository(IQuantityOfInterestRepository.class)
				.findBy(filters);

		if (foundList != null && !foundList.isEmpty()) {
			for (QuantityOfInterest qoi : foundList) {
				qoi.setUserCreation(unknownUser);
				if (qoi.getCreationDate() == null) {
					qoi.setCreationDate(DateTools.getDefault1900Date());
				}
				daoManager.getRepository(IQuantityOfInterestRepository.class).update(qoi);
			}
			return true;
		}

		return false;
	}

	/**
	 * Set unknown user to qoi with creation user null
	 * 
	 * @param daoManager  the dao manager
	 * @param unknownUser the unknown user to set
	 * @return true if the database changed, otherwise false
	 * @throws CredibilityException if an exception occurs during update
	 */
	private boolean setQoIHeaderCreationUserMissing(DaoManager daoManager, User unknownUser)
			throws CredibilityException {

		// search for qoi without creation user
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(QoIHeader.Filter.USER_CREATION, NullParameter.NULL);
		List<QoIHeader> foundList = daoManager.getRepository(IQoIHeaderRepository.class).findBy(filters);

		if (foundList != null && !foundList.isEmpty()) {
			for (QoIHeader qoiHeader : foundList) {
				qoiHeader.setUserCreation(unknownUser);
				if (qoiHeader.getCreationDate() == null) {
					qoiHeader.setCreationDate(DateTools.getDefault1900Date());
				}
				daoManager.getRepository(IQoIHeaderRepository.class).update(qoiHeader);
			}
			return true;
		}

		return false;
	}

	/**
	 * Set unknown user to qoi with creation date null
	 * 
	 * @param daoManager the dao manager
	 * @return true if the database changed, otherwise false
	 * @throws CredibilityException if an exception occurs during update
	 */
	private boolean setQoICreationDateMissing(DaoManager daoManager) throws CredibilityException {

		// search for qoi without creation user
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.DATE_CREATION, NullParameter.NULL);
		List<QuantityOfInterest> foundList = daoManager.getRepository(IQuantityOfInterestRepository.class)
				.findBy(filters);

		if (foundList != null && !foundList.isEmpty()) {
			for (QuantityOfInterest qoi : foundList) {
				qoi.setCreationDate(DateTools.getDefault1900Date());
				daoManager.getRepository(IQuantityOfInterestRepository.class).update(qoi);
			}
			return true;
		}

		return false;
	}

	/**
	 * Set unknown user to qoi with creation date null
	 * 
	 * @param daoManager the dao manager
	 * @return true if the database changed, otherwise false
	 * @throws CredibilityException if an exception occurs during update
	 */
	private boolean setQoIHeaderCreationDateMissing(DaoManager daoManager) throws CredibilityException {

		// search for qoi without creation user
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(QoIHeader.Filter.DATE_CREATION, NullParameter.NULL);
		List<QoIHeader> foundList = daoManager.getRepository(IQoIHeaderRepository.class).findBy(filters);

		if (foundList != null && !foundList.isEmpty()) {
			for (QoIHeader qoiHeader : foundList) {
				qoiHeader.setCreationDate(DateTools.getDefault1900Date());
				daoManager.getRepository(IQoIHeaderRepository.class).update(qoiHeader);
			}
			return true;
		}

		return false;
	}
}
