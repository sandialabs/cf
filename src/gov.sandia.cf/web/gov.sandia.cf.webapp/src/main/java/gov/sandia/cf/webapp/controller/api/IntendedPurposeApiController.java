/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.controller.api;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sandia.cf.webapp.exception.LockException;
import gov.sandia.cf.webapp.mapper.IntendedPurposeMapper;
import gov.sandia.cf.webapp.model.dto.IntendedPurposeDto;
import gov.sandia.cf.webapp.model.stub.EntityLockInfo;
import gov.sandia.cf.webapp.service.IIntendedPurposeService;
import gov.sandia.cf.webapp.service.SseNotificationService;

/**
 * The Class IntendedPurposeApiController.
 * 
 * @author Didier Verstraete
 */
@RestController
@RequestMapping("api/model/{modelId}/purpose")
public class IntendedPurposeApiController {

	Logger logger = LoggerFactory.getLogger(IntendedPurposeApiController.class);

	@Autowired
	private SseNotificationService notificationService;

	@Autowired
	private IIntendedPurposeService intendedPurposeService;

	@Autowired
	private IntendedPurposeMapper intendedPurposeMapper;

	@Autowired
	private ObjectMapper mapToDtoMapper;

	@GetMapping(value = "/get")
	public IntendedPurposeDto get(@PathVariable("modelId") Long modelId) {
		return intendedPurposeMapper.toDto(intendedPurposeService.get(modelId));
	}

	@PutMapping(value = "/edit")
	public void edit(@PathVariable("modelId") Long modelId, @RequestBody Map<String, Object> map) {
		logger.debug("edit {}", map.get(ApiConstants.PURPOSE_INTENDEDPURPOSE_VAR));
		try {
			intendedPurposeService.save(modelId, (String) map.get(ApiConstants.LOCK_TOKEN_VAR),
					intendedPurposeMapper.toEntity(mapToDtoMapper.convertValue(
							map.get(ApiConstants.PURPOSE_INTENDEDPURPOSE_VAR), IntendedPurposeDto.class)));

			// send notifications to this model listeners
			notificationService.sendToModel(modelId, ApiConstants.PURPOSE_INTENDEDPURPOSE_VAR,
					intendedPurposeMapper.toDto(intendedPurposeService.get(modelId)));
			notificationService.sendToModel(modelId, ApiConstants.LOCK_LOCKINFO_VAR, "");

		} catch (LockException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), e);
		}
	}

	@PutMapping(value = "/lock")
	public String lock(@PathVariable("modelId") Long modelId, @RequestBody Map<String, Object> map) {

		String token = intendedPurposeService.lock(modelId, (String) map.get(ApiConstants.LOCK_INFO_VAR));

		// send notification to this model listeners
		notificationService.sendToModel(modelId, ApiConstants.LOCK_LOCKINFO_VAR,
				intendedPurposeService.getLockInfo(modelId));

		return token;
	}

	@GetMapping(value = "/lock/info")
	public EntityLockInfo getLockInfo(@PathVariable("modelId") Long modelId) {
		return intendedPurposeService.getLockInfo(modelId);
	}

	@PutMapping(value = "/unlock")
	public void unlock(@PathVariable("modelId") Long modelId, @RequestBody Map<String, Object> map) {
		intendedPurposeService.unlock(modelId, (String) map.get(ApiConstants.LOCK_TOKEN_VAR));

		// send notification to this model listeners
		notificationService.sendToModel(modelId, ApiConstants.LOCK_LOCKINFO_VAR, "");
	}

}
