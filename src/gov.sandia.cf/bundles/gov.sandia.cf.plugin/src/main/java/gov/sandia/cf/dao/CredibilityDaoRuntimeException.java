/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao;

import java.text.MessageFormat;

/**
 * The Credibility Dao Runtime Exception
 * 
 * @author Didier Verstraete
 *
 */
public class CredibilityDaoRuntimeException extends RuntimeException {

	/**
	 * The default messages enum
	 * 
	 * @author Didier Verstraete
	 *
	 */
	@SuppressWarnings("javadoc")
	public enum CredibilityDaoRuntimeMessage {
		NOT_DAOCRUD_INTERFACE(
				"The specified interface {0} is not a valid DAO repository interface. It must extend ICRUDRepository interface."), //$NON-NLS-1$
		NOT_DAOCRUD_IMPLEMENTATION(
				"The found class {0} implementing interface {1} is not a valid DAO repository class. It must extend AbstractCRUDRepository class."), //$NON-NLS-1$
		NOT_INTERFACE("Please specify a repository interface instead of a repository class"), //$NON-NLS-1$
		NOT_FOUND("The specified interface {0} does not have an associated class for instantiation"); //$NON-NLS-1$

		private String message;

		CredibilityDaoRuntimeMessage(String message) {
			this.message = message;
		}

		public String getMessage(Object... args) {
			return MessageFormat.format(message, args);
		}
	}

	private static final long serialVersionUID = 9154032976025914128L;

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 */
	public CredibilityDaoRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 * @param args    the message args see
	 *                {@link gov.sandia.cf.tools.RscTools#getString(String, Object...)}
	 */
	public CredibilityDaoRuntimeException(CredibilityDaoRuntimeMessage message, Object... args) {
		super(message.getMessage(args));
	}

	/**
	 * Constructor
	 * 
	 * @param e the exception to throw
	 */
	public CredibilityDaoRuntimeException(Exception e) {
		super(e);
	}
}
