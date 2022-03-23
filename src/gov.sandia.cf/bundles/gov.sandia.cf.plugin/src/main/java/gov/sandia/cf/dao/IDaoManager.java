/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.hsqldb.cmdline.SqlToolError;

import gov.sandia.cf.common.IManager;
import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;

/**
 * Interface for layer manager
 * 
 * @author Didier Verstraete
 *
 */
public interface IDaoManager extends IManager {

	/**
	 * Initialize local database with databasePath.
	 *
	 * @param databasePath the database path
	 * @throws CredibilityException          if a parameter is not valid.
	 * @throws SqlToolError                  if a sql tool error occurs.
	 * @throws SQLException                  if a sql exception occurs.
	 * @throws IOException                   if a file read exception occurs.
	 * @throws URISyntaxException            if a file path is not valid.
	 * @throws CredibilityMigrationException if a migration exception occurs.
	 * @throws CredibilityDatabaseInvalidException the credibility database invalid exception
	 */
	void initialize(String databasePath) throws CredibilityException, SqlToolError, SQLException, IOException,
			URISyntaxException, CredibilityMigrationException, CredibilityDatabaseInvalidException;

	/**
	 * Set the persist unit name. Needs to restart the database instance
	 *
	 * @param persistUnitName the new persist unit name
	 */
	void setPersistUnitName(String persistUnitName);

	/**
	 * @return the entity manager
	 */
	EntityManager getEntityManager();

	/**
	 * @return the database migration manager
	 */
	IDBMigrationManager getDbMigrationManager();

	/**
	 * @return nativeQuery Repository
	 */
	INativeQueryRepository getNativeQueryRepository();

	/**
	 * @param <R>            the repository object
	 * @param interfaceClass the repository class
	 * @return the repository requested after instantiation and after being
	 *         populated.
	 */
	<R extends ICRUDRepository<?, ?>> R getRepository(Class<R> interfaceClass);

	/**
	 * @return the database directory path
	 */
	String getDatabaseDirectoryPath();

	/**
	 * @return the database manager
	 */
	IDBManager getDbManager();

}
