/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import gov.sandia.cf.model.Notification;
import gov.sandia.cf.model.NotificationFactory;
import gov.sandia.cf.model.NotificationType;

/**
 * The Class WebNotificationMapper.
 * 
 * @author Didier Verstraete
 */
public class WebNotificationMapper {

	private WebNotificationMapper() {
		// Do not instantiate
	}

	/**
	 * To notification.
	 *
	 * @param webNotification the web notification
	 * @return the notification
	 */
	public static Notification toNotification(WebNotification webNotification) {
		if (webNotification == null || StringUtils.isBlank(webNotification.getValue())) {
			return null;
		}

		Optional<NotificationType> typeOptional = NotificationType.getType(webNotification.getType());
		NotificationType type = typeOptional.isPresent() ? typeOptional.get() : NotificationType.INFO;

		return NotificationFactory.getNew(type, webNotification.getValue());
	}
}
