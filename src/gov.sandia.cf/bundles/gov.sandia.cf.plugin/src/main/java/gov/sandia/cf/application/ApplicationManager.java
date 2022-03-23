/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.common.ServiceLoader;
import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.exceptions.CredibilityDatabaseInvalidException;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException.CredibilityServiceRuntimeMessage;

/**
 * Load the application layer classes and give access to the dao layer
 * 
 * @author Didier Verstraete
 *
 */
public class ApplicationManager implements IApplicationManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ApplicationManager.class);

	/**
	 * The dao layer manager
	 */
	private IDaoManager daoMgr;

	/**
	 * The bean validator
	 */
	private Validator validator;

	/**
	 * Map of interface and services
	 */
	private Map<Class<?>, Class<?>> mapInterface;

	/**
	 * Map of Model services
	 */
	private Map<Class<?>, Object> mapEnabledService;

	/**
	 * Defines the state of the loader
	 */
	private boolean isStarted = false;

	/**
	 * The constructor with a specific dao manager
	 */
	public ApplicationManager() {
		this.daoMgr = new DaoManager();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		logger.debug("application loader started"); //$NON-NLS-1$

		mapInterface = new HashMap<>();

		// load services
		mapInterface.putAll(ServiceLoader.load(Service.class, this.getClass().getPackage().getName(),
				this.getClass().getPackage().getName()));

		mapEnabledService = new HashMap<>();

		// instantiate application validation
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();

		// start Dao Manager
		if (!daoMgr.isStarted()) {
			daoMgr.start();
		}

		isStarted = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {

		// stop the dao manager
		daoMgr.stop();

		isStarted = false;
		logger.debug("application loader stopped"); //$NON-NLS-1$
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S> S getService(Class<S> interfaceClass) {

		S service = null;
		Class<S> serviceClass = null;

		// check if is interface
		if (!interfaceClass.isInterface()) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_INTERFACE);
		}

		// check if it is using Service annotation
		if (interfaceClass.getAnnotation(Service.class) == null) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_APPSERVICE_INTERFACE,
					interfaceClass, Service.class.getName());
		}

		// check if it is a IApplication service
		if (!IApplication.class.isAssignableFrom(interfaceClass)) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_APPSERVICE_INTERFACE,
					interfaceClass, IApplication.class.getName());
		}

		// check initialized
		if (mapInterface == null) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_INITIALIZED);
		}

		// get repository class
		serviceClass = (Class<S>) mapInterface.get(interfaceClass);
		if (serviceClass == null) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_FOUND,
					interfaceClass.getName());
		}

		if (mapEnabledService.containsKey(serviceClass)) {
			Object iService = mapEnabledService.get(serviceClass);
			if (serviceClass.isInstance(iService)) {
				service = serviceClass.cast(iService);
			}
		} else {
			try {
				service = serviceClass.newInstance();
				mapEnabledService.put(serviceClass, service);
			} catch (InstantiationException | IllegalAccessException e) {
				throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.INSTANTIATION_ERROR,
						interfaceClass.getName(), e.getMessage());
			}
		}

		// populate
		populate((IApplication) service);

		return service;
	}

	/**
	 * @param service the service to populate
	 * @return the service populated with the entity manager
	 */
	private IApplication populate(IApplication service) {

		// set the application manager
		if (service != null && service.getAppMgr() == null) {
			service.setAppMgr(this);
		}
		return service;
	}

	/**
	 * @return the bean validator
	 */
	@Override
	public Validator getValidator() {
		return validator;
	}

	/**
	 * @return the dao manager
	 */
	@Override
	public IDaoManager getDaoManager() {
		return daoMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStarted() {
		return isStarted;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void initializeLocalDB(String projectPath) throws SqlToolError, CredibilityException, SQLException,
			IOException, URISyntaxException, CredibilityMigrationException, CredibilityDatabaseInvalidException {
		daoMgr.initialize(projectPath);
	}
}
