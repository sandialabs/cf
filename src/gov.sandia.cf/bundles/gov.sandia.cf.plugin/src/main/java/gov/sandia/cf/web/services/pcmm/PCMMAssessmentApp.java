/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.pcmm;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pcmm.IPCMMAssessmentApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.PCMMAssessment;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.Tag;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;

/**
 * Manage PCMM Assessment Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessmentApp extends AApplication implements IPCMMAssessmentApp {
	/**
	 * The constructor
	 */
	public PCMMAssessmentApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public PCMMAssessmentApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public List<PCMMAssessment> getActiveAssessmentList() throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMAssessment> getAssessmentByRoleAndUserAndEltAndTag(Role role, User user, PCMMElement elt, Tag tag)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMAssessment> getAssessmentByRoleAndUserAndSubeltAndTag(Role role, User user, PCMMSubelement subelt,
			Tag tag) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMAssessment getAssessmentById(Integer id) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMAssessment> getAssessmentByElement(PCMMElement elt, Map<EntityFilter, Object> filters)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMAssessment> getAssessmentByElementInSubelement(PCMMElement elt, Tag tag)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMAssessment> getAssessmentBySubelement(PCMMSubelement subelt, Map<EntityFilter, Object> filters)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMAssessment> getAssessmentByTag(Tag tag) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<PCMMAssessment> getAssessmentByTag(List<Tag> tagList) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMAssessment addAssessment(PCMMAssessment assessment) throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PCMMAssessment updateAssessment(PCMMAssessment assessment, User userUpdate, Role roleUpdate)
			throws CredibilityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteAssessment(PCMMAssessment assessment) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteAssessment(List<PCMMAssessment> assessmentList) throws CredibilityException {
		// TODO Auto-generated method stub

	}

}
