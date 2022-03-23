/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.global;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.dto.ModelDto;
import gov.sandia.cf.tools.GsonTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.web.WebClientRuntimeException;
import gov.sandia.cf.web.services.AWebClient;
import gov.sandia.cf.web.services.WebClientManager;

/**
 * The Class ModelWebClient.
 *
 * @author Didier Verstraete
 */
public class ModelWebClient extends AWebClient implements IModelWebClient {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ModelWebClient.class);

	/**
	 * GlobalApplication constructor
	 */
	public ModelWebClient() {
		super();
	}

	/**
	 * GlobalApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ModelWebClient(WebClientManager appMgr) {
		super(appMgr);
	}

	@Override
	public Model loadModel(Integer modelId) throws CredibilityException {

		logger.debug("load model {}", modelId); //$NON-NLS-1$

		if (modelId == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_MODELWEBCLIENT_LOAD_MODEL_NULL));
		}

		try {
			return ModelMapper.toApp(GsonTools.getFromGson(getWebClientMgr().getWebClient().get()
					.uri(ModelRoute.get(ModelFactory.get(modelId))).retrieve().bodyToMono(String.class).block(),
					ModelDto.class));
		} catch (WebClientResponseException | WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		}
	}

	@Override
	public Model create(Model model) throws CredibilityException {

		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_MODELWEBCLIENT_CREATE_MODEL_NULL));
		}

		logger.debug("Create model {}", model); //$NON-NLS-1$

		Map<String, Object> map = new HashMap<>();
		map.put(ModelRouteParam.model(), ModelMapper.toWeb(model));
		String bodyContent = GsonTools.toGson(map);

		try {
			return ModelMapper.toApp(GsonTools.getFromGson(getWebClientMgr().getWebClient().post()
					.uri(ModelRoute.create()).contentType(MediaType.APPLICATION_JSON).bodyValue(bodyContent).retrieve()
					.bodyToMono(String.class).block(), ModelDto.class));
		} catch (WebClientResponseException | WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		}
	}

	@Override
	public List<Model> list() {
		try {
			return Arrays.asList(GsonTools.getFromGson(getWebClientMgr().getWebClient().get().uri(ModelRoute.list())
					.retrieve().bodyToMono(String.class).block(), Model[].class));
		} catch (WebClientResponseException | WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		}
	}

	@Override
	public void delete(Integer modelId) throws CredibilityException {

		logger.debug("delete model {}", modelId); //$NON-NLS-1$

		if (modelId == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_MODELWEBCLIENT_DELETE_MODEL_NULL));
		}

		try {
			getWebClientMgr().getWebClient().delete().uri(ModelRoute.delete(ModelFactory.get(modelId))).retrieve()
					.bodyToMono(String.class).block();

			// TODO implement 405 not found exception, 403 forbidden

		} catch (WebClientResponseException | WebClientRequestException e) {
			throw new WebClientRuntimeException(e);
		}

	}
}
