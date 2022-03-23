/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.validation.Validator;

import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;

import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.IApplicationManager;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.common.ServiceLoader;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException.CredibilityServiceRuntimeMessage;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.WebClientRuntimeException.WebClientRuntimeMessage;

/**
 * Load the web client layer classes.
 * 
 * @author Didier Verstraete
 *
 */
public class WebClientManager implements IApplicationManager, IWebClientManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(WebClientManager.class);

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

	/** The spring web client. */
	private WebClient webClient;

	/** The web server base URI. */
	private String baseURI;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		logger.debug("web client loader started"); //$NON-NLS-1$

		mapInterface = new HashMap<>();

		// load services
		mapInterface.putAll(ServiceLoader.load(Service.class, WebClientManager.class.getPackage().getName(),
				WebClientManager.class.getPackage().getName()));
		mapInterface.putAll(ServiceLoader.load(Service.class, ApplicationManager.class.getPackage().getName(),
				WebClientManager.class.getPackage().getName()));

		mapEnabledService = new HashMap<>();

		isStarted = true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop() {

		// TODO stop connection with server

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
		if (!IApplication.class.isAssignableFrom(interfaceClass)
				&& !IWebClient.class.isAssignableFrom(interfaceClass)) {
			throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_APPSERVICE_INTERFACE,
					interfaceClass, "[" + IApplication.class.getName() + ", " + IWebClient.class.getName() + "]"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
		populate(service);

		return service;
	}

	/**
	 * @param service the service to populate
	 */
	private void populate(Object service) {

		// set the application manager
		if (service instanceof IApplication && ((IApplication) service).getAppMgr() == null) {
			((IApplication) service).setAppMgr(this);
		} else if (service instanceof IWebClient && ((IWebClient) service).getWebClientMgr() == null) {
			((IWebClient) service).setWebClientMgr(this);
		}
	}

	/**
	 * @return the bean validator
	 */
	@Override
	public Validator getValidator() {
		return null;
	}

	/**
	 * @return the dao manager
	 */
	@Override
	public IDaoManager getDaoManager() {
		return null;
	}

	@Override
	public void setBaseURI(String baseURI) {
		this.baseURI = baseURI;

		// reload web client
		if (webClient == null) {
			webClient = WebClient.create(getBaseURI());
		} else {
			webClient = webClient.mutate().baseUrl(getBaseURI()).build();
		}
	}

	@Override
	public String getBaseURI() {
		return this.baseURI;
	}

	@Override
	public WebClient getWebClient() {
		if (webClient == null) {
			throw new WebClientRuntimeException(WebClientRuntimeMessage.NULL);
		}
		return webClient;
	}

	@Override
	public boolean isStarted() {
		return isStarted;
	}

	@Override
	public void initializeLocalDB(String projectPath) throws SqlToolError, CredibilityException, SQLException,
			IOException, URISyntaxException, CredibilityMigrationException {
		// do nothing
	}
}
