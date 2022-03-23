/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.dao.migration.tasks;

import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import org.eclipse.persistence.sessions.UnitOfWork;
import org.hsqldb.cmdline.SqlToolError;
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
 * Migration of fields to add richtext content and allow bigger content in the
 * columns: from VARCHAR to LONGVARCHAR
 * 
 * @author Didier Verstraete
 *
 */
@MigrationTask(name = "0.2.0-iwfcf-219.sql", id = 2)
public class Task_002_RichTextContent implements IMigrationTask {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(Task_002_RichTextContent.class);

	private static final String SCRIPT_NAME = "0.2.0-iwfcf-219.sql"; //$NON-NLS-1$

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

		// get the unit of work
		UnitOfWork unitOfWork = daoManager.getEntityManager().unwrap(UnitOfWork.class);

		// change QoI tags and put them under QoI
		try {
			EclipseLinkMigrationManager.executeSQLScript(unitOfWork, SCRIPT_NAME);
		} catch (SqlToolError | SQLException | IOException | URISyntaxException e) {
			logger.error(e.getMessage());
		}

		return true;
	}

}
