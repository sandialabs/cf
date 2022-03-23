/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.sandia.cf.webapp.exception.CredibilityException;
import gov.sandia.cf.webapp.model.entity.IntendedPurpose;
import gov.sandia.cf.webapp.model.entity.Model;
import gov.sandia.cf.webapp.repository.IntendedPurposeRepository;
import gov.sandia.cf.webapp.repository.ModelRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class ModelService.
 * 
 * @author Didier Verstraete
 */
@Slf4j
@Service
public class ModelService implements IModelService {

	@Autowired
	private ModelRepository modelRepository;

	@Autowired
	private IntendedPurposeRepository intendedPurposeRepository;

	@Override
	public Model get(Long modelId) {

		Optional<Model> model = modelRepository.findById(modelId);

		if (!model.isPresent()) {
			log.error("Model not found for id={}", modelId);
			throw new CredibilityException("Model not found");
		}

		return model.get();
	}

	@Override
	public List<Model> list() {
		return modelRepository.findAll();
	}

	@Override
	public Model create(Model model) {

		// TODO validate

		// create model
		Model modelCreated = modelRepository.saveAndFlush(model);

		// create intended purpose
		IntendedPurpose intendedPurpose = new IntendedPurpose();
		intendedPurpose.setModel(modelCreated);
		intendedPurpose.setDateCreation(LocalDateTime.now());
		intendedPurposeRepository.saveAndFlush(intendedPurpose);

		return modelCreated;
	}

	@Override
	public void save(Long modelId, String string, Model entity) {
		// TODO Auto-generated method stub

	}

	@Override
	public void delete(Long modelId) {
		Optional<Model> model = modelRepository.findById(modelId);

		if (!model.isPresent()) {
			log.error("Model not found for id={}", modelId);
			throw new CredibilityException("Model not found");
		}

		// delete intended purpose
		IntendedPurpose intendedPurpose = intendedPurposeRepository.findByModelId(modelId);
		if (intendedPurpose != null) {
			intendedPurposeRepository.delete(intendedPurpose);
		}

		// delete model
		modelRepository.delete(model.get());
		modelRepository.flush();
	}
}
