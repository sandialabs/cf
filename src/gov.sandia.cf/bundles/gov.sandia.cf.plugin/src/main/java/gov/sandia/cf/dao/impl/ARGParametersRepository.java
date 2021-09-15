/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.util.List;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IARGParametersRepository;
import gov.sandia.cf.model.ARGParameters;

/**
 * ARG Parameters entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class ARGParametersRepository extends AbstractCRUDRepository<ARGParameters, Integer>
		implements IARGParametersRepository {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ARGParametersRepository.class);

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public ARGParametersRepository() {
		super(ARGParameters.class);
	}

	/**
	 * ARGParametersRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 * 
	 */
	public ARGParametersRepository(EntityManager entityManager) {
		super(entityManager, ARGParameters.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ARGParameters getFirst() {
		logger.debug("Calling getFirst() method"); //$NON-NLS-1$

		List<ARGParameters> result = findAll();
		return (result != null && !result.isEmpty()) ? result.get(0) : null;
	}

}