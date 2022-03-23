/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web;

import java.text.MessageFormat;

/**
 * The Credibility Web Client Runtime Exception
 * 
 * @author Didier Verstraete
 *
 */
public class WebClientRuntimeException extends RuntimeException {

	private static final long serialVersionUID = -5859715430831333189L;

	/**
	 * The default messages enum
	 * 
	 * @author Didier Verstraete
	 *
	 */
	@SuppressWarnings("javadoc")
	public enum WebClientRuntimeMessage {
		NULL("The web client is null"); //$NON-NLS-1$

		private String message;

		WebClientRuntimeMessage(String message) {
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
	public WebClientRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * 
	 * @param message the exception message
	 * @param args    the message args see
	 *                {@link gov.sandia.cf.tools.RscTools#getString(String, Object...)}
	 */
	public WebClientRuntimeException(WebClientRuntimeMessage message, Object... args) {
		super(message.getMessage(args));
	}

	/**
	 * Constructor
	 * 
	 * @param e the exception to throw
	 */
	public WebClientRuntimeException(Exception e) {
		super(e);
	}
}
