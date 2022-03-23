/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.webapp.service;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gov.sandia.cf.webapp.exception.CredibilityException;
import gov.sandia.cf.webapp.exception.LockException;
import gov.sandia.cf.webapp.model.entity.IntendedPurpose;
import gov.sandia.cf.webapp.model.stub.EntityLockInfo;
import gov.sandia.cf.webapp.repository.IntendedPurposeRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * The Class IntendedPurposeService.
 * 
 * @author Didier Verstraete
 */
@Slf4j
@Service
public class IntendedPurposeService implements IIntendedPurposeService {

	@Autowired
	private ILockService lockService;

	@Autowired
	private IntendedPurposeRepository intendedPurposeRepository;

	@Override
	public IntendedPurpose get(Long modelId) {
		IntendedPurpose intendedPurpose = intendedPurposeRepository.findByModelId(modelId);
		if (intendedPurpose == null) {
			throw new CredibilityException("The intended purpose has not been found for model: " + modelId);
		}

		return intendedPurpose;
	}

	@Override
	public void save(Long modelId, String token, IntendedPurpose intendedPurpose) {

		log.debug("Edit intended purpose" + intendedPurpose);

		// get the intendedPurpose locked from database (not the updated one coming)
		IntendedPurpose foundIntendedPurpose = get(modelId);

		// check is writable
		if (!lockService.isWritable(token, foundIntendedPurpose.getClass(), foundIntendedPurpose.getId())) {
			throw new LockException("The intended purpose is locked and can not be updated");
		}

		// update fields
		foundIntendedPurpose.setDescription(intendedPurpose.getDescription());
		foundIntendedPurpose.setReference(intendedPurpose.getReference());
		foundIntendedPurpose.setDateCreation(LocalDateTime.now());
		// TODO get the real user from request or session
		foundIntendedPurpose.setUserUpdate(intendedPurpose.getUserUpdate());

		// save
		intendedPurposeRepository.saveAndFlush(foundIntendedPurpose);

		// unlock after each save
		lockService.unlock(token, foundIntendedPurpose.getClass(), foundIntendedPurpose.getId());
	}

	@Override
	public EntityLockInfo getLockInfo(Long modelId) {
		IntendedPurpose foundIntendedPurpose = get(modelId);
		return lockService.getLockInfo(foundIntendedPurpose.getClass(), foundIntendedPurpose.getId());
	}

	@Override
	public String lock(Long modelId, String information) {
		IntendedPurpose foundIntendedPurpose = get(modelId);
		return lockService.lock(foundIntendedPurpose.getClass(), foundIntendedPurpose.getId(), information);
	}

	@Override
	public void unlock(Long modelId, String token) {
		IntendedPurpose foundIntendedPurpose = get(modelId);
		lockService.unlock(token, foundIntendedPurpose.getClass(), foundIntendedPurpose.getId());
	}
}
