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
import gov.sandia.cf.dao.migration.EclipseLinkMigrationManager;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.dao.migration.MigrationTask;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Migration of evidence values to Gson format including evidence type.
 * 
 * This is to harmonize the storage of links which can be URL or FILE PATH. The
 * Gson format contains the link value and the link type.
 * 
 * See class @see gov.sandia.cf.application.configuration.ParameterLinkGson
 * 
 * @author Didier Verstraete
 */
@MigrationTask(name = "0.6.0-iwfcf-425-evidenceValuesToGson-task9", id = 9)
public class Task_009_EvidenceValueToGson implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_009_EvidenceValueToGson.class);

	/**
	 * Types
	 */
	/** Evidence Type FILE */
	public static final String TYPE_FILE = "file"; //$NON-NLS-1$
	/** Evidence Type URL */
	public static final String TYPE_URL = "url"; //$NON-NLS-1$

	/** Evidence TABLE */
	public static final String EVIDENCE_TABLE = "PCMMEVIDENCE"; //$NON-NLS-1$
	/** Evidence Column TYPE */
	public static final String EVIDENCE_TYPE_COLUMN = "TYPE"; //$NON-NLS-1$
	/** Evidence Column PATH */
	public static final String EVIDENCE_PATH_COLUMN = "PATH"; //$NON-NLS-1$
	/** Evidence Column VALUE */
	public static final String EVIDENCE_VALUE_COLUMN = "VALUE"; //$NON-NLS-1$

	private static final String GSON_FILE_VALUE = "'{\"type\":\"LINK_FILE\",\"value\":\"' + " + EVIDENCE_PATH_COLUMN //$NON-NLS-1$
			+ " + '\"}'"; //$NON-NLS-1$
	private static final String GSON_URL_VALUE = "'{\"type\":\"LINK_URL\",\"value\":\"' + " + EVIDENCE_PATH_COLUMN //$NON-NLS-1$
			+ " + '\"}'"; //$NON-NLS-1$

	private static final String QUERY_UPDATE_EVIDENCE_VALUE_BY_TYPE = "UPDATE " + EVIDENCE_TABLE + " SET " //$NON-NLS-1$ //$NON-NLS-2$
			+ EVIDENCE_VALUE_COLUMN + "={0} WHERE " + EVIDENCE_TYPE_COLUMN + "=''{1}''"; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String QUERY_UPDATE_EVIDENCE_VALUE_WITHOUT_TYPE = "UPDATE " + EVIDENCE_TABLE + " SET " //$NON-NLS-1$ //$NON-NLS-2$
			+ EVIDENCE_VALUE_COLUMN + "={0}"; //$NON-NLS-1$

	private static final String QUERY_DROP_FIELD = "ALTER TABLE " + EVIDENCE_TABLE + " DROP COLUMN {0};"; //$NON-NLS-1$ //$NON-NLS-2$

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

		// get the unit of work
		UnitOfWork unitOfWork = daoManager.getEntityManager().unwrap(UnitOfWork.class);

		// check old columns existence
		boolean existsTypeInEvidenceTable = EclipseLinkMigrationManager.existsColumnInTable(unitOfWork, EVIDENCE_TABLE,
				EVIDENCE_TYPE_COLUMN);
		boolean existsPathInEvidenceTable = EclipseLinkMigrationManager.existsColumnInTable(unitOfWork, EVIDENCE_TABLE,
				EVIDENCE_PATH_COLUMN);

		if (existsTypeInEvidenceTable && existsPathInEvidenceTable) {

			// change evidence value to Gson
			unitOfWork.executeNonSelectingSQL(
					MessageFormat.format(QUERY_UPDATE_EVIDENCE_VALUE_BY_TYPE, GSON_FILE_VALUE, TYPE_FILE));
			unitOfWork.executeNonSelectingSQL(
					MessageFormat.format(QUERY_UPDATE_EVIDENCE_VALUE_BY_TYPE, GSON_URL_VALUE, TYPE_URL));

			changed = true;
		}
		// if the type column does not exist, put the values as files (for old<=0.2.0 or
		// recent versions>0.6.0)
		else if (!existsTypeInEvidenceTable && existsPathInEvidenceTable) {

			// change evidence value to Gson with default type file
			unitOfWork.executeNonSelectingSQL(
					MessageFormat.format(QUERY_UPDATE_EVIDENCE_VALUE_WITHOUT_TYPE, GSON_FILE_VALUE));

			changed = true;
		}

		// delete unused columns
		if (existsTypeInEvidenceTable) {
			unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_FIELD, EVIDENCE_TYPE_COLUMN));

			changed = true;
		}
		if (existsPathInEvidenceTable) {
			unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_FIELD, EVIDENCE_PATH_COLUMN));
		}

		return changed;
	}
}
