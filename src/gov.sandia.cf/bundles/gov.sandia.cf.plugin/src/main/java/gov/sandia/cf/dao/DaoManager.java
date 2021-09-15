/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.hsqldb.cmdline.SqlToolError;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.CredibilityServiceRuntimeException;
import gov.sandia.cf.dao.CredibilityDaoRuntimeException.CredibilityDaoRuntimeMessage;
import gov.sandia.cf.dao.hsqldb.HSQLDBDaoManager;
import gov.sandia.cf.dao.impl.NativeQueryRepository;
import gov.sandia.cf.dao.migration.EclipseLinkMigrationManager;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.launcher.IManager;

/**
 * Manage the database access implementing hsqldb This class is a singleton
 * 
 * @author Didier Verstraete
 *
 */
public class DaoManager implements IManager {

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
	private Map<Class<? extends ICRUDRepository<?, ?>>, Class<? extends AbstractCRUDRepository<?, ?>>> mapInterface;

	/**
	 * Map of Model entities repository
	 */
	private Map<Class<?>, ICRUDRepository<?, ?>> mapRepositories;

	/**
	 * Defines the state of the loader
	 */
	private boolean isStarted = false;

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

		instantiateInterfaceMap();

		// initialize repository map
		// repositories will be instantiated along the water with get method
		mapRepositories = new HashMap<>();

		// native query
		nativeQueryRepository = new NativeQueryRepository();

		// initialize migration manager
		dbMigrationManager = new EclipseLinkMigrationManager(this);

		isStarted = true;
	}

	@SuppressWarnings("unchecked")
	private void instantiateInterfaceMap() {

		// load dao repositories file
		Map<String, Object> daoConfiguration = null;
		try (InputStream stream = DaoManager.class.getClassLoader()
				.getResourceAsStream(DaoManagerConstants.REPOSITORIES_FILENAME)) {
			daoConfiguration = new Yaml().load(stream);
		} catch (IOException e) {
			throw new CredibilityServiceRuntimeException(e);
		}

		if (daoConfiguration == null) {
			logger.warn("The DAO configuration can not be loaded."); //$NON-NLS-1$
			return;
		}

		List<String> repositories = (List<String>) daoConfiguration.get(DaoManagerConstants.REPOSITORIES_KEY);

		mapInterface = new HashMap<>();

		Reflections reflections = new Reflections(DaoManager.class.getPackage().getName());

		// start implementation
		for (String crudInterfaceToImplement : repositories) {

			Class<?> interfaceClass;

			// get the dao repository interface
			try {
				interfaceClass = Class.forName(crudInterfaceToImplement);
			} catch (ClassNotFoundException e) {
				throw new CredibilityDaoRuntimeException(e);
			}

			// check dao repository interface extends ICRUDRepository
			if (!ICRUDRepository.class.isAssignableFrom(interfaceClass)) {
				throw new CredibilityDaoRuntimeException(CredibilityDaoRuntimeMessage.NOT_DAOCRUD_INTERFACE,
						crudInterfaceToImplement);
			}

			// search for the first implementation
			Set<?> crudImplementationClassSet = reflections.getSubTypesOf(interfaceClass);

			// check if dao repository implementation is found
			if (crudImplementationClassSet == null || crudImplementationClassSet.isEmpty()) {
				throw new CredibilityDaoRuntimeException(CredibilityDaoRuntimeMessage.NOT_FOUND,
						interfaceClass.getName());
			}

			// check if dao repository implementation extends AbstractCRUDRepository
			Class<?> crudImplementation = (Class<?>) crudImplementationClassSet.iterator().next();
			if (!AbstractCRUDRepository.class.isAssignableFrom(crudImplementation)) {
				throw new CredibilityDaoRuntimeException(CredibilityDaoRuntimeMessage.NOT_DAOCRUD_IMPLEMENTATION,
						crudImplementation.getName(), interfaceClass.getName());
			}

			// put into the repositories map
			mapInterface.put((Class<? extends ICRUDRepository<?, ?>>) interfaceClass,
					(Class<? extends AbstractCRUDRepository<?, ?>>) crudImplementation);
		}
	}

	/**
	 * Initialize local database with databasePath
	 * 
	 * @param databasePath the database path
	 * @throws CredibilityException          if a parameter is not valid.
	 * @throws SQLException                  if a sql exception occurs.
	 * @throws IOException                   if a file read exception occurs.
	 * @throws SqlToolError                  if a sql tool error occurs.
	 * @throws URISyntaxException            if a file path is not valid.
	 * @throws CredibilityMigrationException if a migration exception occurs.
	 */
	public void initialize(String databasePath) throws CredibilityException, SqlToolError, SQLException, IOException,
			URISyntaxException, CredibilityMigrationException {

		logger.debug("initializing dao loader with local database at:{}", databasePath); //$NON-NLS-1$

		// initialize database manager
		dbManager.initialize(databasePath);

		// execute sql database migration
		dbMigrationManager.executeMigration();
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

		isStarted = false;
		logger.debug("dao loader stopped"); //$NON-NLS-1$
	}

	/** {@inheritDoc} */
	@Override
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * @return the entity manager
	 */
	public EntityManager getEntityManager() {
		return dbManager.getEntityManager();
	}

	/**
	 * @return the database migration manager
	 */
	public IDBMigrationManager getDbMigrationManager() {
		return dbMigrationManager;
	}

	/**
	 * @return nativeQuery Repository
	 */
	public INativeQueryRepository getNativeQueryRepository() {
		populateRepository(nativeQueryRepository);
		return nativeQueryRepository;
	}

	/**
	 * @param <R>            the repository object
	 * @param interfaceClass the repository class
	 * @return the repository requested after instantiation and after being
	 *         populated.
	 */
	@SuppressWarnings("unchecked")
	public <R extends ICRUDRepository<?, ?>> R getRepository(Class<R> interfaceClass) {

		R repository = null;
		Class<R> repositoryClass = null;

		// check if is interface
		if (!interfaceClass.isInterface()) {
			throw new CredibilityDaoRuntimeException(CredibilityDaoRuntimeMessage.NOT_INTERFACE);
		}

		// get repository class
		repositoryClass = (Class<R>) mapInterface.get(interfaceClass);
		if (repositoryClass == null) {
			throw new CredibilityDaoRuntimeException(CredibilityDaoRuntimeMessage.NOT_FOUND, interfaceClass.getName());
		}

		if (mapRepositories.containsKey(repositoryClass)) {
			ICRUDRepository<?, ?> icrudRepository = mapRepositories.get(repositoryClass);
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
		populateRepository(repository);
		return repository;
	}

	/**
	 * @return the database directory path
	 */
	public String getDatabaseDirectoryPath() {
		return dbManager.getDatabaseDirectoryPath();
	}

	/**
	 * @return the database manager
	 */
	public IDBManager getDbManager() {
		return dbManager;
	}

	/**
	 * @param repository the dao repository to populate
	 * @return the repository populated with the entity manager
	 */
	private IRepository populateRepository(IRepository repository) {

		// set the entity manager
		if (repository != null && repository.getEntityManager() == null) {
			repository.setEntityManager(getEntityManager());
		}
		return repository;
	}

}
