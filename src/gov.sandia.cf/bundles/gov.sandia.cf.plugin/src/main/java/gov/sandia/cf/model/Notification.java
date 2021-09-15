/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.util.HashSet;
import java.util.Set;

/**
 * CF Notification
 * 
 * @author Didier Verstraete
 *
 */
public class Notification {

	private NotificationType type;

	private Set<String> messages;

	/**
	 * @return the notification type
	 */
	public NotificationType getType() {
		return type;
	}

	/**
	 * @param type the notification type
	 */
	public void setType(NotificationType type) {
		this.type = type;
	}

	/**
	 * @return the set of messages
	 */
	public Set<String> getMessages() {
		return messages;
	}

	/**
	 * Add a message to the notification
	 * 
	 * @param message the message to add
	 */
	public void addMessage(String message) {
		if (messages == null) {
			messages = new HashSet<>();
		}
		messages.add(message);
	}

	/**
	 * Add a set of messages to the existing ones
	 * 
	 * @param message the message set to add
	 */
	public void addMessage(Set<String> message) {
		if (messages == null) {
			messages = new HashSet<>();
		}
		messages.addAll(message);
	}

	/**
	 * Clear all messages
	 */
	public void clearMessages() {
		messages.clear();
	}

	/**
	 * @return true if the notification type is error
	 */
	public boolean isError() {
		return type != null && NotificationType.ERROR.equals(type);
	}
}
