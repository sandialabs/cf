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
import gov.sandia.cf.application.IUncertaintyApplication;
import gov.sandia.cf.application.configuration.uncertainty.UncertaintySpecification;
import gov.sandia.cf.dao.IUncertaintyGroupRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintyRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.dao.IUncertaintyValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage Uncertainty Application methods
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyApplication extends AApplication implements IUncertaintyApplication {
	/**
	 * The logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(UncertaintyApplication.class);

	/**
	 * UncertaintyApplication constructor
	 */
	public UncertaintyApplication() {
		super();
	}

	/**
	 * UncertaintyApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public UncertaintyApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UncertaintySpecification loadUncertaintyConfiguration(Model model) {
		// Initialize
		UncertaintySpecification specs = null;

		// Check if uncertainty is enabled
		if (isUncertaintyEnabled(model)) {
			specs = new UncertaintySpecification();

			// get the Uncertainty Parameters
			specs.setParameters(getUncertaintyParameterByModel(model));
		}

		return specs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameConfiguration(UncertaintySpecification spec1, UncertaintySpecification spec2) {

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
	public List<UncertaintyGroup> getUncertaintyGroupByModel(Model model) {
		// Logger
		UncertaintyApplication.logger.debug("UncertaintyApplication: getUncertaintyGroupByModel"); //$NON-NLS-1$

		// Filter
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(UncertaintyGroup.Filter.MODEL, model);

		// Query
		return this.getDaoManager().getRepository(IUncertaintyGroupRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UncertaintyGroup getUncertaintyGroupById(Integer id) {
		UncertaintyApplication.logger.debug("UncertaintyApplication: getUncertaintyGroupById"); //$NON-NLS-1$
		return this.getDaoManager().getRepository(IUncertaintyGroupRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uncertainty getUncertaintyById(Integer id) {
		UncertaintyApplication.logger.debug("UncertaintyApplication: getUncertaintyById"); //$NON-NLS-1$
		return this.getDaoManager().getRepository(IUncertaintyRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UncertaintyParam> getUncertaintyParameterByModel(Model model) {
		UncertaintyApplication.logger.debug("UncertaintyApplication: getParameterByModel"); //$NON-NLS-1$
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(UncertaintyGroup.Filter.MODEL, model);
		return this.getDaoManager().getRepository(IUncertaintyParamRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UncertaintyGroup addUncertaintyGroup(UncertaintyGroup uncertaintyGroup, Model model, User userCreation)
			throws CredibilityException {
		if (uncertaintyGroup == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYGROUP_NULL));
		} else if (userCreation == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYGROUP_USERNULL));
		} else if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYGROUP_MODELNULL));
		}

		uncertaintyGroup.setModel(model);
		uncertaintyGroup.setUserCreation(userCreation);
		return getAppMgr().getDaoManager().getRepository(IUncertaintyGroupRepository.class).create(uncertaintyGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uncertainty addUncertainty(Uncertainty uncertainty, User userCreation) throws CredibilityException {

		if (uncertainty == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYROW_NULL));
		} else if (userCreation == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_ADD_UNCERTAINTYROW_USERNULL));
		}

		// Get list
		List<UncertaintyValue> uncertaintyParameters = uncertainty.getUncertaintyParameterList();
		uncertainty.setUncertaintyParameterList(new ArrayList<>());

		// Create
		uncertainty.setUserCreation(userCreation);
		Uncertainty uncertaintyCreated = getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class)
				.create(uncertainty);

		if (uncertaintyParameters != null) {
			for (UncertaintyValue uncertaintyValue : uncertaintyParameters) {

				// set values
				uncertaintyValue.setUncertainty(uncertaintyCreated);
				uncertaintyValue.setDateCreation(DateTools.getCurrentDate());
				uncertaintyValue.setUserCreation(userCreation);

				// create value
				UncertaintyValue createdUncertaintyValue = getAppMgr().getDaoManager()
						.getRepository(IUncertaintyValueRepository.class).create(uncertaintyValue);
				uncertainty.getUncertaintyParameterList().add(createdUncertaintyValue);
			}
		}

		return uncertaintyCreated;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UncertaintyGroup updateUncertaintyGroup(UncertaintyGroup uncertaintyGroup) throws CredibilityException {

		if (uncertaintyGroup == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYGROUP_NULL));
		} else if (uncertaintyGroup.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYGROUP_IDNULL));
		}

		return getAppMgr().getDaoManager().getRepository(IUncertaintyGroupRepository.class).update(uncertaintyGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Uncertainty updateUncertainty(Uncertainty uncertainty, User userUpdate) throws CredibilityException {

		if (uncertainty == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_NULL));
		} else if (uncertainty.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_IDNULL));
		} else if (userUpdate == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_UPDATE_UNCERTAINTYROW_USERNULL));
		}

		if (uncertainty.getValueList() != null) {
			for (UncertaintyValue value : uncertainty.getValueList().stream().filter(UncertaintyValue.class::isInstance)
					.map(UncertaintyValue.class::cast).collect(Collectors.toList())) {
				if (value.getDateUpdate() == null) {
					value.setDateUpdate(DateTools.getCurrentDate());
				}
				if (value.getUserUpdate() == null) {
					value.setUserUpdate(userUpdate);
				}
			}
		}

		return getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class).update(uncertainty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertaintyGroup(UncertaintyGroup uncertaintyGroup) throws CredibilityException {

		if (uncertaintyGroup == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYGROUP_NULL));
		} else if (uncertaintyGroup.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYGROUP_IDNULL));
		}

		// refresh uncertainties before deletion
		refresh(uncertaintyGroup);

		// Remove group - uncertainties associated will be automatically
		// deleted by cascade REMOVE
		getAppMgr().getDaoManager().getRepository(IUncertaintyGroupRepository.class).delete(uncertaintyGroup);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertainty(Uncertainty uncertainty) throws CredibilityException {

		if (uncertainty == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYROW_NULL));
		} else if (uncertainty.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYROW_IDNULL));
		}

		// refresh Uncertainty
		getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class).refresh(uncertainty);

		// Remove Uncertainty - values associated will be automatically
		// deleted by cascade REMOVE
		getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class).delete(uncertainty);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllUncertaintyValue(List<UncertaintyValue> values) throws CredibilityException {
		if (values != null) {
			for (UncertaintyValue value : values) {
				deleteUncertaintyValue(value);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertaintyValue(UncertaintyValue value) throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYVALUE_IDNULL));
		}

		getAppMgr().getDaoManager().getRepository(IUncertaintyValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllUncertaintyParam(List<UncertaintyParam> params) throws CredibilityException {
		if (params != null) {
			for (UncertaintyParam param : params) {
				deleteUncertaintyParam(param);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertaintyParam(UncertaintyParam param) throws CredibilityException {

		if (param == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYPARAM_NULL));
		} else if (param.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYPARAM_IDNULL));
		}

		// retrieve the uncertainty values associated to this parameter
		Map<EntityFilter, Object> filtersValue = new HashMap<>();
		filtersValue.put(UncertaintyValue.Filter.PARAMETER, param);
		deleteAllUncertaintyValue(
				getDaoManager().getRepository(IUncertaintyValueRepository.class).findBy(filtersValue));

		// retrieve the uncertainty parameter select values
		Map<EntityFilter, Object> filtersSelectValues = new HashMap<>();
		filtersSelectValues.put(GenericParameterSelectValue.Filter.PARAMETER, param);
		deleteAllUncertaintySelectValue(
				getDaoManager().getRepository(IUncertaintySelectValueRepository.class).findBy(filtersSelectValues));

		getDaoManager().getRepository(IUncertaintyParamRepository.class).delete(param);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllUncertaintySelectValue(List<UncertaintySelectValue> selectValues) throws CredibilityException {
		if (selectValues != null) {
			for (UncertaintySelectValue select : selectValues) {
				deleteUncertaintySelectValue(select);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteUncertaintySelectValue(UncertaintySelectValue select) throws CredibilityException {

		if (select == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYSELECTVALUE_NULL));
		} else if (select.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_UNCERTAINTY_DELETE_UNCERTAINTYSELECTVALUE_IDNULL));
		}

		getDaoManager().getRepository(IUncertaintySelectValueRepository.class).delete(select);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isUncertaintyEnabled(Model model) {
		// Initialize
		boolean isEnabled = true;

		// Has Parameter
		List<UncertaintyParam> parameters = getUncertaintyParameterByModel(model);
		isEnabled &= parameters != null && !parameters.isEmpty();

		// The result
		return isEnabled;
	}

	@Override
	public void refresh(UncertaintyGroup group) {
		getDaoManager().getRepository(IUncertaintyGroupRepository.class).refresh(group);
	}

	@Override
	public void refresh(Uncertainty uncertainty) {
		getDaoManager().getRepository(IUncertaintyRepository.class).refresh(uncertainty);
	}
}
