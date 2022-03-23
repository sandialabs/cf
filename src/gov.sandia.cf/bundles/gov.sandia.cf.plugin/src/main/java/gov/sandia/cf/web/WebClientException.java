/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web;

/**
 * The Class WebClientException.
 * 
 * @author Didier Verstraete
 */
public class WebClientException extends Exception {

	private static final long serialVersionUID = 1393943285726492117L;

	/**
	 * Instantiates a new web client exception.
	 */
	public WebClientException() {
		super();
	}

	/**
	 * Instantiates a new web client exception.
	 *
	 * @param message the message
	 */
	public WebClientException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new web client exception.
	 *
	 * @param cause the cause
	 */
	public WebClientException(Throwable cause) {
		super(cause);
	}
}
