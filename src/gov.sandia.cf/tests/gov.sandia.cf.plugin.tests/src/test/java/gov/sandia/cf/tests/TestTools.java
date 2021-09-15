/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tests;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

/**
 * Test tools
 * 
 * @author Didier Verstraete
 *
 */
public class TestTools {

	/**
	 * @param e          the exception
	 * @param messageKey the messageKey to find
	 * @return true if the exception contains the message key in parameter,
	 *         otherwise false
	 */
	public static boolean containsConstraintViolationException(ConstraintViolationException e, String messageKey) {
		if (e != null && messageKey != null) {
			for (ConstraintViolation<?> constraint : e.getConstraintViolations()) {
				if (constraint.getMessage() != null && constraint.getMessage().equals(messageKey)) {
					return true;
				}
			}
		}
		return false;
	}
}
