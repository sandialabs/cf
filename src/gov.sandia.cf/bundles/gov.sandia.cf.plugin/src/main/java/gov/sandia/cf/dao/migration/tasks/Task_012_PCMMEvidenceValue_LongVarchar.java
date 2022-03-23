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
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Migration of PCMMEvidence value from VARCHAR to LONGVARCHAR
 * 
 * @author Didier Verstraete
 */
@MigrationTask(name = "1.0.1-iwfcf-533-pcmm-evidence-longvarchar-task12", id = 12)
public class Task_012_PCMMEvidenceValue_LongVarchar implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_012_PCMMEvidenceValue_LongVarchar.class);

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
			String pcmmEvidenceTable = PCMMEvidence.class.getAnnotation(javax.persistence.Table.class).name();
			String valueColumn = PCMMEvidence.class.getDeclaredField(PCMMEvidence.Filter.VALUE.getField())
					.getAnnotation(javax.persistence.Column.class).name();

			// execute query
			unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_LONGVARCHAR, pcmmEvidenceTable, valueColumn));

		} catch (NoSuchFieldException | SecurityException e) {
			logger.error(e.getMessage(), e);
			return false;
		}

		return true;
	}

}
