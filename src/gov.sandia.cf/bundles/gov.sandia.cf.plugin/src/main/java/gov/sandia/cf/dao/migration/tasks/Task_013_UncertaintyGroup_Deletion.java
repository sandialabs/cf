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
import gov.sandia.cf.model.User;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Deletion of Uncertainty Group and migration of the data to Uncertainty table.
 * Uncertainties can have multiple levels instead of only one with groups.
 * 
 * Deletion of Uncertainty "COM_" tables.
 * 
 * See issue #530 (https://gitlab.com/iwf/cf/-/issues/530).
 * 
 * @author Didier Verstraete
 */
@MigrationTask(name = "1.0.1-iwfcf-530-uncertaintygroup-task13", id = 13)
public class Task_013_UncertaintyGroup_Deletion implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_013_UncertaintyGroup_Deletion.class);

	/** The Constant OLD_UNCERTAINTY_GROUP_TABLE. */
	public static final String OLD_UNCERTAINTY_GROUP_TABLE = "COM_UNCERTAINTY_GROUP"; //$NON-NLS-1$

	/** The Constant OLD_UNCERTAINTY_TABLE. */
	public static final String OLD_UNCERTAINTY_TABLE = "COM_UNCERTAINTY"; //$NON-NLS-1$

	/** The Constant OLD_UNCERTAINTY_PARAM_TABLE. */
	public static final String OLD_UNCERTAINTY_PARAM_TABLE = "COM_UNCERTAINTY_PARAM"; //$NON-NLS-1$

	/** The Constant OLD_UNCERTAINTY_SELECT_VALUE_TABLE. */
	public static final String OLD_UNCERTAINTY_SELECT_VALUE_TABLE = "COM_UNCERTAINTY_SELECT_VALUE"; //$NON-NLS-1$

	/** The Constant OLD_UNCERTAINTY_VALUE_TABLE. */
	public static final String OLD_UNCERTAINTY_VALUE_TABLE = "COM_UNCERTAINTY_VALUE"; //$NON-NLS-1$

	/** The Constant QUERY_DROP_TABLE. */
	private static final String QUERY_DROP_TABLE = "DROP TABLE {0};"; //$NON-NLS-1$

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

		// migrate old tables data as is
		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_PARAM_TABLE)) {
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO UNCERTAINTY_PARAM"); //$NON-NLS-1$
			query.append("(ID,NAME,TYPE,MODEL_ID,PARENT_ID,LEVEL,DEFAULT_VALUE,REQUIRED) "); //$NON-NLS-1$
			query.append("SELECT ID,NAME,TYPE,MODEL_ID,PARENT_ID,LEVEL,DEFAULT_VALUE,REQUIRED "); //$NON-NLS-1$
			query.append("FROM COM_UNCERTAINTY_PARAM;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			changed = true;
		}

		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_SELECT_VALUE_TABLE)) {
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO UNCERTAINTY_SELECT_VALUE(ID,NAME,PARAMETER_ID) "); //$NON-NLS-1$
			query.append("SELECT ID,NAME,PARAMETER_ID FROM COM_UNCERTAINTY_SELECT_VALUE;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			changed = true;
		}

		// migrate Uncertainties
		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_TABLE)) {

			StringBuilder query = new StringBuilder();
			query.append(
					"INSERT INTO UNCERTAINTY(ID,ID_GENERATED,NAME,MODEL_ID,PARENT_ID,CREATION_DATE,USER_CREATION_ID) "); //$NON-NLS-1$
			query.append("SELECT ID,'',NULL,NULL,NULL,NULL,USER_CREATION_ID FROM COM_UNCERTAINTY ORDER BY ID;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// set model id
			query = new StringBuilder();
			query.append(
					"UPDATE UNCERTAINTY SET MODEL_ID=(SELECT TOP 1 MODEL_ID FROM COM_UNCERTAINTY_GROUP) WHERE UNCERTAINTY.MODEL_ID IS NULL;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// set user id if NULL
			query = new StringBuilder();
			query.append("UPDATE UNCERTAINTY SET USER_CREATION_ID="); //$NON-NLS-1$
			query.append("(SELECT ID FROM \"USER\" WHERE USERID='" + User.UNKNOWN_USERID + "') "); //$NON-NLS-1$ //$NON-NLS-2$
			query.append("WHERE UNCERTAINTY.USER_CREATION_ID IS NULL;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// set creation date
			query = new StringBuilder();
			query.append(
					"UPDATE UNCERTAINTY SET CREATION_DATE=(SELECT TOP 1 DATE_CREATION FROM COM_UNCERTAINTY_VALUE "); //$NON-NLS-1$
			query.append("WHERE UNCERTAINTY.ID=COM_UNCERTAINTY_VALUE.UNCERTAINTY_ID) "); //$NON-NLS-1$
			query.append("WHERE UNCERTAINTY.CREATION_DATE IS NULL;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// set creation date if null
			query = new StringBuilder();
			query.append("UPDATE UNCERTAINTY SET CREATION_DATE='1900-01-01' WHERE CREATION_DATE IS NULL;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// set name from variable name
			query = new StringBuilder();
			query.append("UPDATE UNCERTAINTY SET NAME=(SELECT VALUE FROM COM_UNCERTAINTY_VALUE "); //$NON-NLS-1$
			query.append(
					"INNER JOIN COM_UNCERTAINTY_PARAM ON COM_UNCERTAINTY_VALUE.PARAMETER_ID=COM_UNCERTAINTY_PARAM.ID "); //$NON-NLS-1$
			query.append(
					"WHERE COM_UNCERTAINTY_PARAM.NAME='Variable Name' AND UNCERTAINTY.ID=COM_UNCERTAINTY_VALUE.UNCERTAINTY_ID) "); //$NON-NLS-1$
			query.append("WHERE UNCERTAINTY.NAME IS NULL;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			changed = true;
		}

		// migrate Uncertainty groups
		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_GROUP_TABLE)) {

			StringBuilder query = new StringBuilder();
			query.append(
					"INSERT INTO UNCERTAINTY(ID_GENERATED,NAME,MODEL_ID,PARENT_ID,CREATION_DATE,USER_CREATION_ID) "); //$NON-NLS-1$
			query.append("SELECT '',NAME,MODEL_ID,NULL,NULL,USER_CREATION_ID FROM COM_UNCERTAINTY_GROUP;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// set creation date if null
			query = new StringBuilder();
			query.append("UPDATE UNCERTAINTY SET CREATION_DATE='1900-01-01' WHERE CREATION_DATE IS NULL;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// set user id if NULL
			query = new StringBuilder();
			query.append("UPDATE UNCERTAINTY SET USER_CREATION_ID="); //$NON-NLS-1$
			query.append("(SELECT ID FROM \"USER\" WHERE USERID='" + User.UNKNOWN_USERID + "') "); //$NON-NLS-1$ //$NON-NLS-2$
			query.append("WHERE UNCERTAINTY.USER_CREATION_ID IS NULL;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// set parent id
			query = new StringBuilder();
			query.append("UPDATE UNCERTAINTY A SET A.PARENT_ID="); //$NON-NLS-1$
			query.append("(SELECT NEW_G_ID FROM "); //$NON-NLS-1$
			query.append("(SELECT U.ID AS U_ID, CORRESPONDANCE.OLD_G_ID, CORRESPONDANCE.NEW_G_ID "); //$NON-NLS-1$
			query.append("FROM COM_UNCERTAINTY U "); //$NON-NLS-1$
			query.append("LEFT JOIN (SELECT B.ID AS NEW_G_ID, COM_UNCERTAINTY_GROUP.ID AS OLD_G_ID "); //$NON-NLS-1$
			query.append("FROM UNCERTAINTY B INNER JOIN COM_UNCERTAINTY_GROUP ON B.NAME=COM_UNCERTAINTY_GROUP.NAME) "); //$NON-NLS-1$
			query.append("AS CORRESPONDANCE ON U.GROUP_ID=CORRESPONDANCE.OLD_G_ID)"); //$NON-NLS-1$
			query.append("WHERE U_ID=A.ID);"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			changed = true;
		}

		// migrate Uncertainty Values
		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_VALUE_TABLE)) {
			StringBuilder query = new StringBuilder();
			query.append("INSERT INTO UNCERTAINTY_VALUE"); //$NON-NLS-1$
			query.append("(ID,VALUE,PARAMETER_ID,UNCERTAINTY_ID,DATE_CREATION,"); //$NON-NLS-1$
			query.append("DATE_UPDATE,USER_CREATION_ID,USER_UPDATE_ID) "); //$NON-NLS-1$
			query.append("SELECT ID,VALUE,PARAMETER_ID,UNCERTAINTY_ID,DATE_CREATION,"); //$NON-NLS-1$
			query.append("DATE_UPDATE,USER_CREATION_ID,USER_UPDATE_ID "); //$NON-NLS-1$
			query.append("FROM COM_UNCERTAINTY_VALUE;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			changed = true;
		}

		// drop old tables
		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_VALUE_TABLE)) {
			unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_TABLE, OLD_UNCERTAINTY_VALUE_TABLE));

			changed = true;
		}

		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_TABLE)) {
			unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_TABLE, OLD_UNCERTAINTY_TABLE));

			changed = true;
		}

		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_GROUP_TABLE)) {
			unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_TABLE, OLD_UNCERTAINTY_GROUP_TABLE));

			changed = true;
		}

		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_SELECT_VALUE_TABLE)) {
			unitOfWork
					.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_TABLE, OLD_UNCERTAINTY_SELECT_VALUE_TABLE));

			changed = true;
		}

		if (EclipseLinkMigrationManager.existsTable(unitOfWork, OLD_UNCERTAINTY_PARAM_TABLE)) {

			// drop foreign key to COM_UNCERTAINTY_PARAM
			StringBuilder query = new StringBuilder();
			query.append("ALTER TABLE UNCERTAINTY_PARAM_CONSTRAINT "); //$NON-NLS-1$
			query.append("DROP CONSTRAINT FK_UNCERTAINTY_PARAM_CONSTRAINT_PARAMETER_ID;"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// drop foreign key to UNCERTAINTY_PARAM
			query = new StringBuilder();
			query.append("ALTER TABLE UNCERTAINTY_PARAM_CONSTRAINT "); //$NON-NLS-1$
			query.append("ADD CONSTRAINT FK_UNCERTAINTY_PARAM_CONSTRAINT_PARAMETER_ID FOREIGN KEY (PARAMETER_ID) "); //$NON-NLS-1$
			query.append("REFERENCES UNCERTAINTY_PARAM(ID);"); //$NON-NLS-1$
			unitOfWork.executeNonSelectingSQL(query.toString());

			// drop table
			unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_TABLE, OLD_UNCERTAINTY_PARAM_TABLE));

			changed = true;
		}

		return changed;
	}

}
