/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration;

import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.exceptions.CredibilityMigrationException;

/**
 * The migration task to execute.
 * 
 * @author Didier Verstraete
 *
 */
public interface IMigrationTask {

	/**
	 * Do not change the name after task creation, otherwise the task will be
	 * re-executed.
	 * 
	 * @return the task name to log in database
	 */
	String getName();

	/**
	 * Execute the migration task
	 * 
	 * @param daoManager th dao manager
	 * @return true if the database changed, otherwise false.
	 * @throws CredibilityMigrationException if a migration exception occurs
	 */
	boolean execute(IDaoManager daoManager) throws CredibilityMigrationException;
}
