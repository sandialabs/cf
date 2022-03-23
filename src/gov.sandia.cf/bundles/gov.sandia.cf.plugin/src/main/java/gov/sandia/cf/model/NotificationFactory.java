/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

import java.text.MessageFormat;
import java.util.Set;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.widgets.FormFactory;
import gov.sandia.cf.tools.ColorTools;

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
	 * @return a new notification of type success
	 */
	public static Notification getNewSuccess() {
		return getNew(NotificationType.SUCCESS);
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
	 * @return a new notification of type success
	 */
	public static Notification getNewSuccess(String message) {
		return getNew(NotificationType.SUCCESS, message);
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
	 * Gets the new success.
	 *
	 * @param message the message to parse
	 * @param args    the message arguments
	 * @return a new notification of type info
	 */
	public static Notification getNewSuccess(String message, Object... args) {
		return getNewSuccess(MessageFormat.format(message, args));
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
	 * @return a new notification of type info
	 */
	public static Notification getNewSuccess(Set<String> message) {
		return getNew(NotificationType.SUCCESS, message);
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

	/**
	 * @param notification the notification
	 * @return the notification color associated
	 */
	public static String getNotificationColorName(Notification notification) {
		if (notification == null) {
			return null;
		}

		if (NotificationType.SUCCESS.equals(notification.getType())) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_SUCCESS);
		} else if (NotificationType.INFO.equals(notification.getType())) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_INFO);
		} else if (NotificationType.WARN.equals(notification.getType())) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_WARNING);
		} else if (NotificationType.ERROR.equals(notification.getType())) {
			return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_ERROR);
		}

		return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
	}

	/**
	 * Gets the notification color.
	 *
	 * @param rscMgr       the rsc mgr
	 * @param notification the notification
	 * @return the notification color associated
	 */
	public static Color getNotificationColor(ResourceManager rscMgr, Notification notification) {
		if (notification == null) {
			return null;
		}

		return ColorTools.toColor(rscMgr, getNotificationColorName(notification));
	}

	/**
	 * Gets the notification background color name.
	 *
	 * @param notification the notification
	 * @return the notification background color name
	 */
	public static String getNotificationBackgroundColorName(Notification notification) {
		return ConstantTheme.getAssociatedColor(getNotificationColorName(notification));
	}

	/**
	 * Gets the notification background color.
	 *
	 * @param rscMgr       the rsc mgr
	 * @param notification the notification
	 * @return the notification background color
	 */
	public static Color getNotificationBackgroundColor(ResourceManager rscMgr, Notification notification) {
		if (notification == null) {
			return null;
		}

		return ColorTools.toColor(rscMgr, getNotificationBackgroundColorName(notification));
	}

	/**
	 * @param rscMgr       the resource manager used to manage the resources (fonts,
	 *                     colors, images, cursors...)
	 * @param notification the notification
	 * @return the notification icon associated
	 */
	public static Image getNotificationIcon(ResourceManager rscMgr, Notification notification) {
		if (notification == null) {
			return null;
		}

		if (NotificationType.SUCCESS.equals(notification.getType())) {
			return FormFactory.getSuccessIcon(rscMgr);
		} else if (NotificationType.INFO.equals(notification.getType())) {
			return FormFactory.getInfoIcon(rscMgr);
		} else if (NotificationType.WARN.equals(notification.getType())) {
			return FormFactory.getWarningIcon(rscMgr);
		} else if (NotificationType.ERROR.equals(notification.getType())) {
			return FormFactory.getErrorIcon(rscMgr);
		}

		return null;
	}

}
