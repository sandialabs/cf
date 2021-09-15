/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.text.MessageFormat;

/**
 * The Credibility Service Runtime Exception
 * 
 * @author Didier Verstraete
 *
 */
public class CredibilityServiceRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -5859715430831333189L;

	/**
	 * The default messages enum
	 * 
	 * @author Didier Verstraete
	 *
	 */
	@SuppressWarnings("javadoc")
	public enum CredibilityServiceRuntimeMessage {
		NOT_APPSERVICE_INTERFACE(
				"The specified interface {0} is not a valid service interface. It must extend IApplication interface."), //$NON-NLS-1$
		NOT_APPSERVICE_IMPLEMENTATION(
				"The found class {0} implementing interface {1} is not a valid service class. It must extend AApplication class."), //$NON-NLS-1$
		NOT_INTERFACE("Please specify a service interface instead of a service class"), //$NON-NLS-1$
		NOT_INITIALIZED("Please initialize the service by calling start() method before using it."), //$NON-NLS-1$
		NOT_FOUND("The specified interface {0} does not have an associated class for instantiation"), //$NON-NLS-1$
		INSTANTIATION_ERROR("The associated class of the specified interface {0} can not be instantiated: {1}"); //$NON-NLS-1$

		private String message;

		CredibilityServiceRuntimeMessage(String message) {
			this.message = message;
		}

		public String getMessage(Object... args) {
			return MessageFormat.format(message, args);
		}
	}

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 */
	public CredibilityServiceRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 * @param args    the message args see
	 *                {@link gov.sandia.cf.tools.RscTools#getString(String, Object...)}
	 */
	public CredibilityServiceRuntimeException(CredibilityServiceRuntimeMessage message, Object... args) {
		super(message.getMessage(args));
	}

	/**
	 * Constructor
	 * 
	 * @param e the exception to throw
	 */
	public CredibilityServiceRuntimeException(Exception e) {
		super(e);
	}
}
