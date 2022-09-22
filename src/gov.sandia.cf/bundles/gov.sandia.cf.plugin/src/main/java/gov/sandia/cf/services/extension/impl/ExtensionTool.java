/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.services.extension.impl;

import java.lang.reflect.Method;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.ISafeRunnableWithResult;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.exceptions.CredibilityRuntimeException;
import gov.sandia.cf.services.extensionpoint.IPredefinedProperties;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The Class ExtensionTool.
 *
 * @author Didier Verstraete
 */
public class ExtensionTool {

	private ExtensionTool() {
	}

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(ExtensionTool.class);

	/**
	 * Gets the first implementation of the extension point.
	 *
	 * @param <S>                          the interface class type
	 * @param interfaceClass               the interface class
	 * @param extensionPointId             the extension point id
	 * @param extensionPointClassAttribute the extension point class attribute
	 * @return the first implementation
	 */
	@SuppressWarnings("unchecked")
	public static <S> S getFirstImplementation(Class<S> interfaceClass, String extensionPointId,
			String extensionPointClassAttribute) {
		// The following will read all existing extensions for the defined ID
		IExtensionPoint extensionPoint2 = Platform.getExtensionRegistry()
				.getExtensionPoint(CredibilityFrameworkConstants.CF_PLUGIN_NAME, extensionPointId);
		IConfigurationElement[] extensions = extensionPoint2.getConfigurationElements();

		if (extensions != null && extensions.length > 0) {

			// get the first one
			try {
				for (IConfigurationElement e : extensions) {
					// instantiate extension
					final Object extension = e.createExecutableExtension(extensionPointClassAttribute);

					if (interfaceClass != null && interfaceClass.isInstance(extension)) {
						return (S) extension;
					}
				}
			} catch (CoreException ex) {
				logger.error(ex.getMessage());
			}
		}

		logger.info("No extension point defined for extension {}", //$NON-NLS-1$
				interfaceClass != null ? interfaceClass.getName() : "(null)"); //$NON-NLS-1$

		return null;
	}

	/**
	 * Execute.
	 *
	 * @param <S>                          the interface class
	 * @param <R>                          the method return type
	 * @param interfaceClass               the interface class
	 * @param extensionPointId             the extension point id
	 * @param extensionPointClassAttribute the extension point class attribute
	 * @param methodName                   the method name
	 * @param parameters                   the parameters
	 * @return the r
	 */
	public static <S, R> R execute(Class<S> interfaceClass, String extensionPointId,
			String extensionPointClassAttribute, String methodName, Object... parameters) {
		Method functionToPass = null;
		try {
			if (parameters == null || parameters.length <= 0) {
				functionToPass = IPredefinedProperties.class.getMethod(methodName);
			} else if (parameters.length == 1) {
				Class<?>[] parameterTypes = new Class[parameters.length];
				parameterTypes[0] = parameters[0].getClass();
				functionToPass = IPredefinedProperties.class.getMethod(methodName, parameterTypes[0]);
			} else {
				Class<?>[] parameterTypes = new Class[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					parameterTypes[i] = parameters[i] != null ? parameters[i].getClass() : null;
				}
				functionToPass = IPredefinedProperties.class.getMethod(methodName, parameterTypes);
			}
			return execute(interfaceClass, extensionPointId, extensionPointClassAttribute, functionToPass, parameters);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new CredibilityRuntimeException(RscTools.getString(RscConst.EX_EXTENSIONTOOL_EXECUTE_ERROR,
					extensionPointId, extensionPointClassAttribute, methodName), e);
		}
	}

	/**
	 * Execute.
	 *
	 * @param <S>                          the interface class
	 * @param <R>                          the method return type
	 * @param interfaceClass               the interface class
	 * @param extensionPointId             the extension point id
	 * @param extensionPointClassAttribute the extension point class attribute
	 * @param method                       the method
	 * @param parameters                   the parameters
	 * @return the result
	 */
	public static <S, R> R execute(Class<S> interfaceClass, String extensionPointId,
			String extensionPointClassAttribute, Method method, Object... parameters) {
		S extension = ExtensionTool.getFirstImplementation(interfaceClass, extensionPointId,
				extensionPointClassAttribute);

		if (extension == null) {
			return null;
		}

		if (method == null) {
			logger.warn("Method not defined for extension {}", interfaceClass.getName()); //$NON-NLS-1$
			return null;
		}

		// safely execute
		ISafeRunnableWithResult<R> runnable = new ISafeRunnableWithResult<>() {
			@Override
			public void handleException(Throwable e) {
				logger.error(e.getMessage(), e);
				throw new CredibilityRuntimeException(RscTools.getString(RscConst.EX_EXTENSIONTOOL_EXECUTE_ERROR,
						extensionPointId, extensionPointClassAttribute, method.getName()), e);
			}

			@SuppressWarnings("unchecked")
			@Override
			public R runWithResult() throws Exception {
				return (R) method.invoke(extension, parameters);
			}
		};
		return SafeRunner.run(runnable);
	}

}
