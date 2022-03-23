/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.dao.migration.MigrationTask;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Deletion of evidence without an assessable (PCMM Element or Subelement).
 * Those evidence can not be displayed and pollute the database.
 * 
 * Those evidence should no longer been created without an assessable but
 * remains from an old CF version &lt;= 0.2.0.
 * 
 * @author Didier Verstraete
 */
@MigrationTask(name = "1.0.0-iwfcf-479-evidenceNoAssessable-task11", id = 11)
public class Task_011_EvidenceNoAssessable implements IMigrationTask {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_011_EvidenceNoAssessable.class);

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

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(PCMMEvidence.Filter.ELEMENT, NullParameter.NULL);
		filters.put(PCMMEvidence.Filter.SUBELEMENT, NullParameter.NULL);
		List<PCMMEvidence> found = daoManager.getRepository(IPCMMEvidenceRepository.class).findBy(filters);

		if (found != null && !found.isEmpty()) {
			for (PCMMEvidence evidenceToDelete : found) {
				daoManager.getRepository(IPCMMEvidenceRepository.class).delete(evidenceToDelete);
				changed |= true;
			}
		}

		return changed;
	}
}
