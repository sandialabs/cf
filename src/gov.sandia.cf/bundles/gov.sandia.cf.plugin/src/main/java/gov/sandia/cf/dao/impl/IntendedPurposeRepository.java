/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IIntendedPurposeRepository;
import gov.sandia.cf.model.IntendedPurpose;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * IntendedPurpose entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class IntendedPurposeRepository extends AbstractCRUDRepository<IntendedPurpose, Integer>
		implements IIntendedPurposeRepository {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(IntendedPurposeRepository.class);

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public IntendedPurposeRepository() {
		super(IntendedPurpose.class);
	}

	/**
	 * 
	 * IntendedPurposeRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public IntendedPurposeRepository(EntityManager entityManager) {
		super(entityManager, IntendedPurpose.class);
	}

	/** {@inheritDoc} */
	@Override
	public IntendedPurpose getFirst(Model model) {
		logger.debug("Calling getFirst() method"); //$NON-NLS-1$

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(IntendedPurpose.Filter.MODEL, model);
		List<IntendedPurpose> result = findBy(filters);
		return (result != null && !result.isEmpty()) ? result.get(0) : null;
	}
}