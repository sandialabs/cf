/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.ISystemRequirementApplication;
import gov.sandia.cf.application.configuration.requirement.SystemRequirementSpecification;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.dao.ISystemRequirementRepository;
import gov.sandia.cf.dao.ISystemRequirementSelectValueRepository;
import gov.sandia.cf.dao.ISystemRequirementValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage System Requirement Application methods
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementApplication extends AApplication implements ISystemRequirementApplication {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(SystemRequirementApplication.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemRequirementSpecification loadSysRequirementConfiguration(Model model) {
		// Initialize
		SystemRequirementSpecification specs = null;

		// Check if requirement is enabled
		if (isRequirementEnabled(model)) {

			// Create
			specs = new SystemRequirementSpecification();

			// Get the Requirement Parameters
			specs.setParameters(getParameterByModel(model));
		}

		return specs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemRequirement getRequirementById(Integer id) {
		SystemRequirementApplication.logger.debug("SystemRequirementApplication: getRequirementById"); //$NON-NLS-1$
		return this.getDaoManager().getRepository(ISystemRequirementRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<SystemRequirementParam> getParameterByModel(Model model) {
		SystemRequirementApplication.logger.debug("SystemRequirementApplication: getParameterByModel"); //$NON-NLS-1$
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(SystemRequirement.Filter.MODEL, model);
		return this.getDaoManager().getRepository(ISystemRequirementParamRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemRequirement addRequirement(SystemRequirement requirement, Model model, User userCreation)
			throws CredibilityException {
		if (requirement == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_NULL));
		} else if (existsRequirementStatement((Integer[]) null, requirement.getStatement())) {
			throw new CredibilityException(RscTools.getString(
					RscConst.EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_STATEMENTDUPLICATED, requirement.getStatement()));
		} else if (userCreation == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_USERNULL));
		} else if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_ADD_REQUIREMENTROW_MODELNULL));
		}

		// set date and user creation
		if (requirement.getValueList() != null) {
			for (SystemRequirementValue value : requirement.getValueList().stream()
					.filter(v -> v instanceof SystemRequirementValue).map(SystemRequirementValue.class::cast)
					.collect(Collectors.toList())) {
				if (value.getDateCreation() == null) {
					value.setDateCreation(DateTools.getCurrentDate());
				}
				if (value.getUserCreation() == null) {
					value.setUserCreation(userCreation);
				}
			}
		}

		// set requirement fields
		requirement.setModel(model);
		requirement.setCreationDate(DateTools.getCurrentDate());
		requirement.setUserCreation(userCreation);

		return getAppMgr().getDaoManager().getRepository(ISystemRequirementRepository.class).create(requirement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SystemRequirement updateRequirement(SystemRequirement requirement, User userUpdate)
			throws CredibilityException {

		if (requirement == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_NULL));
		} else if (requirement.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_IDNULL));
		} else if (userUpdate == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_UPDATE_REQUIREMENTROW_USERNULL));
		}

		// set date and user update
		if (requirement.getValueList() != null) {
			for (SystemRequirementValue value : requirement.getValueList().stream()
					.filter(v -> v instanceof SystemRequirementValue).map(SystemRequirementValue.class::cast)
					.collect(Collectors.toList())) {
				if (value.getDateUpdate() == null) {
					value.setDateUpdate(DateTools.getCurrentDate());
				}
				if (value.getUserUpdate() == null) {
					value.setUserUpdate(userUpdate);
				}
			}
		}

		return getAppMgr().getDaoManager().getRepository(ISystemRequirementRepository.class).update(requirement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean existsRequirementStatement(Integer[] id, String statement) throws CredibilityException {

		if (statement == null || statement.isEmpty()) {
			return false;
		}

		return !getDaoManager().getRepository(ISystemRequirementRepository.class)
				.isUniqueExcept(SystemRequirement.Filter.STATEMENT, id, statement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteRequirement(SystemRequirement requirement) throws CredibilityException {

		if (requirement == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTROW_NULL));
		} else if (requirement.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTROW_IDNULL));
		}

		// refresh children and values lists before deletion
		getAppMgr().getDaoManager().getRepository(ISystemRequirementRepository.class).refresh(requirement);

		// Remove Requirement - children and values associated will be automatically
		// deleted by cascade REMOVE
		getAppMgr().getDaoManager().getRepository(ISystemRequirementRepository.class).delete(requirement);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllRequirementValue(List<SystemRequirementValue> values) throws CredibilityException {
		if (values != null) {
			for (SystemRequirementValue value : values) {
				deleteRequirementValue(value);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteRequirementValue(SystemRequirementValue value) throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTVALUE_IDNULL));
		}

		getAppMgr().getDaoManager().getRepository(ISystemRequirementValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllRequirementParam(List<SystemRequirementParam> params) throws CredibilityException {
		if (params != null) {
			for (SystemRequirementParam param : params) {
				deleteRequirementParam(param);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteRequirementParam(SystemRequirementParam param) throws CredibilityException {

		if (param == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTPARAM_NULL));
		} else if (param.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTPARAM_IDNULL));
		}

		// retrieve the requirement values associated to this parameter
		Map<EntityFilter, Object> filtersValue = new HashMap<>();
		filtersValue.put(SystemRequirementValue.Filter.PARAMETER, param);
		deleteAllRequirementValue(
				getDaoManager().getRepository(ISystemRequirementValueRepository.class).findBy(filtersValue));

		// retrieve the requirement parameter select values
		Map<EntityFilter, Object> filtersSelectValues = new HashMap<>();
		filtersSelectValues.put(GenericParameterSelectValue.Filter.PARAMETER, param);
		deleteAllRequirementSelectValue(getDaoManager().getRepository(ISystemRequirementSelectValueRepository.class)
				.findBy(filtersSelectValues));

		getDaoManager().getRepository(ISystemRequirementParamRepository.class).delete(param);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllRequirementSelectValue(List<SystemRequirementSelectValue> selectValues)
			throws CredibilityException {
		if (selectValues != null) {
			for (SystemRequirementSelectValue select : selectValues) {
				deleteRequirementSelectValue(select);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteRequirementSelectValue(SystemRequirementSelectValue select) throws CredibilityException {

		if (select == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTSELECTVALUE_NULL));
		} else if (select.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTSELECTVALUE_IDNULL));
		}

		getDaoManager().getRepository(ISystemRequirementSelectValueRepository.class).delete(select);
	}

	@Override
	public List<SystemRequirement> getRequirementRootByModel(Model model) {
		return getDaoManager().getRepository(ISystemRequirementRepository.class).findRootRequirementsByModel(model);
	}

	@Override
	public List<SystemRequirement> getRequirementWithChildrenByModel(Model model) {

		// Initialize
		List<SystemRequirement> results = new ArrayList<>();
		List<SystemRequirement> roots = getRequirementRootByModel(model);

		// Add children
		if (roots != null) {
			results = roots.stream().map(root -> root.getChildrenTree(true)).flatMap(List::stream)
					.collect(Collectors.toList());
		}

		// Result
		return results;
	}

	@Override
	public void refresh(SystemRequirement requirement) {
		getDaoManager().getRepository(ISystemRequirementRepository.class).refresh(requirement);
	}

	@Override
	public boolean sameConfiguration(SystemRequirementSpecification spec1, SystemRequirementSpecification spec2) {

		if (spec1 == null) {
			return spec2 == null;
		} else if (spec2 == null) {
			return false;
		}

		// parameters
		return getAppMgr().getService(IImportApplication.class).sameListContent(spec1.getParameters(),
				spec2.getParameters());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRequirementEnabled(Model model) {
		// Initialize
		boolean isEnabled = true;

		// Has Parameter
		List<SystemRequirementParam> parameters = getParameterByModel(model);
		isEnabled &= parameters != null && !parameters.isEmpty();

		return isEnabled;
	}
}
