/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.tools;

import java.util.HashSet;
import java.util.Set;

import javax.validation.ConstraintViolation;

/**
 * 
 * The validation tools class
 * 
 * @author Didier Verstraete
 *
 */
public class ValidationTools {

	/**
	 * Private constructor to not allow instantiation.
	 */
	private ValidationTools() {
	}

	/**
	 * @param <E>         the entity object
	 * @param constraints the constraints to convert
	 * @return a set of messages of the constraint set
	 */
	public static <E> Set<String> constraintsToSetString(Set<ConstraintViolation<E>> constraints) {
		Set<String> messages = new HashSet<>();
		if (constraints != null && !constraints.isEmpty()) {
			for (ConstraintViolation<E> constraint : constraints) {
				messages.add(RscTools.getString(constraint.getMessage()));
			}
		}
		return messages;
	}

	/**
	 * @param <E>         the entity object
	 * @param constraints the constraints to convert
	 * @return a set of messages of the constraint set
	 */
	public static <E> String constraintsToString(Set<ConstraintViolation<E>> constraints) {
		StringBuilder str = new StringBuilder();
		if (constraints != null && !constraints.isEmpty()) {
			for (ConstraintViolation<E> constraint : constraints) {
				str.append("- "); //$NON-NLS-1$
				str.append(RscTools.getString(constraint.getMessage()));
				str.append("\n"); //$NON-NLS-1$
			}
		}
		return str.toString();
	}
}
