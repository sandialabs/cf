/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.controller.api;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.databind.ObjectMapper;

import gov.sandia.cf.webapp.exception.LockException;
import gov.sandia.cf.webapp.mapper.ModelMapper;
import gov.sandia.cf.webapp.model.dto.ModelDto;
import gov.sandia.cf.webapp.model.entity.Model;
import gov.sandia.cf.webapp.service.IModelService;

/**
 * The Class ModelApiController.
 * 
 * @author Didier Verstraete
 */
@RestController
@RequestMapping("api/model")
public class ModelApiController {

	Logger logger = LoggerFactory.getLogger(ModelApiController.class);

	@Autowired
	private IModelService modelService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private ObjectMapper mapToDtoMapper;

	@GetMapping(value = "/{modelId}/get")
	public ModelDto get(@PathVariable("modelId") Long modelId) {
		return modelMapper.toDto(modelService.get(modelId));
	}

	@GetMapping(value = "/list")
	public List<ModelDto> get() {
		return modelMapper.toDto(modelService.list());
	}

	@PostMapping(value = "/new")
	public ModelDto create(@RequestBody Map<String, Object> map) {
		Model model = modelService.create(modelMapper
				.toEntity(mapToDtoMapper.convertValue(map.get(ApiConstants.MODEL_MODEL_VAR), ModelDto.class)));
		return modelMapper.toDto(model);
	}

	@PutMapping(value = "/{modelId}/edit")
	public void edit(@PathVariable("modelId") Long modelId, @RequestBody Map<String, Object> map) {
		logger.debug("edit {}", map.get("modelId"));
		try {
			modelService.save(modelId, (String) map.get(ApiConstants.LOCK_TOKEN_VAR), modelMapper
					.toEntity(mapToDtoMapper.convertValue(map.get(ApiConstants.MODEL_MODEL_VAR), ModelDto.class)));
		} catch (LockException e) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage(), e);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.METHOD_NOT_ALLOWED, e.getMessage(), e);
		}
	}

	@DeleteMapping(value = "/{modelId}/delete")
	public void delete(@PathVariable("modelId") Long modelId) {
		modelService.delete(modelId);
	}

}
