/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CFVariable;
import gov.sandia.cf.dao.IARGParametersRepository;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.dao.migration.MigrationTask;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Migration of ARG parameters path variable from ${workspace_dir} to
 * ${eclipse.workspace}.
 * 
 * If the value does not contain this variable, do nothing.
 * 
 * @author Didier Verstraete
 */
@MigrationTask(name = "0.6.0-iwfcf-443-argparamvariable-task10", id = 10)
public class Task_010_ARGParam_Variable implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_010_ARGParam_Variable.class);

	/**
	 * Types
	 */
	public static final String OLD_VAR_WORKSPACE = "${workspace_dir}"; //$NON-NLS-1$

	@Override
	public String getName() {
		return this.getClass().getAnnotation(MigrationTask.class).name();
	}

	@Override
	public boolean execute(IDaoManager daoManager) throws CredibilityMigrationException {

		logger.info("Starting migration Task: {}", getName()); //$NON-NLS-1$

		if (daoManager == null || daoManager.getEntityManager() == null) {
			throw new CredibilityMigrationException(RscTools.getString(RscConst.EX_MIGRATIONDAO_DAOMGR_NULL));
		}

		// get current model - if not found do nothing
		Model model = daoManager.getRepository(IModelRepository.class).getFirst();
		if (model == null) {
			return false;
		}

		boolean changed = false;

		List<ARGParameters> argParamList = daoManager.getRepository(IARGParametersRepository.class).findAll();

		if (argParamList != null) {
			for (ARGParameters param : argParamList) {
				changed |= migrateARGParam(param, daoManager);
			}
		}

		return changed;
	}

	/**
	 * @param param      the ARG param to migrate
	 * @param daoManager the dao manager
	 * @return true if the ARG parameters needed migration
	 * 
	 * @throws CredibilityMigrationException if an error occurs during migration
	 */
	private boolean migrateARGParam(ARGParameters param, IDaoManager daoManager) throws CredibilityMigrationException {

		if (param == null) {
			return false;
		}

		boolean changed = false;

		// parameters file
		if (param.getParametersFilePath() != null && param.getParametersFilePath().contains(OLD_VAR_WORKSPACE)) {
			String paramFilePath = param.getParametersFilePath().replace(OLD_VAR_WORKSPACE, CFVariable.WORKSPACE.get());
			param.setParametersFilePath(paramFilePath);
			changed = true;
		}

		// structure file
		if (param.getStructureFilePath() != null && param.getStructureFilePath().contains(OLD_VAR_WORKSPACE)) {
			String paramFilePath = param.getStructureFilePath().replace(OLD_VAR_WORKSPACE, CFVariable.WORKSPACE.get());
			param.setStructureFilePath(paramFilePath);
			changed = true;
		}

		// output folder
		if (param.getOutput() != null && param.getOutput().contains(OLD_VAR_WORKSPACE)) {
			String paramFilePath = param.getOutput().replace(OLD_VAR_WORKSPACE, CFVariable.WORKSPACE.get());
			param.setOutput(paramFilePath);
			changed = true;
		}

		if (changed) {
			try {
				daoManager.getRepository(IARGParametersRepository.class).update(param);
			} catch (CredibilityException e) {
				throw new CredibilityMigrationException(e);
			}
		}

		return changed;
	}
}
