/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.exceptions;

/**
 * The credibility runtime exception class
 * 
 * @author Didier Verstraete
 *
 */
public class CredibilityRuntimeException extends RuntimeException {
	/**
	 * serial version UID
	 */
	private static final long serialVersionUID = -119090096026766474L;

	/**
	 * Constructs a CredibilityException
	 */
	public CredibilityRuntimeException() {
		super();
	}

	/**
	 * Constructs a {@code CredibilityException} with the specified detail message.
	 *
	 * @param message The detail message (which is saved for later retrieval by the
	 *                {@link #getMessage()} method)
	 */
	public CredibilityRuntimeException(String message) {
		super(message);
	}

	/**
	 * Constructs a {@code CredibilityException} with the specified detail message
	 * and cause.
	 *
	 *
	 * @param message The detail message (which is saved for later retrieval by the
	 *                {@link #getMessage()} method)
	 *
	 * @param cause   The cause (which is saved for later retrieval by the
	 *                {@link #getCause()} method). (A null value is permitted, and
	 *                indicates that the cause is nonexistent or unknown.)
	 *
	 */
	public CredibilityRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a {@code CredibilityException} with the specified cause and a
	 * detail message of {@code (cause==null ? null : cause.toString())} (which
	 * typically contains the class and detail message of {@code cause}). This
	 * constructor is useful for Credibility exceptions that are little more than
	 * wrappers for other throwables.
	 *
	 * @param cause The cause (which is saved for later retrieval by the
	 *              {@link #getCause()} method). (A null value is permitted, and
	 *              indicates that the cause is nonexistent or unknown.)
	 *
	 */
	public CredibilityRuntimeException(Throwable cause) {
		super(cause);
	}
}
