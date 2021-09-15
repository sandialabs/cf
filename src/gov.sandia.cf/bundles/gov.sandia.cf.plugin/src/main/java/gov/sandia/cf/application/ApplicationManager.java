/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import gov.sandia.cf.application.CredibilityServiceRuntimeException.CredibilityServiceRuntimeMessage;
import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

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
	private DaoManager daoMgr;

	/**
	 * The bean validator
	 */
	private Validator validator;

	/**
	 * Map of interface and entities repository
	 */
	private Map<Class<? extends IApplication>, Class<? extends AApplication>> mapInterface;

	/**
	 * Map of Model entities repository
	 */
	private Map<Class<? extends IApplication>, IApplication> mapEnabledService;

	/**
	 * Defines the state of the loader
	 */
	private boolean isStarted = false;

	/**
	 * The constructor with a specific dao manager
	 * 
	 * @param daoMgr the dao manager
	 */
	public ApplicationManager(DaoManager daoMgr) {
		if (daoMgr == null) {
			throw new IllegalArgumentException(RscTools.getString(RscConst.EX_APPMGR_DAOMGR_NULL));
		}
		this.daoMgr = daoMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		logger.debug("application loader started"); //$NON-NLS-1$

		instantiateInterfaceMap();

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

	@SuppressWarnings("unchecked")
	private void instantiateInterfaceMap() {

		// load service configuration file
		Map<String, Object> serviceConfiguration = null;
		try (InputStream stream = ApplicationManager.class.getClassLoader()
				.getResourceAsStream(AppManagerConstants.SERVICES_FILENAME)) {
			serviceConfiguration = new Yaml().load(stream);
		} catch (IOException e) {
			throw new CredibilityServiceRuntimeException(e);
		}

		if (serviceConfiguration == null) {
			logger.warn("The services configuration can not be loaded."); //$NON-NLS-1$
			return;
		}

		List<String> repositories = (List<String>) serviceConfiguration.get(AppManagerConstants.SERVICES_KEY);

		mapInterface = new HashMap<>();

		Reflections reflections = new Reflections(ApplicationManager.class.getPackage().getName());

		// start implementation
		for (String serviceInterfaceToImplement : repositories) {

			Class<?> interfaceClass;

			// get the service interface
			try {
				interfaceClass = Class.forName(serviceInterfaceToImplement);
			} catch (ClassNotFoundException e) {
				throw new CredibilityServiceRuntimeException(e);
			}

			// check service interface extends IApplication
			if (!IApplication.class.isAssignableFrom(interfaceClass)) {
				throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_APPSERVICE_INTERFACE,
						serviceInterfaceToImplement);
			}

			// search for the first implementation
			Set<?> serviceImplementationClassSet = reflections.getSubTypesOf(interfaceClass);

			// check if service implementation is found
			if (serviceImplementationClassSet == null || serviceImplementationClassSet.isEmpty()) {
				throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_FOUND,
						interfaceClass.getName());
			}

			// check if service implementation extends AApplication
			Class<?> serviceImplementation = (Class<?>) serviceImplementationClassSet.iterator().next();
			if (!AApplication.class.isAssignableFrom(serviceImplementation)) {
				throw new CredibilityServiceRuntimeException(
						CredibilityServiceRuntimeMessage.NOT_APPSERVICE_IMPLEMENTATION, serviceImplementation.getName(),
						interfaceClass.getName());
			}

			// put into the services map
			mapInterface.put((Class<? extends IApplication>) interfaceClass,
					(Class<? extends AApplication>) serviceImplementation);
		}
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
	public <S extends IApplication> S getService(Class<S> interfaceClass) {

		S service = null;
		Class<S> serviceClass = null;

		// check if is interface
		if (!interfaceClass.isInterface()) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_INTERFACE);
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
			IApplication iService = mapEnabledService.get(serviceClass);
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
		populate(service);

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
	public DaoManager getDaoManager() {
		return daoMgr;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStarted() {
		return isStarted;
	}

}
