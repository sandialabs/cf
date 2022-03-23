/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import gov.sandia.cf.web.services.AWebClient;
import gov.sandia.cf.web.services.IWebClientManager;

/**
 * Manage Web app status.
 * 
 * @author Didier Verstraete
 *
 */
public class StatusWebClient extends AWebClient implements IStatusService {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(StatusWebClient.class);

	/**
	 * Instantiates a new web authentication service.
	 */
	public StatusWebClient() {
	}

	/**
	 * Instantiates a new web authentication service.
	 *
	 * @param webClientMgr the web client mgr
	 */
	public StatusWebClient(IWebClientManager webClientMgr) {
		super(webClientMgr);
	}

	/** {@inheritDoc} */
	@Override
	public boolean ping() {
		logger.debug("Ping {}", getWebClientMgr().getBaseURI()); //$NON-NLS-1$
		try {
			String returnCode = getWebClientMgr().getWebClient().get().uri(StatusRoute.ping()).retrieve()
					.onStatus(HttpStatus::is3xxRedirection, response -> {
						throw new WebClientResponseException(0, null, null, null, null);
					}).bodyToMono(String.class).onErrorContinue((throwable, o) -> {
						throw new WebClientResponseException(0, null, null, null, null);
					}).doOnError(throwable -> {
						throw new WebClientResponseException(0, null, null, null, null);
					}).block();

			logger.debug("Ping {} response {}", getWebClientMgr().getBaseURI(), returnCode); //$NON-NLS-1$
			return StatusRouteParam.isSuccessPingResponse(returnCode);
		} catch (WebClientResponseException | WebClientRequestException e) {
			return false;
		}
	}

}