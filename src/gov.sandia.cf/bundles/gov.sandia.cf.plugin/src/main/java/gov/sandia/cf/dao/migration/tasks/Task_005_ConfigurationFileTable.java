/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.text.MessageFormat;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.internal.sessions.ArrayRecord;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IConfigurationFileRepository;
import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.migration.EclipseLinkMigrationManager;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.dao.migration.MigrationTask;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.ConfigurationFile;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Move the configuration files from the MODEL table into a specific table
 * CONFIGURATION_FILE.
 * 
 * The model to apply the migration return true if the database needed and has
 * been updated, otherwise false.
 * 
 * @author Didier Verstraete
 *
 */
@MigrationTask(name = "0.6.0-iwfcf-384-confile-task", id = 5)
public class Task_005_ConfigurationFileTable implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_005_ConfigurationFileTable.class);

	private static final Map<CFFeature, String> CONFFILE_FIELD;
	static {
		CONFFILE_FIELD = new EnumMap<>(CFFeature.class);
		CONFFILE_FIELD.put(CFFeature.PIRT, "PIRT_SCHEMA_PATH"); //$NON-NLS-1$
		CONFFILE_FIELD.put(CFFeature.PCMM, "PCMM_SCHEMA_PATH"); //$NON-NLS-1$
		CONFFILE_FIELD.put(CFFeature.UNCERTAINTY, "COM_UNCERTAINTY_SCHEMA_PATH"); //$NON-NLS-1$
		CONFFILE_FIELD.put(CFFeature.SYSTEM_REQUIREMENTS, "COM_REQUIREMENT_SCHEMA_PATH"); //$NON-NLS-1$
	}

	private static final String QUERY_SELECT_FIELD = "SELECT {0} FROM MODEL"; //$NON-NLS-1$
	private static final String QUERY_DROP_FIELD = "ALTER TABLE MODEL DROP COLUMN {0};"; //$NON-NLS-1$

	@Override
	public String getName() {
		return this.getClass().getAnnotation(MigrationTask.class).name();
	}

	/**
	 * {@inheritDoc}
	 */
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

		// change PIRT conf file
		changed |= changeConfFile(model, CFFeature.PIRT, daoManager, unitOfWork);
		// change PCMM conf file
		changed |= changeConfFile(model, CFFeature.PCMM, daoManager, unitOfWork);
		// change Uncertainty conf file
		changed |= changeConfFile(model, CFFeature.UNCERTAINTY, daoManager, unitOfWork);
		// change System Requirement conf file
		changed |= changeConfFile(model, CFFeature.SYSTEM_REQUIREMENTS, daoManager, unitOfWork);

		return changed;
	}

	/**
	 * @param model      the model to set
	 * @param feature    the CF feature
	 * @param daoManager the dao manager
	 * @param unitOfWork the unitOfWork to query the database
	 * @return true if the database needed changes, otherwise false.
	 * @throws CredibilityMigrationException
	 */
	private boolean changeConfFile(Model model, CFFeature feature, IDaoManager daoManager, UnitOfWork unitOfWork)
			throws CredibilityMigrationException {

		boolean changed = false;

		// check table and column existence
		if (!EclipseLinkMigrationManager.existsColumnInTable(unitOfWork,
				Model.class.getAnnotation(javax.persistence.Table.class).name(), CONFFILE_FIELD.get(feature))) {
			return changed;
		}

		// search for the configuration file column in 'Model' table
		List<?> result = unitOfWork.executeSQL(MessageFormat.format(QUERY_SELECT_FIELD, CONFFILE_FIELD.get(feature)));
		String pirtPath = null;
		if (result != null) {
			Iterator<?> iterator = result.iterator();
			if (iterator.hasNext()) {
				ArrayRecord next = (ArrayRecord) iterator.next();
				Object returned = next.get(CONFFILE_FIELD.get(feature));
				if (returned != null) {
					pirtPath = returned.toString();
				}
			}
		}

		// if found
		if (!org.apache.commons.lang3.StringUtils.isBlank(pirtPath)) {

			// add configuration file
			ConfigurationFile confFile = new ConfigurationFile();
			confFile.setModel(model);
			confFile.setFeature(feature);
			confFile.setPath(pirtPath);
			confFile.setDateImport(DateTools.getCurrentDate());
			try {
				daoManager.getRepository(IConfigurationFileRepository.class).create(confFile);
			} catch (CredibilityException e) {
				throw new CredibilityMigrationException(e);
			}

			// drop column in 'Model' table
			unitOfWork.executeNonSelectingSQL(MessageFormat.format(QUERY_DROP_FIELD, CONFFILE_FIELD.get(feature)));

			changed = confFile != null;
		}

		return changed;
	}

}
