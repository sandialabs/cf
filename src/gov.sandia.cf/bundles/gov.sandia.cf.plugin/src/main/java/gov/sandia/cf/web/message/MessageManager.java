/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.message;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.web.IWebEventListener;
import gov.sandia.cf.web.WebClientException;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.WebEvent;
import gov.sandia.cf.web.services.IWebClientManager;
import reactor.core.publisher.Flux;

/**
 * The Class MessageManager.
 *
 * @author Didier Verstraete
 */
public class MessageManager implements IMessageManager {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MessageManager.class);

	/** The list listener. */
	private List<IWebEventListener> listListener;

	/**
	 * Defines the state of the loader
	 */
	private boolean isStarted = false;

	/** The server member id. */
	private String serverMemberId;

	private IWebClientManager webClientManager;

	/**
	 * Instantiates a new message manager.
	 *
	 * @param webClientManager the web client manager
	 */
	public MessageManager(IWebClientManager webClientManager) {
		Assert.isNotNull(webClientManager);
		Assert.isTrue(webClientManager.isStarted());
		this.webClientManager = webClientManager;
		this.listListener = new ArrayList<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {
		logger.debug("Web Message service started"); //$NON-NLS-1$

		try {
			// get connection
			serverMemberId = connect();

			// listen messages
			listen();

			isStarted = true;

		} catch (WebClientException e) {
			logger.error("An error occurs during connection: {}", e.getMessage()); //$NON-NLS-1$
			throw new WebClientRuntimeException(e);
		}
	}

	/**
	 * Connect.
	 *
	 * @return the string
	 * @throws WebClientException the web client exception
	 */
	private String connect() throws WebClientException {
		try {
			return webClientManager.getWebClient().get().uri(MessageRoute.connect()).retrieve().bodyToMono(String.class)
					.block();
		} catch (WebClientResponseException | WebClientRequestException e) {
			throw new WebClientException(e);
		}
	}

	/**
	 * Listen.
	 *
	 * @throws WebClientException the web client exception
	 */
	private void listen() throws WebClientException {
		try {

			ParameterizedTypeReference<ServerSentEvent<String>> type = new ParameterizedTypeReference<ServerSentEvent<String>>() {
			};

			Flux<ServerSentEvent<String>> eventStream = webClientManager.getWebClient().get()
					.uri(MessageRoute.get(serverMemberId)).retrieve().bodyToFlux(type).retry();

			eventStream.subscribe(content -> {
				logger.debug("Time: {} - event: name[{}], id [{}], content[{}] ", LocalTime.now(), content.event(), //$NON-NLS-1$
						content.id(), content.data());
				WebEvent event = new WebEvent();
				event.id = content.id();
				event.data = content.data();
				listListener.forEach(listener -> listener.handle(event));
			}, error -> {
				logger.error("Error receiving SSE: {}", error.getMessage()); //$NON-NLS-1$
				listListener.forEach(listener -> listener.handleError(error));
			}, () -> {
				if (isStarted) {
					// TODO try to reopen connection if closed
				} else {
					logger.info("Connection completed."); //$NON-NLS-1$
				}
			});

		} catch (WebClientResponseException | WebClientRequestException e) {
			throw new WebClientException(e);
		}
	}

	@Override
	public void addListener(IWebEventListener listener) {
		listListener.add(listener);
	}

	@Override
	public void removeListener(IWebEventListener listener) {
		listListener.remove(listener);
	}

	@Override
	public void stop() {

		// stop connection with server
		try {
			disconnect();
		} catch (WebClientException e) {
			logger.error("An error occurs during disconnection: {}", e.getMessage()); //$NON-NLS-1$
		}

		serverMemberId = null;

		isStarted = false;
		logger.debug("application loader stopped"); //$NON-NLS-1$
	}

	/**
	 * Disconnect.
	 *
	 * @return the string
	 * @throws WebClientException the web client exception
	 */
	private String disconnect() throws WebClientException {
		try {
			return webClientManager.getWebClient().get().uri(MessageRoute.disconnect(serverMemberId)).retrieve()
					.bodyToMono(String.class).block();
		} catch (WebClientResponseException | WebClientRequestException e) {
			throw new WebClientException(e);
		}
	}

	@Override
	public boolean isStarted() {
		return isStarted;
	}

	@Override
	public void subscribeToModel(Model model) throws WebClientException {
		try {
			webClientManager.getWebClient().get().uri(MessageRoute.subscribeToModel(serverMemberId, model)).retrieve()
					.bodyToMono(String.class).block();
		} catch (WebClientResponseException | WebClientRequestException | WebClientRuntimeException e) {
			throw new WebClientException(e);
		}
	}

}
