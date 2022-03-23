/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.Optional;
import java.util.stream.Stream;

import gov.sandia.cf.tools.RscTools;

/**
 * CF Notification type
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum NotificationType {
	INFO("info"), //$NON-NLS-1$
	ERROR("error"), //$NON-NLS-1$
	SUCCESS("success"), //$NON-NLS-1$
	WARN("warning"); //$NON-NLS-1$

	/**
	 * The notification type
	 */
	private String type;

	NotificationType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}

	/**
	 * Gets the type.
	 *
	 * @param type the type
	 * @return the type
	 */
	public static Optional<NotificationType> getType(final String type) {
		final String typeToTest = (type == null) ? RscTools.empty() : type.replace(" ", ""); //$NON-NLS-1$ //$NON-NLS-2$
		return Stream.of(NotificationType.values()).filter(t -> t.type.equals(typeToTest)).findFirst();
	}

}
