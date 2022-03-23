/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.common;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException;
import gov.sandia.cf.exceptions.CredibilityServiceRuntimeException.CredibilityServiceRuntimeMessage;

/**
 * The Class ServiceLoader.
 * 
 * @author Didier Verstraete
 */
public class ServiceLoader {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ServiceLoader.class);

	private ServiceLoader() {
		// Do not instantiate
	}

	/**
	 * Instantiate interface map with classes implementing service interface.
	 *
	 * @param annotation               the annotation to search
	 * @param interfacePackageToSearch the interface package to search
	 * @param implPackageToSearch      the impl package to search
	 * @return the map instantiated
	 */
	public static Map<Class<?>, Class<?>> load(final Class<? extends Annotation> annotation,
			final String interfacePackageToSearch, final String implPackageToSearch) {

		Map<Class<?>, Class<?>> mapInterface = new HashMap<>();

		// load interfaces
		Reflections reflectionsInterface = new Reflections(interfacePackageToSearch);
		Set<Class<?>> services = reflectionsInterface.getTypesAnnotatedWith(annotation);
		if (services != null && !services.isEmpty()) {
			services = services.stream().filter(Class::isInterface).collect(Collectors.toSet());
		}

		if (services == null) {
			return mapInterface;
		}

		// search implementation
		Reflections reflections = new Reflections(implPackageToSearch);

		for (Class<?> interfaceClass : services) {

			// search for the first implementation
			Set<?> serviceImplementationClassSet = reflections.getSubTypesOf(interfaceClass);

			// check if service implementation is found
			if (serviceImplementationClassSet == null || serviceImplementationClassSet.isEmpty()) {
				logger.error(CredibilityServiceRuntimeMessage.NOT_FOUND.getMessage(interfaceClass.getName()));
				throw new CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage.NOT_FOUND,
						interfaceClass.getName());
			}

			// get implementation
			Class<?> serviceImplementation = null;
			if (serviceImplementationClassSet.iterator() != null && serviceImplementationClassSet.iterator().hasNext())
				serviceImplementation = (Class<?>) serviceImplementationClassSet.iterator().next();

			// put into the services map
			mapInterface.put(interfaceClass, serviceImplementation);
		}

		return mapInterface;
	}

	/**
	 * Load.
	 *
	 * @param annotation      the annotation
	 * @param packageToSearch the package to search
	 * @return the list
	 */
	public static List<Class<?>> load(final Class<? extends Annotation> annotation, final String packageToSearch) {

		List<Class<?>> listClasses = new ArrayList<>();

		// load interfaces
		Reflections reflectionsInterface = new Reflections(packageToSearch);
		Set<Class<?>> services = reflectionsInterface.getTypesAnnotatedWith(annotation);
		if (services != null && !services.isEmpty()) {
			listClasses.addAll(services);
		}

		return listClasses;
	}
}
