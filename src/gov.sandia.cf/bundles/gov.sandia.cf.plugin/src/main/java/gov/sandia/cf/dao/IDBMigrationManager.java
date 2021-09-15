/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.hsqldb.cmdline.SqlToolError;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;

/**
 * This interface manage the sql data and schema migration
 * 
 * @author Didier Verstraete
 *
 */
public interface IDBMigrationManager {

	/**
	 * Process the database migration
	 * 
	 * @throws CredibilityException          if a parameter is not valid.
	 * @throws SQLException                  if a sql exception occurs.
	 * @throws IOException                   if a file read exception occurs.
	 * @throws SqlToolError                  if a sql tool error occurs.
	 * @throws URISyntaxException            if a file path is not valid.
	 * @throws CredibilityMigrationException if a migration error occurs.
	 */
	public void executeMigration() throws CredibilityException, SqlToolError, SQLException, IOException,
			URISyntaxException, CredibilityMigrationException;
}
