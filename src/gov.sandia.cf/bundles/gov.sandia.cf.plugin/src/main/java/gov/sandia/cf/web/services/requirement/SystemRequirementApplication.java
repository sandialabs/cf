/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.requirement;

import java.util.List;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.requirement.ISystemRequirementApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;

/**
 * Manage System Requirement Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class SystemRequirementApplication extends AApplication implements ISystemRequirementApplication {

	/**
	 * SystemRequirementApplication constructor
	 */
	public SystemRequirementApplication() {
		super();
	}

	/**
	 * SystemRequirementApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public SystemRequirementApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public SystemRequirementSpecification loadSysRequirementConfiguration(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public SystemRequirement getRequirementById(Integer id) {
		// TODO to implement
		return null;
	}

	@Override
	public boolean existsRequirementStatement(Integer[] id, String statement) throws CredibilityException {
		// TODO to implement
		return false;
	}

	@Override
	public SystemRequirement getRequirementByStatement(String statement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<SystemRequirementParam> getParameterByModel(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public SystemRequirement updateRequirement(SystemRequirement requirement, User userUpdate)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void deleteRequirement(SystemRequirement requirement) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllRequirementValue(List<SystemRequirementValue> values) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteRequirementValue(SystemRequirementValue value) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllRequirementParam(List<SystemRequirementParam> params) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteRequirementParam(SystemRequirementParam param) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteAllRequirementSelectValue(List<SystemRequirementSelectValue> selectValues)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void deleteRequirementSelectValue(SystemRequirementSelectValue select) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public List<SystemRequirement> getRequirementRootByModel(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public List<SystemRequirement> getRequirementWithChildrenByModel(Model model) {
		// TODO to implement
		return null;
	}

	@Override
	public SystemRequirement addRequirement(SystemRequirement requirement, Model model, User userCreation)
			throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public void refresh(SystemRequirement requirement) {
		// TODO to implement

	}

	@Override
	public boolean sameConfiguration(SystemRequirementSpecification spec1, SystemRequirementSpecification spec2) {
		// TODO to implement
		return false;
	}

	@Override
	public boolean isRequirementEnabled(Model model) {
		// TODO to implement
		return false;
	}

	@Override
	public List<SystemRequirement> getRequirementByModelAndParent(Model model, SystemRequirement parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reorderAll(Model model, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderSystemRequirement(SystemRequirement systemRequirement, int newIndex, User user)
			throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void reorderSystemRequirementAtSameLevel(SystemRequirement toMove, User user) throws CredibilityException {
		// TODO Auto-generated method stub

	}
}
