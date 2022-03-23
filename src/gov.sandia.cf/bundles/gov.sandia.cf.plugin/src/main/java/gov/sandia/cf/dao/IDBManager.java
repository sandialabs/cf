/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.io.IOException;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * This interface manage the data access
 * 
 * @author Didier Verstraete
 *
 */
public interface IDBManager {

	/**
	 * Initialize the dao manager and set it to the new path.
	 *
	 * @param path the database path
	 * @throws CredibilityException if a parameter is not valid
	 * @throws SQLException         if an error occured while intializing database
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws CredibilityDatabaseInvalidException the credibility database invalid exception
	 */
	public void initialize(String path) throws CredibilityException, SQLException, IOException, CredibilityDatabaseInvalidException;

	/**
	 * Closes database connection
	 * 
	 * @throws CredibilityException if an error occured while closing connection
	 */
	public void close() throws CredibilityException;

	/**
	 * @return the entity manager
	 */
	public EntityManager getEntityManager();

	/**
	 * 
	 * @return the database directory path
	 */
	public String getDatabaseDirectoryPath();
}
