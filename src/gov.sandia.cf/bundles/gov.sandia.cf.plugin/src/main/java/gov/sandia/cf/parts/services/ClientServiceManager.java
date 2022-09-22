/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.services;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.Service;
import gov.sandia.cf.common.ServiceLoader;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException.CredibilityServiceRuntimeMessage;

/**
 * Load the client service layer classes and give access to the dao layer
 * 
 * @author Didier Verstraete
 *
 */
public class ClientServiceManager implements IClientServiceManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ClientServiceManager.class);

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
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		logger.debug("client service loader started"); //$NON-NLS-1$

		mapInterface = new HashMap<>();
		mapInterface.putAll(ServiceLoader.load(Service.class, ClientServiceManager.class.getPackage().getName(),
				ClientServiceManager.class.getPackage().getName()));

		mapEnabledService = new HashMap<>();

		isStarted = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {
		isStarted = false;
		logger.debug("client service loader stopped"); //$NON-NLS-1$
	}

	@Override
	@SuppressWarnings("unchecked")
	public <S extends IClientService> S getService(Class<S> interfaceClass) {

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

		// check if it is a IClientService service
		if (!IClientService.class.isAssignableFrom(interfaceClass)) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_APPSERVICE_INTERFACE,
					interfaceClass, IClientService.class.getName());
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
				service = serviceClass.getDeclaredConstructor().newInstance();
				mapEnabledService.put(serviceClass, service);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
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
	private IClientService populate(IClientService service) {

		// set the client servicetion manager
		if (service != null && service.getClientSrvMgr() == null) {
			service.setClientSrvMgr(this);
		}
		return service;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStarted() {
		return isStarted;
	}
}
