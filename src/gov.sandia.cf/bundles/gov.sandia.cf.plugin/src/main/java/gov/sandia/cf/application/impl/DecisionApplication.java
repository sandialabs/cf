/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.IApplicationManager;
import gov.sandia.cf.application.IDecisionApplication;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.configuration.decision.DecisionSpecification;
import gov.sandia.cf.dao.IDecisionConstraintRepository;
import gov.sandia.cf.dao.IDecisionParamRepository;
import gov.sandia.cf.dao.IDecisionRepository;
import gov.sandia.cf.dao.IDecisionSelectValueRepository;
import gov.sandia.cf.dao.IDecisionValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.Decision;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.DecisionValue;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage Decision Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionApplication extends AApplication implements IDecisionApplication {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(DecisionApplication.class);

	/**
	 * DecisionApplication constructor
	 */
	public DecisionApplication() {
		super();
	}

	/**
	 * DecisionApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public DecisionApplication(IApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DecisionSpecification loadDecisionConfiguration(Model model) {
		// Initialize
		DecisionSpecification specs = null;

		// Check if requirement is enabled
		if (isDecisionEnabled(model)) {

			// Create
			specs = new DecisionSpecification();

			// Get the Decision Parameters
			specs.setParameters(getParameterByModel(model));
		}

		return specs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Decision getDecisionById(Integer id) {
		DecisionApplication.logger.debug("DecisionApplication: getDecisionById"); //$NON-NLS-1$
		return this.getDaoManager().getRepository(IDecisionRepository.class).findById(id);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<DecisionParam> getParameterByModel(Model model) {
		DecisionApplication.logger.debug("DecisionApplication: getParameterByModel"); //$NON-NLS-1$
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(Decision.Filter.MODEL, model);
		return this.getDaoManager().getRepository(IDecisionParamRepository.class).findBy(filters);
	}

	@Override
	public List<Decision> getDecisionRootByModel(Model model) {
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(Decision.Filter.MODEL, model);
		filters.put(Decision.Filter.PARENT, null);
		return getDaoManager().getRepository(IDecisionRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Decision addDecision(Decision decision, Model model, User userCreation) throws CredibilityException {
		if (decision == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_ADD_DECISIONROW_NULL));
		} else if (existsDecisionTitle((Integer[]) null, decision.getTitle())) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_DECISION_ADD_DECISIONROW_TITLEDUPLICATED, decision.getTitle()));
		} else if (userCreation == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_ADD_DECISIONROW_USERNULL));
		} else if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_ADD_DECISIONROW_MODELNULL));
		}

		if (decision.getValueList() != null) {
			for (DecisionValue value : decision.getValueList().stream().filter(v -> v instanceof DecisionValue)
					.map(DecisionValue.class::cast).collect(Collectors.toList())) {
				if (value.getDateCreation() == null) {
					value.setDateCreation(DateTools.getCurrentDate());
				}
				if (value.getUserCreation() == null) {
					value.setUserCreation(userCreation);
				}
			}
		}

		// set decision fields
		decision.setModel(model);
		decision.setCreationDate(DateTools.getCurrentDate());
		decision.setUserCreation(userCreation);

		return getAppMgr().getDaoManager().getRepository(IDecisionRepository.class).create(decision);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Decision updateDecision(Decision decision, User userUpdate) throws CredibilityException {

		if (decision == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_UPDATE_DECISIONROW_NULL));
		} else if (decision.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_UPDATE_DECISIONROW_IDNULL));
		} else if (userUpdate == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_UPDATE_DECISIONROW_USERNULL));
		}

		if (decision.getValueList() != null) {
			for (DecisionValue value : decision.getValueList().stream().filter(v -> v instanceof DecisionValue)
					.map(DecisionValue.class::cast).collect(Collectors.toList())) {
				if (value.getDateUpdate() == null) {
					value.setDateUpdate(DateTools.getCurrentDate());
				}
				if (value.getUserUpdate() == null) {
					value.setUserUpdate(userUpdate);
				}
			}
		}

		return getAppMgr().getDaoManager().getRepository(IDecisionRepository.class).update(decision);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean existsDecisionTitle(Integer[] id, String title) throws CredibilityException {

		if (title == null || title.isEmpty()) {
			return false;
		}

		return !getDaoManager().getRepository(IDecisionRepository.class).isUniqueExcept(Decision.Filter.TITLE, id,
				title);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDecision(Decision decision) throws CredibilityException {

		if (decision == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONROW_NULL));
		} else if (decision.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONROW_IDNULL));
		}

		// Refresh Decision
		getAppMgr().getDaoManager().getRepository(IDecisionRepository.class).refresh(decision);

		// Remove Decision - children and values associated will be automatically
		// deleted by cascade REMOVE
		getAppMgr().getDaoManager().getRepository(IDecisionRepository.class).delete(decision);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllDecisionValue(List<DecisionValue> values) throws CredibilityException {
		if (values != null) {
			for (DecisionValue value : values) {
				deleteDecisionValue(value);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDecisionValue(DecisionValue value) throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONVALUE_IDNULL));
		}

		getAppMgr().getDaoManager().getRepository(IDecisionValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllDecisionParam(List<DecisionParam> params) throws CredibilityException {
		if (params != null) {
			for (DecisionParam param : params) {
				deleteDecisionParam(param);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDecisionParam(DecisionParam param) throws CredibilityException {

		if (param == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONPARAM_NULL));
		} else if (param.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_DECISIONPARAM_IDNULL));
		}

		// retrieve the requirement values associated to this parameter
		Map<EntityFilter, Object> filtersValue = new HashMap<>();
		filtersValue.put(DecisionValue.Filter.PARAMETER, param);
		deleteAllDecisionValue(getDaoManager().getRepository(IDecisionValueRepository.class).findBy(filtersValue));

		// retrieve the requirement parameter select values
		Map<EntityFilter, Object> filtersSelectValues = new HashMap<>();
		filtersSelectValues.put(GenericParameterSelectValue.Filter.PARAMETER, param);
		deleteAllDecisionSelectValue(
				getDaoManager().getRepository(IDecisionSelectValueRepository.class).findBy(filtersSelectValues));

		// retrieve the requirement parameter constraints
		Map<EntityFilter, Object> filtersConstraints = new HashMap<>();
		filtersConstraints.put(GenericParameterSelectValue.Filter.PARAMETER, param);
		deleteAllDecisionConstraint(
				getDaoManager().getRepository(IDecisionConstraintRepository.class).findBy(filtersConstraints));

		getDaoManager().getRepository(IDecisionParamRepository.class).delete(param);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllDecisionSelectValue(List<DecisionSelectValue> selectValues) throws CredibilityException {
		if (selectValues != null) {
			for (DecisionSelectValue select : selectValues) {
				deleteDecisionSelectValue(select);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDecisionSelectValue(DecisionSelectValue select) throws CredibilityException {

		if (select == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_SELECTVALUE_NULL));
		} else if (select.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_SELECTVALUE_IDNULL));
		}

		getDaoManager().getRepository(IDecisionSelectValueRepository.class).delete(select);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllDecisionConstraint(List<DecisionConstraint> contraints) throws CredibilityException {
		if (contraints != null) {
			for (DecisionConstraint select : contraints) {
				deleteDecisionConstraint(select);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteDecisionConstraint(DecisionConstraint select) throws CredibilityException {

		if (select == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_CONSTRAINT_NULL));
		} else if (select.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_DECISION_DELETE_CONSTRAINT_IDNULL));
		}

		getDaoManager().getRepository(IDecisionConstraintRepository.class).delete(select);
	}

	@Override
	public void refresh(Decision requirement) {
		getDaoManager().getRepository(IDecisionRepository.class).refresh(requirement);
	}

	@Override
	public boolean sameConfiguration(DecisionSpecification spec1, DecisionSpecification spec2) {

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
	public boolean isDecisionEnabled(Model model) {
		// Initialize
		boolean isEnabled = true;

		// Has Parameter
		List<DecisionParam> parameters = getParameterByModel(model);
		isEnabled &= parameters != null && !parameters.isEmpty();

		return isEnabled;
	}
}
