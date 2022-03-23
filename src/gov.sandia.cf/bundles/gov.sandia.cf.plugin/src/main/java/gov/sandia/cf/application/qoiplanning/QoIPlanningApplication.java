/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.qoiplanning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.dao.IQoIPlanningConstraintRepository;
import gov.sandia.cf.dao.IQoIPlanningParamRepository;
import gov.sandia.cf.dao.IQoIPlanningSelectValueRepository;
import gov.sandia.cf.dao.IQoIPlanningValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.QoIPlanningValue;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Manage QoI Planning Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class QoIPlanningApplication extends AApplication implements IQoIPlanningApplication {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(QoIPlanningApplication.class);

	/**
	 * QoIPlanningApplication constructor
	 */
	public QoIPlanningApplication() {
		super();
	}

	/**
	 * QoIPlanningApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public QoIPlanningApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QoIPlanningSpecification loadQoIPlanningConfiguration(Model model) {
		// Initialize
		QoIPlanningSpecification specs = null;

		// Check if qoi planning is enabled
		if (isQoIPlanningEnabled(model)) {

			// Create
			specs = new QoIPlanningSpecification();

			// Get the QoI Planning Parameters
			specs.setParameters(getParameterByModel(model));
		}

		return specs;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QoIPlanningParam> getParameterByModel(Model model) {
		QoIPlanningApplication.logger.debug("QoIPlanningApplication: getParameterByModel"); //$NON-NLS-1$
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.MODEL, model);
		return this.getDaoManager().getRepository(IQoIPlanningParamRepository.class).findBy(filters);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public QoIPlanningValue createOrUpdateQoIPlanningValue(QoIPlanningValue value, User user)
			throws CredibilityException {

		QoIPlanningValue returnValue = null;

		if (value != null) {
			if (value.getId() == null) {
				value.setDateCreation(DateTools.getCurrentDate());
				value.setUserCreation(user);
				returnValue = getAppMgr().getDaoManager().getRepository(IQoIPlanningValueRepository.class)
						.create(value);
			} else {
				value.setDateUpdate(DateTools.getCurrentDate());
				value.setUserUpdate(user);
				returnValue = getAppMgr().getDaoManager().getRepository(IQoIPlanningValueRepository.class)
						.update(value);
			}
		}

		return returnValue;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllQoIPlanningValue(List<QoIPlanningValue> values) throws CredibilityException {
		if (values != null) {
			for (QoIPlanningValue value : values) {
				deleteQoIPlanningValue(value);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteQoIPlanningValue(QoIPlanningValue value) throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGVALUE_IDNULL));
		}

		getAppMgr().getDaoManager().getRepository(IQoIPlanningValueRepository.class).delete(value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllQoIPlanningParam(List<QoIPlanningParam> params) throws CredibilityException {
		if (params != null) {
			for (QoIPlanningParam param : params) {
				deleteQoIPlanningParam(param);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteQoIPlanningParam(QoIPlanningParam param) throws CredibilityException {

		if (param == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGPARAM_NULL));
		} else if (param.getId() == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGPARAM_IDNULL));
		}

		// retrieve the qoi planning values associated to this parameter
		Map<EntityFilter, Object> filtersValue = new HashMap<>();
		filtersValue.put(QoIPlanningValue.Filter.PARAMETER, param);
		deleteAllQoIPlanningValue(
				getDaoManager().getRepository(IQoIPlanningValueRepository.class).findBy(filtersValue));

		// retrieve the qoi planning parameter select values
		Map<EntityFilter, Object> filtersSelectValues = new HashMap<>();
		filtersSelectValues.put(GenericParameterSelectValue.Filter.PARAMETER, param);
		deleteAllQoIPlanningSelectValue(
				getDaoManager().getRepository(IQoIPlanningSelectValueRepository.class).findBy(filtersSelectValues));

		// retrieve the qoi planning parameter constraints
		Map<EntityFilter, Object> filtersConstraints = new HashMap<>();
		filtersConstraints.put(GenericParameterSelectValue.Filter.PARAMETER, param);
		deleteAllQoIPlanningConstraint(
				getDaoManager().getRepository(IQoIPlanningConstraintRepository.class).findBy(filtersConstraints));

		getDaoManager().getRepository(IQoIPlanningParamRepository.class).delete(param);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllQoIPlanningSelectValue(List<QoIPlanningSelectValue> selectValues) throws CredibilityException {
		if (selectValues != null) {
			for (QoIPlanningSelectValue select : selectValues) {
				deleteQoIPlanningSelectValue(select);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteQoIPlanningSelectValue(QoIPlanningSelectValue select) throws CredibilityException {

		if (select == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGSELECTVALUE_NULL));
		} else if (select.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGSELECTVALUE_IDNULL));
		}

		getDaoManager().getRepository(IQoIPlanningSelectValueRepository.class).delete(select);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteAllQoIPlanningConstraint(List<QoIPlanningConstraint> contraints) throws CredibilityException {
		if (contraints != null) {
			for (QoIPlanningConstraint select : contraints) {
				deleteQoIPlanningConstraint(select);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void deleteQoIPlanningConstraint(QoIPlanningConstraint select) throws CredibilityException {

		if (select == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGCONSTRAINT_NULL));
		} else if (select.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_QOIPLANNING_DELETE_QOIPLANNINGCONSTRAINT_IDNULL));
		}

		getDaoManager().getRepository(IQoIPlanningConstraintRepository.class).delete(select);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean sameConfiguration(QoIPlanningSpecification spec1, QoIPlanningSpecification spec2) {

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
	public boolean isQoIPlanningEnabled(Model model) {
		// Initialize
		boolean isEnabled = true;

		// Has Parameter
		List<QoIPlanningParam> parameters = getParameterByModel(model);
		isEnabled &= parameters != null && !parameters.isEmpty();

		return isEnabled;
	}
}
