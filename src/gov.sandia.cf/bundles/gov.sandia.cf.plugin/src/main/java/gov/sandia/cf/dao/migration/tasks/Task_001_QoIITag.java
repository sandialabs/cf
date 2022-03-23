/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.hsqldb.cmdline.SqlToolError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.dao.IDaoManager;
import gov.sandia.cf.dao.IModelRepository;
import gov.sandia.cf.dao.IQuantityOfInterestRepository;
import gov.sandia.cf.dao.migration.EclipseLinkMigrationManager;
import gov.sandia.cf.dao.migration.IMigrationTask;
import gov.sandia.cf.dao.migration.MigrationTask;
import gov.sandia.cf.exceptions.CredibilityMigrationException;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.NullParameter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Migration of QoI to manage tags on the QoI Home View. This task update the
 * QoI created with a datetag and set the tag as a child of the QoI.
 * 
 * 
 * @author Didier Verstraete
 *
 */
@MigrationTask(name = "0.2.0-iwfcf-216.sql", id = 1)
public class Task_001_QoIITag implements IMigrationTask {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_001_QoIITag.class);

	private static final String SCRIPT_NAME = "0.2.0-iwfcf-216.sql"; //$NON-NLS-1$

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

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(QuantityOfInterest.Filter.DATE_TAG, NullParameter.NOT_NULL);
		filters.put(QuantityOfInterest.Filter.PARENT, NullParameter.NULL);
		List<QuantityOfInterest> findBy = daoManager.getRepository(IQuantityOfInterestRepository.class).findBy(filters);

		boolean needToChange = findBy != null && !findBy.isEmpty();

		if (needToChange) {
			// get the unit of work
			UnitOfWork unitOfWork = daoManager.getEntityManager().unwrap(UnitOfWork.class);

			// change QoI tags and put them under QoI
			try {
				EclipseLinkMigrationManager.executeSQLScript(unitOfWork, SCRIPT_NAME);
			} catch (SqlToolError | SQLException | IOException | URISyntaxException e) {
				logger.error(e.getMessage());
			}
			return true;
		} else {
			return false;
		}
	}

}
