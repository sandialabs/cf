/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.common.ServiceLoader;
import gov.sandia.cf.dao.CredibilityDaoRuntimeException.CredibilityDaoRuntimeMessage;
import gov.sandia.cf.dao.hsqldb.HSQLDBDaoManager;
import gov.sandia.cf.dao.impl.NativeQueryRepository;
import gov.sandia.cf.dao.migration.EclipseLinkMigrationManager;
import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException.CredibilityServiceRuntimeMessage;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage the database access implementing hsqldb This class is a singleton
 * 
 * @author Didier Verstraete
 *
 */
public class DaoManager implements IDaoManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(DaoManager.class);

	/**
	 * The database manager
	 */
	private IDBManager dbManager;

	/**
	 * SQL Migration manager
	 */
	private IDBMigrationManager dbMigrationManager;

	/**
	 * native query repository
	 */
	private INativeQueryRepository nativeQueryRepository;

	/**
	 * Map of interface and entities repository
	 */
	private Map<Class<?>, Class<?>> mapInterface;

	/**
	 * Map of Model entities repository
	 */
	private Map<Class<?>, Object> mapRepositories;

	/**
	 * Defines the state of the loader
	 */
	private boolean isStarted = false;

	/** The is initialized. */
	private boolean isInitialized = false;

	/**
	 * The persistence unit name
	 */
	private String persistUnitName;

	/**
	 * The constructor
	 * 
	 */
	public DaoManager() {
		persistUnitName = HSQLDBDaoManager.ENTITY_PERSIST_UNIT_NAME_PRODUCTION;
	}

	/**
	 * The constructor
	 * 
	 * @param persistUnitName the persistence unit name in the xml parameter file
	 */
	public DaoManager(String persistUnitName) {
		this.persistUnitName = persistUnitName;
	}

	/** {@inheritDoc} */
	@Override
	public void start() {
		logger.debug("dao loader started"); //$NON-NLS-1$

		dbManager = new HSQLDBDaoManager(persistUnitName);
		logger.debug("dao manager loaded: HSQLDBDaoManager is the implementation"); //$NON-NLS-1$

		mapInterface = new HashMap<>();

		// load repositories
		mapInterface.putAll(ServiceLoader.load(Repository.class, this.getClass().getPackage().getName(),
				this.getClass().getPackage().getName()));

		// initialize repository map
		// repositories will be instantiated along the water with get method
		mapRepositories = new HashMap<>();

		// native query
		nativeQueryRepository = new NativeQueryRepository();

		// initialize migration manager
		dbMigrationManager = new EclipseLinkMigrationManager(this);

		isStarted = true;
	}

	/** {@inheritDoc} */
	@Override
	public void initialize(String databasePath) throws CredibilityException, SqlToolError, SQLException, IOException,
			URISyntaxException, CredibilityMigrationException, CredibilityDatabaseInvalidException {

		if (!isInitialized) {
			logger.debug("initializing dao loader with local database at:{}", databasePath); //$NON-NLS-1$

			// initialize database manager
			dbManager.initialize(databasePath);

			// execute sql database migration
			dbMigrationManager.executeMigration();

			isInitialized = true;
		} else {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DAOMANAGER_ALREADY_INIT));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void setPersistUnitName(String persistUnitName) {
		this.persistUnitName = persistUnitName;
	}

	/** {@inheritDoc} */
	@Override
	public void stop() {
		try {
			if (dbManager != null) {
				dbManager.close();
			}
		} catch (CredibilityException e) {
			logger.error(e.getMessage(), e);
		}

		isInitialized = false;
		isStarted = false;
		logger.debug("dao loader stopped"); //$NON-NLS-1$
	}

	/** {@inheritDoc} */
	@Override
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * Checks if is initialized.
	 *
	 * @return true, if is initialized
	 */
	public boolean isInitialized() {
		return isInitialized;
	}

	/** {@inheritDoc} */
	@Override
	public EntityManager getEntityManager() {
		return dbManager.getEntityManager();
	}

	/** {@inheritDoc} */
	@Override
	public IDBMigrationManager getDbMigrationManager() {
		return dbMigrationManager;
	}

	/** {@inheritDoc} */
	@Override
	public INativeQueryRepository getNativeQueryRepository() {
		populate(nativeQueryRepository);
		return nativeQueryRepository;
	}

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public <R extends ICRUDRepository<?, ?>> R getRepository(Class<R> interfaceClass) {

		R repository = null;
		Class<R> repositoryClass = null;

		// check if is interface
		if (!interfaceClass.isInterface()) {
			throw new CredibilityDaoRuntimeException(CredibilityDaoRuntimeMessage.NOT_INTERFACE);
		}

		// check if it is using Repository annotation
		if (interfaceClass.getAnnotation(Repository.class) == null) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_APPSERVICE_INTERFACE,
					interfaceClass.getName(), Repository.class.getName());
		}

		// check if it is a ICRUDRepository service
		if (!ICRUDRepository.class.isAssignableFrom(interfaceClass)) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_APPSERVICE_INTERFACE,
					interfaceClass.getName(), ICRUDRepository.class.getName());
		}

		// check initialized
		if (mapInterface == null) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_INITIALIZED);
		}

		// get repository class
		repositoryClass = (Class<R>) mapInterface.get(interfaceClass);
		if (repositoryClass == null) {
			throw new CredibilityDaoRuntimeException(CredibilityDaoRuntimeMessage.NOT_FOUND, interfaceClass.getName());
		}

		if (mapRepositories.containsKey(repositoryClass)) {
			Object icrudRepository = mapRepositories.get(repositoryClass);
			if (repositoryClass.isInstance(icrudRepository)) {
				repository = repositoryClass.cast(icrudRepository);
			}
		} else {
			try {
				repository = repositoryClass.newInstance();
				mapRepositories.put(repositoryClass, repository);
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("An error occurs while instantiating new DAO Repository: {}", e.getMessage(), e); //$NON-NLS-1$
			}
		}

		// populate
		populate(repository);

		return repository;
	}

	/**
	 * @param repository the dao repository to populate
	 * @return the repository populated with the entity manager
	 */
	private IRepository populate(IRepository repository) {

		// set the entity manager
		if (repository != null && repository.getEntityManager() == null) {
			repository.setEntityManager(getEntityManager());
		}
		return repository;
	}

	/** {@inheritDoc} */
	@Override
	public String getDatabaseDirectoryPath() {
		return dbManager.getDatabaseDirectoryPath();
	}

	/** {@inheritDoc} */
	@Override
	public IDBManager getDbManager() {
		return dbManager;
	}

}
