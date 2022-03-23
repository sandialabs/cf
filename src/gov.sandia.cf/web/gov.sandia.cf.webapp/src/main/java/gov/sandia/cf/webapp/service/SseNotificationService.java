/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * The Class SseNotificationService.
 * 
 * @author Didier Verstraete
 */
@Service
public class SseNotificationService {

	private Map<String, SseEmitter> userEmitterMap = new ConcurrentHashMap<>();
	private Map<Long, Set<String>> modelEmitterMap = new ConcurrentHashMap<>();

	/**
	 * Adds the connection.
	 *
	 * @param memberId the member id
	 * @return the member id
	 */
	public String add() {

		// generate member id
		final String localMemberId = UUID.randomUUID().toString();

		// create connection
		SseEmitter emitter = new SseEmitter();

		// add
		userEmitterMap.put(localMemberId, emitter);

		// listeners
		emitter.onCompletion(() -> {
			userEmitterMap.remove(localMemberId);
		});
		emitter.onTimeout(() -> {
			emitter.complete();
			userEmitterMap.remove(localMemberId);
		});
		emitter.onError(e -> {
			emitter.complete();
			userEmitterMap.remove(localMemberId);
		});
		return localMemberId;
	}

	/**
	 * Gets the Sse Emitter.
	 *
	 * @param memberId the member id
	 * @return the sse emitter
	 */
	public SseEmitter get(String memberId) {
		return userEmitterMap.get(memberId);
	}

	/**
	 * Adds the connection.
	 *
	 * @param memberId the member id
	 * @return the sse emitter
	 */
	public void remove(String memberId) {
		if (!StringUtils.hasText(memberId) || userEmitterMap.get(memberId) == null) {
			return;
		}

		// get
		SseEmitter sseEmitter = userEmitterMap.get(memberId);

		// complete connection
		sseEmitter.complete();

		// remove
		userEmitterMap.remove(memberId);
		modelEmitterMap.forEach((modelId, ids) -> {
			if (ids != null) {
				ids.remove(memberId);
			}
		});
	}

	/**
	 * Subscribe to model.
	 *
	 * @param modelId  the model id
	 * @param memberId the member id
	 */
	public void subscribeToModel(Long modelId, String memberId) {
		if (modelId == null) {
			return;
		}

		Set<String> ids = modelEmitterMap.get(modelId);
		if (ids == null) {
			ids = new HashSet<>();
			modelEmitterMap.put(modelId, ids);
		}
		
		ids.add(memberId);
	}

	/**
	 * Send.
	 *
	 * @param memberId the member id
	 * @param id       the id
	 * @param obj      the obj
	 */
	public void send(String memberId, String id, Object obj) {
		SseEmitter emitter = userEmitterMap.get(memberId);
		if (emitter != null) {
			try {
				userEmitterMap.get(memberId).send(SseEmitter.event().id(id).data(obj));
			} catch (Exception e) {
				emitter.completeWithError(e);
				userEmitterMap.remove(memberId);
			}
		}
	}

	/**
	 * Send.
	 *
	 * @param memberIdList the member id list
	 * @param id           the id
	 * @param obj          the obj
	 */
	public void send(List<String> memberIdList, String id, Object obj) {
		List<String> failedEmitters = new ArrayList<>();
		userEmitterMap.entrySet().stream().filter(entry -> memberIdList.contains(entry.getKey())).forEach(entry -> {
			if (entry != null && entry.getValue() != null) {
				try {
					entry.getValue().send(SseEmitter.event().id(id).data(obj));
				} catch (Exception e) {
					entry.getValue().completeWithError(e);
					failedEmitters.add(entry.getKey());
				}
			}
		});
		userEmitterMap.keySet().removeAll(failedEmitters);
	}

	/**
	 * Send to model.
	 *
	 * @param modelId the model id
	 * @param id      the id
	 * @param obj     the obj
	 */
	public void sendToModel(Long modelId, String id, Object obj) {
		Set<String> ids = modelEmitterMap.get(modelId);
		if (ids != null && !ids.isEmpty()) {
			send(new ArrayList<>(ids), id, obj);
		}
	}

	/**
	 * Send global.
	 *
	 * @param id  the id
	 * @param obj the obj
	 */
	public void sendGlobal(String id, Object obj) {
		send(new ArrayList<>(userEmitterMap.keySet()), id, obj);
	}
}
