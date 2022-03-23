/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.status;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.web.services.IWebClientManager;

/**
 * The Class MessageManager.
 *
 * @author Didier Verstraete
 */
public class PingManager implements IPingManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PingManager.class);

	/** The list listener. */
	private List<IConnectionStatusListener> listListener;

	/**
	 * Defines the state of the loader
	 */
	private boolean isStarted = false;

	private IWebClientManager webClientManager;

	private int defaultTimerMilliSeconds = 10000;

	private Runnable runnable;

	private boolean isConnected;

	private Display display;

	/**
	 * Instantiates a new message manager.
	 *
	 * @param webClientManager the web client manager
	 * @param display          the display
	 */
	public PingManager(IWebClientManager webClientManager, Display display) {
		Assert.isNotNull(webClientManager);
		Assert.isTrue(webClientManager.isStarted());
		Assert.isNotNull(display);
		Assert.isTrue(!display.isDisposed());
		this.webClientManager = webClientManager;
		this.listListener = new ArrayList<>();
		this.runnable = getPingTask();
		this.display = display;
		this.isConnected = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		logger.debug("Ping service started"); //$NON-NLS-1$

		isStarted = true;

		runnable.run();
	}

	/**
	 * Gets the ping task.
	 *
	 * @return the ping task
	 */
	private Runnable getPingTask() {
		return new Runnable() {

			public void run() {
				if (!webClientManager.getService(IStatusService.class).ping()) {
					if (isConnected) {
						isConnected = false;
						listListener.forEach(listener -> {
							if (listener != null) {
								listener.connectionLost();
							}
						});
					}
				} else {
					if (!isConnected) {
						isConnected = true;
						listListener.forEach(listener -> {
							if (listener != null) {
								listener.connectionGained();
							}
						});
					}
				}

				if (display == null || display.isDisposed()) {
					logger.error("Impossible to continue Ping Manager because the Display is null or disposed."); //$NON-NLS-1$
					stop();
				}

				if (isStarted) { // plan a new execution
					display.timerExec(defaultTimerMilliSeconds, this);
					logger.debug("Ping server"); //$NON-NLS-1$
				}
			}
		};
	}

	@Override
	public void addListener(IConnectionStatusListener listener) {
		listListener.add(listener);
	}

	@Override
	public void removeListener(IConnectionStatusListener listener) {
		listListener.remove(listener);
	}

	@Override
	public void stop() {
		isStarted = false;
		logger.debug("application loader stopped"); //$NON-NLS-1$
	}

	@Override
	public boolean isStarted() {
		return isStarted;
	}

	@Override
	public boolean isConnected() {
		return isConnected;
	}
}
