/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

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
}
