/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.intendedpurpose;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import gov.sandia.cf.application.IApplicationManager;
import gov.sandia.cf.application.intendedpurpose.IIntendedPurposeApp;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.IntendedPurposeDto;
import gov.sandia.cf.tools.GsonTools;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.services.IWebClientManager;

/**
 * Intended Purpose Application manager
 * 
 * @author Didier Verstraete
 * 
 */
public class IntendedPurposeWebClient implements IIntendedPurposeApp, IIntendedPurposeWebClient {

	/** The web client mgr. */
	private IWebClientManager webClientMgr;

	/** The app mgr. */
	private IApplicationManager appMgr;

	/**
	 * Instantiates a new intended purpose web client.
	 */
	public IntendedPurposeWebClient() {
		super();
	}

	/**
	 * Instantiates a new intended purpose web client.
	 *
	 * @param webClientMgr the web client manager
	 * @param appMgr       the app mgr
	 */
	public IntendedPurposeWebClient(IWebClientManager webClientMgr, IApplicationManager appMgr) {
		this.webClientMgr = webClientMgr;
		this.appMgr = appMgr;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isIntendedPurposeEnabled(Model model) {
		// Always activated
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public IntendedPurpose get(Model model) throws CredibilityException {
		try {
			return IntendedPurposeMapper.toApp(
					GsonTools.getFromGson(getWebClientMgr().getWebClient().get().uri(IntendedPurposeRoute.get(model))
							.retrieve().bodyToMono(String.class).block(), IntendedPurposeDto.class));
		} catch (WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		} catch (WebClientResponseException e) {
			throw new CredibilityException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public IntendedPurpose updateIntendedPurpose(Model model, String token, IntendedPurpose intendedPurpose,
			User userUpdate) throws CredibilityException {

		Map<String, Object> map = new HashMap<>();
		map.put(IntendedPurposeRouteParam.lockToken(), token);
		map.put(IntendedPurposeRouteParam.intendedPurpose(), IntendedPurposeMapper.toWeb(intendedPurpose));
		String bodyContent = GsonTools.toGson(map);

		try {
			getWebClientMgr().getWebClient().put().uri(IntendedPurposeRoute.update(model))
					.contentType(MediaType.APPLICATION_JSON).bodyValue(bodyContent).retrieve().bodyToMono(String.class)
					.block();
		} catch (WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		} catch (WebClientResponseException e) {
			throw new CredibilityException(e);
		}

		return intendedPurpose;
	}

	/** {@inheritDoc} */
	@Override
	public String lock(Model model, String information) throws CredibilityException {

		Map<String, Object> map = new HashMap<>();
		map.put(IntendedPurposeRouteParam.information(), information);
		String bodyContent = GsonTools.toGson(map);

		try {
			return getWebClientMgr().getWebClient().put().uri(IntendedPurposeRoute.lock(model))
					.contentType(MediaType.APPLICATION_JSON).bodyValue(bodyContent).retrieve().bodyToMono(String.class)
					.block();
		} catch (WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		} catch (WebClientResponseException e) {
			throw new CredibilityException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void unlock(Model model, String token) throws CredibilityException {

		Map<String, Object> map = new HashMap<>();
		map.put(IntendedPurposeRouteParam.lockToken(), token);
		String bodyContent = GsonTools.toGson(map);

		try {
			getWebClientMgr().getWebClient().put().uri(IntendedPurposeRoute.unlock(model))
					.contentType(MediaType.APPLICATION_JSON).bodyValue(bodyContent).retrieve().bodyToMono(String.class)
					.block();
		} catch (WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		} catch (WebClientResponseException e) {
			throw new CredibilityException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getLockInfo(Model model) throws CredibilityException {

		try {
			return getWebClientMgr().getWebClient().get().uri(IntendedPurposeRoute.lockInfo(model)).retrieve()
					.bodyToMono(String.class).block();
		} catch (WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		} catch (WebClientResponseException e) {
			throw new CredibilityException(e);
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLocked(Model model) {
		try {
			return getLockInfo(model) != null;
		} catch (WebClientRuntimeException | CredibilityException e) {
			return true;
		}
	}

	@Override
	public IApplicationManager getAppMgr() {
		return appMgr;
	}

	@Override
	public void setAppMgr(IApplicationManager appMgr) {
		this.appMgr = appMgr;
	}

	@Override
	public IDaoManager getDaoManager() {
		// TODO remove in interface
		return null;
	}

	@Override
	public IWebClientManager getWebClientMgr() {
		return webClientMgr;
	}

	@Override
	public void setWebClientMgr(IWebClientManager webClientMgr) {
		this.webClientMgr = webClientMgr;
	}

}
