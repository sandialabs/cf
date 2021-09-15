/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.sessions.UnitOfWork;

import gov.sandia.cf.dao.DaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.migration.EclipseLinkMigrationManager;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import the generic parameters "isRequired" to "required" field changes.
 * 
 * Change the field isRequired (boolean) to a conditional field Required
 * (String) in all the generic parameter tables available.
 * 
 * The model to apply the migration return true if the database needed and has
 * been updated, otherwise false.
 * 
 * @author Didier Verstraete
 *
 */
public class Task_004_GenericParameterRequired implements IMigrationTask {

	private static final Map<Class<? extends GenericParameter<?>>, String> PARAM_TABLE;
	static {
		PARAM_TABLE = new HashMap<>();
		PARAM_TABLE.put(PCMMPlanningParam.class, "PCMM_PLANNING_PARAM"); //$NON-NLS-1$
		PARAM_TABLE.put(PCMMPlanningQuestion.class, "PCMM_PLANNING_QUESTION"); //$NON-NLS-1$
		PARAM_TABLE.put(QoIPlanningParam.class, "QOI_PLANNING_PARAM"); //$NON-NLS-1$
		PARAM_TABLE.put(SystemRequirementParam.class, "COM_REQUIREMENT_PARAM"); //$NON-NLS-1$
		PARAM_TABLE.put(UncertaintyParam.class, "COM_UNCERTAINTY_PARAM"); //$NON-NLS-1$
	}

	private static final String TASK_NAME = "0.6.0-iwfcf-378-genericparamrequired-task"; //$NON-NLS-1$

	private static final String QUERY_DROP_FIELD = "ALTER TABLE {0} DROP COLUMN IS_REQUIRED;"; //$NON-NLS-1$
	private static final String QUERY_UPDATE_FIELD_OPTIONAL = "UPDATE {0} SET REQUIRED=''false'' WHERE IS_REQUIRED=false;"; //$NON-NLS-1$
	private static final String QUERY_UPDATE_FIELD_REQUIRED = "UPDATE {0} SET REQUIRED=''true'' WHERE IS_REQUIRED=true;"; //$NON-NLS-1$

	@Override
	public String getName() {
		return TASK_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean execute(DaoManager daoManager) throws CredibilityMigrationException {

		if (daoManager == null || daoManager.getEntityManager() == null) {
			throw new CredibilityMigrationException(RscTools.getString(RscConst.EX_MIGRATIONDAO_DAOMGR_NULL));
		}

		// get current model - if not found do nothing
		Model model = daoManager.getRepository(IModelRepository.class).getFirst();
		if (model == null) {
			return false;
		}

		boolean changed = false;

		// get the unit of work
		UnitOfWork unitOfWork = daoManager.getEntityManager().unwrap(UnitOfWork.class);

		// change Generic Parameter tables isRequired to required
		changed |= changeGenericParamTableIsRequired(PCMMPlanningParam.class, unitOfWork);
		changed |= changeGenericParamTableIsRequired(PCMMPlanningQuestion.class, unitOfWork);
		changed |= changeGenericParamTableIsRequired(QoIPlanningParam.class, unitOfWork);
		changed |= changeGenericParamTableIsRequired(SystemRequirementParam.class, unitOfWork);
		changed |= changeGenericParamTableIsRequired(UncertaintyParam.class, unitOfWork);

		return changed;
	}

	/**
	 * @param genericParamClass the CF feature
	 * @param unitOfWork        the unitOfWork to query the database
	 * @return true if the database needed changes, otherwise false.
	 */
	private boolean changeGenericParamTableIsRequired(Class<? extends GenericParameter<?>> genericParamClass,
			UnitOfWork unitOfWork) {

		// check table and column existence
		if (!EclipseLinkMigrationManager.existsColumnInTable(unitOfWork, PARAM_TABLE.get(genericParamClass),
				"IS_REQUIRED")) { //$NON-NLS-1$
			return false;
		}

		// update tables
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(QUERY_UPDATE_FIELD_OPTIONAL, PARAM_TABLE.get(genericParamClass)));
		unitOfWork.executeNonSelectingSQL(
				MessageFormat.format(QUERY_UPDATE_FIELD_REQUIRED, PARAM_TABLE.get(genericParamClass)));

		// drop column in table
		unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_FIELD, PARAM_TABLE.get(genericParamClass)));

		return true;
	}

}
