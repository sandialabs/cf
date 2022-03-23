/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.pcmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.dao.IPCMMAssessmentRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage PCMM Assessment Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessmentApp extends AApplication implements IPCMMAssessmentApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PCMMAssessmentApp.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getActiveAssessmentList() throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findAllActive();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByElement(PCMMElement elt, Map<EntityFilter, Object> filters)
			throws CredibilityException {
		if (filters == null) {
			filters = new HashMap<>();
		}
		filters.put(PCMMAssessment.Filter.ELEMENT, elt);
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByElementInSubelement(PCMMElement elt, Tag tag)
			throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByElementAndTagInSubelement(elt, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByRoleAndUserAndEltAndTag(Role role, User user, PCMMElement elt, Tag tag)
			throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByRoleAndUserAndEltAndTag(role, user,
				elt, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByRoleAndUserAndSubeltAndTag(Role role, User user, PCMMSubelement subelt,
			Tag tag) throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByRoleAndUserAndSubeltAndTag(role,
				user, subelt, tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentBySubelement(PCMMSubelement subelt, Map<EntityFilter, Object> filters)
			throws CredibilityException {
		if (filters == null) {
			filters = new HashMap<>();
		}
		filters.put(PCMMAssessment.Filter.SUBELEMENT, subelt);
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByTag(Tag tag) throws CredibilityException {
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByTag(tag);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<PCMMAssessment> getAssessmentByTag(List<Tag> tagList) throws CredibilityException {

		List<PCMMAssessment> pcmmAssessmentList = new ArrayList<>();
		if (tagList != null) {
			for (Tag tag : tagList) {
				pcmmAssessmentList
						.addAll(getDaoManager().getRepository(IPCMMAssessmentRepository.class).findByTag(tag));
			}
		}

		return pcmmAssessmentList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMAssessment getAssessmentById(Integer id) throws CredibilityException {

		// check assessment before update
		if (id == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_GETASSESSTBYID_IDNULL));
		}

		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMAssessment addAssessment(PCMMAssessment assessment) throws CredibilityException {

		// check assessment before add
		if (assessment == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_ADDASSESSTBYID_ASSESSTNULL));
		}

		logger.debug("Adding assessment {}", assessment); //$NON-NLS-1$

		// set creation data
		assessment.setDateCreation(DateTools.getCurrentDate());

		// create
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).create(assessment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PCMMAssessment updateAssessment(PCMMAssessment assessment, User user, Role role)
			throws CredibilityException {

		// check assessment before update
		if (assessment == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_ASSESSTNULL));
		} else if (assessment.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_IDNULL));
		} else if (assessment.getUserCreation() != null && !assessment.getUserCreation().equals(user)) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_DIFFUSERNULL));
		} else if (assessment.getRoleCreation() != null && !assessment.getRoleCreation().equals(role)) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_UPDATEASSESSTBYID_DIFFROLENULL));
		}

		// set the update date
		assessment.setDateUpdate(DateTools.getCurrentDate());

		// update
		return getDaoManager().getRepository(IPCMMAssessmentRepository.class).update(assessment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAssessment(PCMMAssessment assessment) throws CredibilityException {

		// check assessment before update
		if (assessment == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEASSESSTBYID_ASSESSTNULL));
		} else if (assessment.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMM_DELETEASSESSTBYID_IDNULL));
		}

		// delete
		getDaoManager().getRepository(IPCMMAssessmentRepository.class).delete(assessment);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAssessment(List<PCMMAssessment> assessmentList) throws CredibilityException {
		if (assessmentList != null) {
			for (PCMMAssessment assessment : assessmentList) {
				deleteAssessment(assessment);
			}
		}
	}
}
