/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.text.MessageFormat;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.dao.migration.MigrationTask;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Migration of ARGParameters fields from VARCHAR to LONGVARCHAR
 * 
 * @author Didier Verstraete
 */
@MigrationTask(name = "0.6.0-iwfcf-409-argparam-longvarchar-task7", id = 7)
public class Task_007_ARGParametersLongVarcharFields implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_007_ARGParametersLongVarcharFields.class);

	private static final String QUERY_LONGVARCHAR = "ALTER TABLE {0} ALTER COLUMN {1} LONGVARCHAR;"; //$NON-NLS-1$

	@Override
	public String getName() {
		return this.getClass().getAnnotation(MigrationTask.class).name();
	}

	/**
	 * {@inheritDoc}
	 */
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

		// get the unit of work
		UnitOfWork unitOfWork = daoManager.getEntityManager().unwrap(UnitOfWork.class);

		try {
			// change ARG parameters fields to LONGVARCHAR
			String argParametersTable = ARGParameters.class.getAnnotation(javax.persistence.Table.class).name();
			String outputColumn = ARGParameters.class.getDeclaredField(ARGParameters.Filter.OUTPUT.getField())
					.getAnnotation(javax.persistence.Column.class).name();
			String paramPathColumn = ARGParameters.class
					.getDeclaredField(ARGParameters.Filter.PARAMETERS_FILE_PATH.getField())
					.getAnnotation(javax.persistence.Column.class).name();
			String structPathColumn = ARGParameters.class
					.getDeclaredField(ARGParameters.Filter.STRUCTURE_FILE_PATH.getField())
					.getAnnotation(javax.persistence.Column.class).name();

			// execute queries
			unitOfWork
					.executeNonSelectingSQL(MessageFormat.format(QUERY_LONGVARCHAR, argParametersTable, outputColumn));
			unitOfWork.executeNonSelectingSQL(
					MessageFormat.format(QUERY_LONGVARCHAR, argParametersTable, paramPathColumn));
			unitOfWork.executeNonSelectingSQL(
					MessageFormat.format(QUERY_LONGVARCHAR, argParametersTable, structPathColumn));

		} catch (NoSuchFieldException | SecurityException e) {
			logger.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

}
