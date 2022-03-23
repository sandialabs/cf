/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.migration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.dao.IPCMMAssessmentRepository;
import gov.sandia.cf.dao.IPCMMEvidenceRepository;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;

/**
 * Manage Migration Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class MigrationApplication extends AApplication implements IMigrationApplication {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(MigrationApplication.class);

	/**
	 * The constructor
	 */
	public MigrationApplication() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public MigrationApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean clearMultipleAssessment(PCMMSpecification pcmmSpecification) {

		if (pcmmSpecification == null) {
			return false;
		}

		boolean assessmentsCleared = getDaoManager().getRepository(IPCMMAssessmentRepository.class)
				.clearMultipleAssessment(pcmmSpecification.getMode());

		if (assessmentsCleared) {
			logger.info("The PCMMAssessment table contains bad assessments. The database has been cleared."); //$NON-NLS-1$
		}

		return assessmentsCleared;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean clearEvidencePath() {

		boolean pathCleared = getDaoManager().getRepository(IPCMMEvidenceRepository.class).clearEvidencePath();

		if (pathCleared) {
			logger.info(
					"The PCMMEvidence table contains evidence with bad path. containing character. The character \"\\\\\"  has been replaced by \"/\". The database has been cleared."); //$NON-NLS-1$
		}

		return pathCleared;
	}
}
