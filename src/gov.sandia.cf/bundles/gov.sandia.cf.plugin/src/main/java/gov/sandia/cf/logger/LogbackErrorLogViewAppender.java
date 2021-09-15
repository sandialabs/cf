/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.statushandlers.StatusManager;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.AppenderBase;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;

/**
 * This class is a logger appender for logback library. It adds log messages to
 * the Eclipse Error Log View.
 * 
 * @author Didier Verstraete
 *
 * @param <E> the event type
 */
public class LogbackErrorLogViewAppender<E> extends AppenderBase<E> {

	/**
	 * Constructor
	 */
	public LogbackErrorLogViewAppender() {
		super();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void append(E event) {

		if (event instanceof ILoggingEvent) {

			// get the log level
			Level level = ((ILoggingEvent) event).getLevel();
			String message = ((ILoggingEvent) event).getFormattedMessage();
			IThrowableProxy throwableProxy = ((ILoggingEvent) event).getThrowableProxy();
			Throwable throwable = null;
			if (throwableProxy instanceof ThrowableProxy) {
				throwable = ((ThrowableProxy) throwableProxy).getThrowable();
			}
			int logLevel = IStatus.INFO;
			if (level != null) {
				if (level.equals(Level.ERROR)) {
					logLevel = IStatus.ERROR;
				} else if (level.equals(Level.WARN)) {
					logLevel = IStatus.WARNING;
				}
			}

			// construct the eclipse log message
			IStatus status = new Status(logLevel, CredibilityFrameworkConstants.CF_PLUGIN_NAME, message, throwable);
			StatusManager.getManager().handle(status, StatusManager.LOG);
		}
	}

}
