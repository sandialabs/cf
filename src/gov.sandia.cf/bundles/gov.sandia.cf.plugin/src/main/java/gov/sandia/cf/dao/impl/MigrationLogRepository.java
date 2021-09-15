/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IMigrationLogRepository;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.MigrationLog;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * MigrationLog entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class MigrationLogRepository extends AbstractCRUDRepository<MigrationLog, Integer>
		implements IMigrationLogRepository {

	/**
	 * Queries
	 */
	public static final String SELECT_LAST_EXECUTION_LOG = "SELECT m FROM MigrationLog m WHERE m.scriptName=:{0}"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public MigrationLogRepository() {
		super(MigrationLog.class);
	}

	/**
	 * MigrationLogRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public MigrationLogRepository(EntityManager entityManager) {
		super(entityManager, MigrationLog.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MigrationLog getScriptLog(String scriptName) {

		if (scriptName == null) {
			throw new IllegalArgumentException(RscTools.getString(RscConst.EX_MIGRATIONLOG_SCRIPTNAME_BLANK));
		}

		String paramScriptName = "scriptName"; //$NON-NLS-1$

		TypedQuery<MigrationLog> query = getEntityManager()
				.createQuery(MessageFormat.format(SELECT_LAST_EXECUTION_LOG, paramScriptName), MigrationLog.class);
		query.setParameter(paramScriptName, scriptName);
		List<MigrationLog> resultList = query.getResultList();

		if (resultList != null && !resultList.isEmpty()) {
			return resultList.get(0);
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean isLastExecutionInError(String scriptName) {

		MigrationLog migrationLog = getScriptLog(scriptName);
		Boolean isError = null;

		if (migrationLog != null) {
			isError = migrationLog.getIsError();
		}

		return isError;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markTaskAsExecuted(IMigrationTask task, String databaseVersion) throws CredibilityException {
		if (task != null) {
			insertScriptLog(task.getName(), databaseVersion, null, false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void markTaskInError(IMigrationTask task, String databaseVersion, String errorlog)
			throws CredibilityException {
		if (task != null) {
			insertScriptLog(task.getName(), databaseVersion, errorlog, true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertScriptLog(String taskName, String databaseVersion, String errorlog, Boolean inError)
			throws CredibilityException {

		MigrationLog scriptLog = getScriptLog(taskName);

		// if there is no previous execution, insert the log
		if (scriptLog == null) {
			MigrationLog log = new MigrationLog();
			log.setDatabaseVersion(databaseVersion);
			log.setDateExecution(DateTools.getCurrentDate());
			log.setIsError(inError);
			log.setScriptName(taskName);
			log.setExecutionLog(errorlog);
			create(log);
		} else { // update the migration log table
			scriptLog.setDatabaseVersion(databaseVersion);
			scriptLog.setDateExecution(DateTools.getCurrentDate());
			scriptLog.setIsError(inError);
			scriptLog.setScriptName(taskName);
			scriptLog.setExecutionLog(errorlog);
			update(scriptLog);
		}
	}

}