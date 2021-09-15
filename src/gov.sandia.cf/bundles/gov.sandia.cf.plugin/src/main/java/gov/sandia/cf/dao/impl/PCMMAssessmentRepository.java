/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.AbstractCRUDRepository;
import gov.sandia.cf.dao.IPCMMAssessmentRepository;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMMode;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;

/**
 * PCMMAssessment entity repository
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessmentRepository extends AbstractCRUDRepository<PCMMAssessment, Integer>
		implements IPCMMAssessmentRepository {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAssessmentRepository.class);

	/**
	 * Query find all active by element in subelement
	 */
	public static final String QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_SUBELEMENT = "SELECT a FROM PCMMAssessment a WHERE a.subelement IS NOT NULL AND a.subelement.element = :{0} AND a.tag IS NULL"; //$NON-NLS-1$

	/**
	 * Query find by element and tag in subelement
	 */
	public static final String QUERY_FIND_BY_ELEMENT_IN_SUBELEMENT_AND_TAG = "SELECT a FROM PCMMAssessment a WHERE a.subelement IS NOT NULL AND a.subelement.element = :{0} AND a.tag = :{1}"; //$NON-NLS-1$

	/**
	 * Query find multiple assessments for user, role, tag and subelement
	 */
	public static final String QUERY_FIND_MULTIPLE_ASSESSMENTS_BY_SUBELEMENT = " SELECT" // Custom query //$NON-NLS-1$
			+ "   a.roleCreation," // 0- Role //$NON-NLS-1$
			+ "   a.userCreation," // 1- User //$NON-NLS-1$
			+ "   a.subelement," // 2- PCMMSubelement //$NON-NLS-1$
			+ "   t as tag," // 3- Tag //$NON-NLS-1$
			+ "   COUNT(a.id)" // 4- long //$NON-NLS-1$
			+ " FROM PCMMAssessment a LEFT JOIN a.tag t GROUP BY a.roleCreation, a.userCreation, a.subelement, t" //$NON-NLS-1$
			+ " HAVING COUNT(a.id) > 1"; //$NON-NLS-1$

	/**
	 * Query find multiple assessments for user, role, tag and element
	 */
	public static final String QUERY_FIND_MULTIPLE_ASSESSMENTS_BY_ELEMENT = " SELECT" // Custom query //$NON-NLS-1$
			+ "   a.roleCreation," // 0- Role //$NON-NLS-1$
			+ "   a.userCreation," // 1- User //$NON-NLS-1$
			+ "   a.element," // 2- PCMMElement //$NON-NLS-1$
			+ "   t as tag," // 3- Tag //$NON-NLS-1$
			+ "   COUNT(a.id)" // 4- long //$NON-NLS-1$
			+ " FROM PCMMAssessment a LEFT JOIN a.tag t GROUP BY a.roleCreation, a.userCreation, a.element, t" //$NON-NLS-1$
			+ " HAVING COUNT(a.id) > 1"; //$NON-NLS-1$

	/**
	 * empty constructor: if using, must call setEntityManager later
	 */
	public PCMMAssessmentRepository() {
		super(PCMMAssessment.class);
	}

	/**
	 * ModelRepository constructor
	 * 
	 * @param entityManager the entity manager for this repository to execute
	 *                      queries (must not be null)
	 */
	public PCMMAssessmentRepository(EntityManager entityManager) {
		super(entityManager, PCMMAssessment.class);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> findAllActive() {
		HashMap<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMAssessment.Filter.TAG, NullParameter.NULL);
		return findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> findByElementAndTagInSubelement(PCMMElement elt, Tag tag) {

		// check params
		if (elt == null) {
			logger.error("param elt is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		List<PCMMAssessment> returnedAssessments = null;

		if (tag == null) {
			String paramElt = "elt"; //$NON-NLS-1$
			TypedQuery<PCMMAssessment> query = getEntityManager().createQuery(
					MessageFormat.format(QUERY_FIND_ALL_ACTIVE_BY_ELEMENT_IN_SUBELEMENT, paramElt),
					PCMMAssessment.class);
			query.setParameter(paramElt, elt);
			returnedAssessments = query.getResultList();
		} else {
			String paramElt = "elt"; //$NON-NLS-1$
			String paramTag = "tag"; //$NON-NLS-1$
			TypedQuery<PCMMAssessment> query = getEntityManager().createQuery(
					MessageFormat.format(QUERY_FIND_BY_ELEMENT_IN_SUBELEMENT_AND_TAG, paramElt, paramTag),
					PCMMAssessment.class);
			query.setParameter(paramElt, elt);
			query.setParameter(paramTag, tag);
			returnedAssessments = query.getResultList();
		}

		return returnedAssessments;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> findByRoleAndUserAndEltAndTag(Role role, User user, PCMMElement elt, Tag tag) {

		// check params
		if (role == null) {
			logger.error("param role is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}
		if (user == null) {
			logger.error("param user is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}
		if (elt == null) {
			logger.error("param elt is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		HashMap<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMAssessment.Filter.TAG, tag);
		filters.put(PCMMAssessment.Filter.ROLECREATION, role);
		filters.put(PCMMAssessment.Filter.USERCREATION, user);
		filters.put(PCMMAssessment.Filter.ELEMENT, elt);

		return findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> findByRoleAndUserAndSubeltAndTag(Role role, User user, PCMMSubelement subelt, Tag tag) {

		// check params
		if (role == null) {
			logger.error("param role is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}
		if (user == null) {
			logger.error("param user is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}
		if (subelt == null) {
			logger.error("param subelt is empty or null"); //$NON-NLS-1$
			return new ArrayList<>();
		}

		HashMap<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMAssessment.Filter.TAG, tag);
		filters.put(PCMMAssessment.Filter.ROLECREATION, role);
		filters.put(PCMMAssessment.Filter.USERCREATION, user);
		filters.put(PCMMAssessment.Filter.SUBELEMENT, subelt);

		return findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> findByTag(Tag tag) {

		HashMap<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMAssessment.Filter.TAG, tag);

		return findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean clearMultipleAssessment(PCMMMode mode) {

		if (mode == null) {
			logger.error("param mode is empty or null"); //$NON-NLS-1$
			return false;
		}

		// Initialize
		String queryString;
		Query query;
		List<Object[]> resultList;
		boolean wasDirty = false;

		// Get duplicate data
		if (mode == PCMMMode.DEFAULT) {
			// Query String
			queryString = QUERY_FIND_MULTIPLE_ASSESSMENTS_BY_SUBELEMENT;

			// Query Object
			query = getEntityManager().createQuery(queryString);
			resultList = query.getResultList();

			// For each results
			wasDirty = !resultList.isEmpty();
			resultList.forEach(r -> {

				// Get data
				List<?> data = Arrays.asList(r);
				Role role = (Role) data.get(0);
				User user = (User) data.get(1);
				PCMMSubelement subelement = (PCMMSubelement) data.get(2);
				Tag tag = (Tag) data.get(3);

				// Clear Assessments
				clearAssessment(role, user, subelement, tag);
			});
		}
		else if (mode == PCMMMode.SIMPLIFIED) {
			// Query String
			queryString = QUERY_FIND_MULTIPLE_ASSESSMENTS_BY_ELEMENT;

			// Query Object
			query = getEntityManager().createQuery(queryString);
			resultList = query.getResultList();

			// For each results
			wasDirty = !resultList.isEmpty();
			resultList.forEach(r -> {
				// Get data
				List<?> data = Arrays.asList(r);
				Role role = (Role) data.get(0);
				User user = (User) data.get(1);
				PCMMElement element = (PCMMElement) data.get(2);
				Tag tag = (Tag) data.get(3);

				// Clear Assessments
				clearAssessment(role, user, element, tag);
			});
		}
		return wasDirty;
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearAssessment(Role role, User user, PCMMElement element, Tag tag) {
		// Get assessments to clear
		List<PCMMAssessment> assessments = findByRoleAndUserAndEltAndTag(role, user, element, tag);

		// Get the first assessment id
		Integer id = (!assessments.isEmpty()) ? assessments.get(0).getId() : -1;

		// Delete the other assessments
		for (PCMMAssessment assessment : assessments) {
			if (!id.equals(assessment.getId())) {
				delete(assessment);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearAssessment(Role role, User user, PCMMSubelement subelement, Tag tag) {
		// Get assessments to clear
		List<PCMMAssessment> assessments = findByRoleAndUserAndSubeltAndTag(role, user, subelement, tag);

		// Delete assessments
		Integer id = (!assessments.isEmpty()) ? assessments.get(0).getId() : -1;
		for (PCMMAssessment assessment : assessments) {
			if (!id.equals(assessment.getId())) {
				delete(assessment);
			}
		}
	}

}