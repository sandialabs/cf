/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.MigrationLog;

/**
 * the User repository interface
 * 
 * @author Didier Verstraete
 *
 */
public interface IMigrationLogRepository extends ICRUDRepository<MigrationLog, Integer> {

	/**
	 * @param scriptName the script name
	 * @return the script execution log by script name
	 */
	MigrationLog getScriptLog(String scriptName);

	/**
	 * @param scriptName the script name
	 * @return null if there is no result, true if the script execution was in
	 *         error, false if the script execution was successful
	 */
	Boolean isLastExecutionInError(String scriptName);

	/**
	 * Insert into the Migration log table a success line for the current script
	 * execution.
	 * 
	 * @param task            the task launched
	 * @param databaseVersion the database version number
	 * @throws CredibilityException throw credibility exception if the script log
	 *                              does not fit expected entity criteria.
	 */
	void markTaskAsExecuted(IMigrationTask task, String databaseVersion) throws CredibilityException;

	/**
	 * Insert into the Migration log table an error line for the current script
	 * execution.
	 * 
	 * @param task            the task launched
	 * @param databaseVersion the database version number
	 * @param errorlog        the error log
	 * @throws CredibilityException throw credibility exception if the script log
	 *                              does not fit expected entity criteria.
	 */
	void markTaskInError(IMigrationTask task, String databaseVersion, String errorlog) throws CredibilityException;

	/**
	 * 
	 * Insert or Update the Migration Log corresponding to the script in parameter
	 * 
	 * @param script          the script to insert or update
	 * @param databaseVersion the database version
	 * @param errorlog        the error log
	 * @param inError         is the script in error
	 * @throws CredibilityException throw credibility exception if the script log
	 *                              does not fit expected entity criteria.
	 */
	void insertScriptLog(String script, String databaseVersion, String errorlog, Boolean inError)
			throws CredibilityException;

}
