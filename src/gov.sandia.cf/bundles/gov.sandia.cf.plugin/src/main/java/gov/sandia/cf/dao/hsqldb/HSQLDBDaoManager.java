/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.hsqldb;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IDBManager;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage the database access implementing hsqldb This class is a singleton
 * 
 * @author Didier Verstraete
 *
 */
public class HSQLDBDaoManager implements IDBManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(HSQLDBDaoManager.class);

	/**
	 * jdbc hsqldb url prefix
	 */
	public static final String JDBC_HSQLDB_URL_PREFIX = "jdbc:hsqldb:file:";//$NON-NLS-1$

	/**
	 * Connection properties
	 */
	/** The connection USER property */
	public static final String CONNECTION_PROP_USER = "user"; //$NON-NLS-1$
	/** The connection PASSWORD property */
	public static final String CONNECTION_PROP_PASSWORD = "password"; //$NON-NLS-1$

	/**
	 * The stored data hsqldb folder name.
	 */
	public static final String DB_HSQLDB_DIRECTORY_NAME = FileTools.CREDIBILITY_DATABASE_FOLDER_DEFAULT_NAME;
	/**
	 * The stored data hsqldb file name. This file must be stored in the
	 * DB_HSQLDB_DIRECTORY_NAME folder associated to the modSim project
	 */
	public static final String DB_HSQLDB_DEFAULT_NAME = "credibility";//$NON-NLS-1$
	/**
	 * The production persist entity in the persistence.xml file
	 */
	public static final String ENTITY_PERSIST_UNIT_NAME_PRODUCTION = "credibility"; //$NON-NLS-1$
	/**
	 * jdbc url
	 */
	public static final String JDBC_URL = "javax.persistence.jdbc.url";//$NON-NLS-1$
	/**
	 * jdbc hsqldb default username
	 */
	public static final String JDBC_HSQLDB_USERNAME = "SA";//$NON-NLS-1$
	/**
	 * jdbc hsqldb default password
	 */
	public static final String JDBC_HSQLDB_PASSWORD = "";//$NON-NLS-1$
	/**
	 * jdbc hsqldb lock file property
	 */
	public static final String JDBC_HSQLDB_LOCK_FILE = "hsqldb.lock_file";//$NON-NLS-1$
	/**
	 * jdbc false
	 */
	public static final String JDBC_FALSE = "false";//$NON-NLS-1$

	/**
	 * query shutdown
	 */
	public static final String QUERY_SHUTDOWN = "SHUTDOWN";//$NON-NLS-1$

	/**
	 * The database filepath
	 */
	private String databaseDirectoryPath;

	/**
	 * the entity manager factory
	 */
	private EntityManagerFactory factory;

	/**
	 * The database entity manager
	 */
	private EntityManager entityManager;

	/**
	 * The entity persist unit
	 */
	private String entityPersistUnit;

	/**
	 * the HSQLDBDaoManager private constructor (singleton)
	 */
	public HSQLDBDaoManager() {
		entityPersistUnit = ENTITY_PERSIST_UNIT_NAME_PRODUCTION;
	}

	/**
	 * the HSQLDBDaoManager private constructor (singleton)
	 * 
	 * @param persistUnitName the persistence unit name
	 */
	public HSQLDBDaoManager(String persistUnitName) {
		entityPersistUnit = persistUnitName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityManager getEntityManager() {
		return entityManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDatabaseDirectoryPath() {
		return databaseDirectoryPath;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initialize(String path) throws CredibilityException, SQLException {
		this.databaseDirectoryPath = path + FileTools.PATH_SEPARATOR + DB_HSQLDB_DIRECTORY_NAME;
		initialize();
	}

	/**
	 * Initialize the entitymanager to connect the param connectionUrl database
	 * 
	 * @param connectionUrl
	 * 
	 * @throws CredibilityException
	 * @throws SQLException
	 */
	private void initialize() throws CredibilityException, SQLException {
		if (databaseDirectoryPath == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DAO_HSQLDB_FILEPATH_NULL));
		}

		// if not exists create database
		if (!existsDatabase()) {
			createDatabaseFile();
		}

		logger.debug("database initialization"); //$NON-NLS-1$

		// creating database connection
		String connectionUrl = JDBC_HSQLDB_URL_PREFIX + databaseDirectoryPath + FileTools.PATH_SEPARATOR
				+ DB_HSQLDB_DEFAULT_NAME;

		// setting database path property
		Map<String, String> properties = new HashMap<>();
		properties.put(JDBC_URL, connectionUrl);
		properties.put(JDBC_HSQLDB_LOCK_FILE, JDBC_FALSE);
		factory = Persistence.createEntityManagerFactory(entityPersistUnit, properties);
		entityManager = factory.createEntityManager();

		logger.debug("New database connection set on: {}", connectionUrl); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() throws CredibilityException {
		// ending database connection
		if (entityManager != null && entityManager.isOpen()) {
			logger.debug("Shutdown HSQLDB"); //$NON-NLS-1$
			if (entityManager.getTransaction().isActive()) {
				entityManager.getTransaction().rollback();
			}
			entityManager.getTransaction().begin();
			entityManager.createNativeQuery(QUERY_SHUTDOWN).executeUpdate();
			entityManager.getTransaction().commit();
			entityManager.close();
		}
		if (factory != null && factory.isOpen()) {
			factory.close();
		}
	}

	/**
	 * Create a new database stored in filepath location (full path must be
	 * specified). The database name is specified by default.
	 * 
	 * @throws SQLException if an error occured
	 */
	private void createDatabaseFile() throws SQLException {
		String url = JDBC_HSQLDB_URL_PREFIX + databaseDirectoryPath + FileTools.PATH_SEPARATOR + DB_HSQLDB_DEFAULT_NAME;

		Properties connectionProps = new Properties();
		connectionProps.put(CONNECTION_PROP_USER, JDBC_HSQLDB_USERNAME);
		connectionProps.put(CONNECTION_PROP_PASSWORD, JDBC_HSQLDB_PASSWORD);

		try (Connection conn = DriverManager.getConnection(url, connectionProps)) {
			if (conn != null) {
				logger.info("A new database has been created at:{}", url); //$NON-NLS-1$
			}
		}
	}

	/**
	 * @return true if the database file exists at the param databaseDirectoryPath
	 *         location, otherwise false
	 */
	private boolean existsDatabase() {
		File file = new File(databaseDirectoryPath);
		return file.exists();
	}

	/**
	 * If exists, deletes database files contained in @param dbContainerPath
	 * 
	 * @param dbContainerPath the database container path
	 * @throws IOException if the database file can not be found
	 */
	public static void dropDatabaseFiles(String dbContainerPath) throws IOException {
		if (dbContainerPath != null) {
			File dbPath = new File(dbContainerPath + FileTools.PATH_SEPARATOR + DB_HSQLDB_DIRECTORY_NAME);
			if (dbPath.exists()) {
				FileTools.deleteDirectoryRecursively(dbPath);
			}
		}
	}

}
