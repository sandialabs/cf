/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.text.MessageFormat;
import java.util.Set;

/**
 * CF Notification Factory
 * 
 * @author Didier Verstraete
 *
 */
public class NotificationFactory {

	private NotificationFactory() {
		// do nothing
	}

	/**
	 * @param type the notification type
	 * @return a new notification for the specified type
	 */
	public static Notification getNew(NotificationType type) {
		Notification notification = new Notification();
		notification.setType(type);
		return notification;
	}

	/**
	 * @return a new notification of type info
	 */
	public static Notification getNewInfo() {
		return getNew(NotificationType.INFO);
	}

	/**
	 * @return a new notification of type warning
	 */
	public static Notification getNewWarning() {
		return getNew(NotificationType.WARN);
	}

	/**
	 * @return a new notification of type error
	 */
	public static Notification getNewError() {
		return getNew(NotificationType.ERROR);
	}

	/**
	 * @param type    the notification type
	 * @param message the notification message
	 * @return a new notification
	 */
	public static Notification getNew(NotificationType type, String message) {
		Notification notification = new Notification();
		notification.setType(type);
		if (message != null) {
			notification.addMessage(message);
		}
		return notification;
	}

	/**
	 * @param message the notification message
	 * @return a new notification of type info
	 */
	public static Notification getNewInfo(String message) {
		return getNew(NotificationType.INFO, message);
	}

	/**
	 * @param message the notification message
	 * @return a new notification of type warning
	 */
	public static Notification getNewWarning(String message) {
		return getNew(NotificationType.WARN, message);
	}

	/**
	 * @param message the notification message
	 * @return a new notification of type error
	 */
	public static Notification getNewError(String message) {
		return getNew(NotificationType.ERROR, message);
	}

	/**
	 * @param message the message to parse
	 * @param args    the message arguments
	 * @return a new notification of type info
	 */
	public static Notification getNewInfo(String message, Object... args) {
		return getNewInfo(MessageFormat.format(message, args));
	}

	/**
	 * @param message the message to parse
	 * @param args    the message arguments
	 * @return a new notification of type warning
	 */
	public static Notification getNewWarning(String message, Object... args) {
		return getNewWarning(MessageFormat.format(message, args));
	}

	/**
	 * @param message the message to parse
	 * @param args    the message arguments
	 * @return a new notification of type error
	 */
	public static Notification getNewError(String message, Object... args) {
		return getNewError(MessageFormat.format(message, args));
	}

	/**
	 * @param type    the notification type
	 * @param message the set of messages
	 * @return a new notification
	 */
	public static Notification getNew(NotificationType type, Set<String> message) {
		Notification notification = new Notification();
		notification.setType(type);
		if (message != null) {
			notification.addMessage(message);
		}
		return notification;
	}

	/**
	 * @param message the set of messages
	 * @return a new notification of type info
	 */
	public static Notification getNewInfo(Set<String> message) {
		return getNew(NotificationType.INFO, message);
	}

	/**
	 * @param message the set of messages
	 * @return a new notification of type warning
	 */
	public static Notification getNewWarning(Set<String> message) {
		return getNew(NotificationType.WARN, message);
	}

	/**
	 * @param message the set of messages
	 * @return a new notification of type error
	 */
	public static Notification getNewError(Set<String> message) {
		return getNew(NotificationType.ERROR, message);
	}

}
