/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.dao.ISystemRequirementRepository;
import gov.sandia.cf.dao.ISystemRequirementSelectValueRepository;
import gov.sandia.cf.dao.ISystemRequirementValueRepository;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.dao.migration.MigrationTask;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirement;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.SystemRequirementValue;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Migrate Statement values from System Requirement Value to a persistent field
 * within System Requirement:
 * 
 * 1. Migrate the values
 * 
 * 2. Remove Values
 * 
 * 3. Remove Parameters
 * 
 * @author Maxime N.
 */
@MigrationTask(name = "0.6.0-iwfcf-405-sysreq-statement-task6", id = 6)
public class Task_006_SystemRequirementStatementField implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_006_SystemRequirementStatementField.class);

	/**
	 * The statement query parameter
	 */
	public static final String PARAM_STATEMENT_VALUE = "Statement"; //$NON-NLS-1$

	private static final String QUERY_DELETE_DUPLICATE = "DELETE FROM COM_REQUIREMENT_VALUE " + //$NON-NLS-1$
			" WHERE ID IN (" + //$NON-NLS-1$
			" SELECT VAL1.ID FROM COM_REQUIREMENT_VALUE VAL1 " + //$NON-NLS-1$
			" LEFT JOIN  " + //$NON-NLS-1$
			" (SELECT REQUIREMENT_ID,PARAMETER_ID FROM PUBLIC.COM_REQUIREMENT_VALUE  " + //$NON-NLS-1$
			" GROUP BY REQUIREMENT_ID, PARAMETER_ID " + //$NON-NLS-1$
			" HAVING COUNT(PARAMETER_ID) > 1) DUP  " + //$NON-NLS-1$
			" ON DUP.REQUIREMENT_ID = VAL1.REQUIREMENT_ID AND DUP.PARAMETER_ID = VAL1.PARAMETER_ID  " + //$NON-NLS-1$
			" WHERE VAL1.ID NOT IN " + //$NON-NLS-1$
			" (SELECT ID FROM COM_REQUIREMENT_VALUE VAL2 " + //$NON-NLS-1$
			" WHERE VAL1.REQUIREMENT_ID = VAL2.REQUIREMENT_ID AND VAL1.PARAMETER_ID = VAL2.PARAMETER_ID " + //$NON-NLS-1$
			" ORDER BY VAL2.ID ASC LIMIT 1) " + //$NON-NLS-1$
			" );"; //$NON-NLS-1$

	private static final String QUERY_UPDATE_STATEMENT = "UPDATE COM_REQUIREMENT" //$NON-NLS-1$
			+ " SET COM_REQUIREMENT.STATEMENT = (" //$NON-NLS-1$
			+ " SELECT COM_REQUIREMENT_VALUE.VALUE" //$NON-NLS-1$
			+ " FROM COM_REQUIREMENT_VALUE" //$NON-NLS-1$
			+ " INNER JOIN COM_REQUIREMENT_PARAM ON COM_REQUIREMENT_VALUE.PARAMETER_ID = COM_REQUIREMENT_PARAM.ID" //$NON-NLS-1$
			+ " WHERE COM_REQUIREMENT_VALUE.REQUIREMENT_ID = COM_REQUIREMENT.ID" //$NON-NLS-1$
			+ " AND COM_REQUIREMENT_PARAM.NAME='" + PARAM_STATEMENT_VALUE + "'" //$NON-NLS-1$ //$NON-NLS-2$
			+ " ); "; //$NON-NLS-1$

	@Override
	public String getName() {
		return this.getClass().getAnnotation(MigrationTask.class).name();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean execute(IDaoManager daoManager) throws CredibilityMigrationException {

		logger.info("Starting migration Task: {}", getName()); //$NON-NLS-1$

		// Check DAO
		if (daoManager == null || daoManager.getEntityManager() == null) {
			throw new CredibilityMigrationException(RscTools.getString(RscConst.EX_MIGRATIONDAO_DAOMGR_NULL));
		}

		// Check Model
		Model model = daoManager.getRepository(IModelRepository.class).getFirst();
		if (model == null) {
			return false;
		}

		// Get all parameters with "Statement" value
		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericParameter.Filter.NAME, PARAM_STATEMENT_VALUE);
		List<SystemRequirementParam> parameters = daoManager.getRepository((ISystemRequirementParamRepository.class))
				.findBy(filters);

		// check if change is needed
		if (parameters != null && !parameters.isEmpty()) {

			// Get the unit of work
			UnitOfWork unitOfWork = daoManager.getEntityManager().unwrap(UnitOfWork.class);

			// Delete duplicate of requirement values
			unitOfWork.executeNonSelectingSQL(QUERY_DELETE_DUPLICATE);

			// Update all SystemRequirements
			unitOfWork.executeNonSelectingSQL(QUERY_UPDATE_STATEMENT);

			// refresh statement
			List<SystemRequirement> findAllSystemRequirement = daoManager
					.getRepository(ISystemRequirementRepository.class).findAll();
			if (findAllSystemRequirement != null && !findAllSystemRequirement.isEmpty()) {
				findAllSystemRequirement.stream().forEach(
						entity -> daoManager.getRepository(ISystemRequirementRepository.class).refresh(entity));
			}

			// Remove all parameters with "Statement" value
			try {
				deleteAllRequirementParam(daoManager, parameters);
			} catch (CredibilityException e) {
				throw new CredibilityMigrationException(e.getMessage(), e);
			}
			return true;
		}

		return false;
	}

	/**
	 * Delete SystemRequirementParam list
	 * 
	 * @param daoManager the dao manager
	 * @param params     the requirement parameters
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteAllRequirementParam(IDaoManager daoManager, List<SystemRequirementParam> params)
			throws CredibilityException {
		if (params != null) {
			for (SystemRequirementParam param : params) {
				deleteRequirementParam(daoManager, param);
			}
		}
	}

	/**
	 * Delete a SystemRequirementParam
	 * 
	 * @param daoManager the dao manager
	 * @param param      the requirement parameter
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteRequirementParam(IDaoManager daoManager, SystemRequirementParam param)
			throws CredibilityException {

		if (param == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTPARAM_NULL));
		} else if (param.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTPARAM_IDNULL));
		}

		// retrieve the requirement values associated to this parameter
		Map<EntityFilter, Object> filtersValue = new HashMap<>();
		filtersValue.put(SystemRequirementValue.Filter.PARAMETER, param);
		deleteAllRequirementValue(daoManager,
				daoManager.getRepository(ISystemRequirementValueRepository.class).findBy(filtersValue));

		// retrieve the requirement parameter select values
		Map<EntityFilter, Object> filtersSelectValues = new HashMap<>();
		filtersSelectValues.put(GenericParameterSelectValue.Filter.PARAMETER, param);
		deleteAllRequirementSelectValue(daoManager,
				daoManager.getRepository(ISystemRequirementSelectValueRepository.class).findBy(filtersSelectValues));

		daoManager.getRepository(ISystemRequirementParamRepository.class).delete(param);
	}

	/**
	 * Delete SystemRequirementValue list
	 * 
	 * @param daoManager the dao manager
	 * @param values     the requirement values
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteAllRequirementValue(IDaoManager daoManager, List<SystemRequirementValue> values)
			throws CredibilityException {
		if (values != null) {
			for (SystemRequirementValue value : values) {
				deleteRequirementValue(daoManager, value);
			}
		}
	}

	/**
	 * Delete a SystemRequirementValue
	 * 
	 * @param daoManager the dao manager
	 * @param value      the requirement value
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteRequirementValue(IDaoManager daoManager, SystemRequirementValue value)
			throws CredibilityException {

		if (value == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTVALUE_NULL));
		} else if (value.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTVALUE_IDNULL));
		}

		daoManager.getRepository(ISystemRequirementValueRepository.class).delete(value);
	}

	/**
	 * Delete SystemRequirementSelectValue list
	 * 
	 * @param daoManager   the dao manager
	 * @param selectValues the requirement select values
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteAllRequirementSelectValue(IDaoManager daoManager, List<SystemRequirementSelectValue> selectValues)
			throws CredibilityException {
		if (selectValues != null) {
			for (SystemRequirementSelectValue select : selectValues) {
				deleteRequirementSelectValue(daoManager, select);
			}
		}
	}

	/**
	 * Delete a SystemRequirementSelectValue
	 * 
	 * @param daoManager the dao manager
	 * @param select     the requirement select value
	 * @throws CredibilityException if an error occurs during deletion
	 */
	public void deleteRequirementSelectValue(IDaoManager daoManager, SystemRequirementSelectValue select)
			throws CredibilityException {

		if (select == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTSELECTVALUE_NULL));
		} else if (select.getId() == null) {
			throw new CredibilityException(
					RscTools.getString(RscConst.EX_SYSREQUIREMENT_DELETE_REQUIREMENTSELECTVALUE_IDNULL));
		}

		daoManager.getRepository(ISystemRequirementSelectValueRepository.class).delete(select);
	}

}
