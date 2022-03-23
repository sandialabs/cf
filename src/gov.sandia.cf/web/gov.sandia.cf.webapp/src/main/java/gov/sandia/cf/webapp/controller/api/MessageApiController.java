/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import gov.sandia.cf.webapp.service.SseNotificationService;

/**
 * The Class MessageApiController.
 *
 * @author Didier Verstraete
 */
@RestController
@RequestMapping("api/message")
public class MessageApiController {

	Logger logger = LoggerFactory.getLogger(MessageApiController.class);

	@Autowired
	private SseNotificationService notificationService;

	@GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public String connect() {
		logger.debug("Connected");

		// return member id
		return notificationService.add();
	}

	@GetMapping(path = "/get/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public SseEmitter getConnection(@PathVariable("memberId") String memberId) {
		logger.debug("Get connection");

		// return sse emitter
		return notificationService.get(memberId);
	}

	@GetMapping(path = "/subscribe/{memberId}/model/{modelId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public void subscribeToModel(@PathVariable("memberId") String memberId, @PathVariable("modelId") Long modelId) {
		logger.debug("{} subscribed to model {}", memberId, modelId);

		// subscribe to model
		notificationService.subscribeToModel(modelId, memberId);
	}

	@GetMapping(path = "/disconnect/{memberId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public void disconnect(@PathVariable("memberId") String memberId) {
		logger.debug("{} disconnected", memberId);
		notificationService.remove(memberId);
	}
}
