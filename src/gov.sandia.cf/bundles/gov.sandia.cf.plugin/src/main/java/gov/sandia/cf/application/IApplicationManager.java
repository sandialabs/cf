/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import javax.validation.Validator;

import org.hsqldb.cmdline.SqlToolError;

import gov.sandia.cf.common.IManager;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;

/**
 * The main interface to define the application manager.
 * 
 * @author Didier Verstraete
 *
 */
public interface IApplicationManager extends IManager {

	/**
	 * @param <S>            the service interface inherited IApplication class
	 * @param interfaceClass the service interface class
	 * @return the associated service if found, otherwise null
	 */
	public <S> S getService(Class<S> interfaceClass);

	/**
	 * @return the dao manager
	 */
	IDaoManager getDaoManager();

	/**
	 * @return the bean validator
	 */
	Validator getValidator();

	/**
	 * Initialize the local database file.
	 *
	 * @param projectPath the project path
	 * @throws SqlToolError                        the sql tool error
	 * @throws CredibilityException                the credibility exception
	 * @throws SQLException                        the SQL exception
	 * @throws IOException                         Signals that an I/O exception has
	 *                                             occurred.
	 * @throws URISyntaxException                  the URI syntax exception
	 * @throws CredibilityMigrationException       the credibility migration
	 *                                             exception
	 * @throws CredibilityDatabaseInvalidException the credibility database invalid
	 *                                             exception
	 */
	void initializeLocalDB(String projectPath) throws SqlToolError, CredibilityException, SQLException, IOException,
			URISyntaxException, CredibilityMigrationException, CredibilityDatabaseInvalidException;
}
