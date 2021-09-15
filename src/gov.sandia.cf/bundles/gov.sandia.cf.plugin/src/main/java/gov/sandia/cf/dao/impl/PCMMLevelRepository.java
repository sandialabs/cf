/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.text.MessageFormat;
import java.util.List;

import javax.persistence.EntityManager;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMLevelRepository;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMSubelement;

/**
 * PCMMLevel entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMLevelRepository extends AbstractCRUDRepository<PCMMLevel, Integer> implements IPCMMLevelRepository {

	/**
	 * The find by element query
	 */
	private static final String FINDBY_ELEMENT_QUERY = "SELECT l FROM PCMMLevel l WHERE l.element = :{0}"; //$NON-NLS-1$

	/**
	 * The find by subelement query
	 */
	private static final String FINDBY_SUBELEMENT_QUERY = "SELECT l FROM PCMMLevel l WHERE l.subelement=:{0}"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PCMMLevelRepository() {
		super(PCMMLevel.class);
	}

	/**
	 * PCMMLevelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMLevelRepository(EntityManager entityManager) {
		super(entityManager, PCMMLevel.class);
	}

	/**
	 * Find the Level by PCMM Element
	 */
	@SuppressWarnings("unchecked")
	public List<PCMMLevel> findByPCMMElement(PCMMElement element) {
		String paramElt = "eltParam"; //$NON-NLS-1$
		return getEntityManager().createQuery(MessageFormat.format(FINDBY_ELEMENT_QUERY, paramElt))
				.setParameter(paramElt, element).getResultList();
	}

	/**
	 * Find the level by PCMM Subelement
	 */
	@SuppressWarnings("unchecked")
	public List<PCMMLevel> findByPCMMSubelement(PCMMSubelement subelement) {
		String paramSubelt = "subeltParam"; //$NON-NLS-1$
		return getEntityManager().createQuery(MessageFormat.format(FINDBY_SUBELEMENT_QUERY, paramSubelt))
				.setParameter(paramSubelt, subelement).getResultList();
	}
}