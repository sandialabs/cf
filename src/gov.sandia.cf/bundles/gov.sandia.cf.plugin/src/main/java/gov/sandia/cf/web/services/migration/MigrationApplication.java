/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.migration;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.migration.IMigrationApplication;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;

/**
 * Manage Migration Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class MigrationApplication extends AApplication implements IMigrationApplication {

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

	@Override
	public boolean clearMultipleAssessment(PCMMSpecification pcmmSpecification) {
		// TODO to implement
		return false;
	}

	@Override
	public boolean clearEvidencePath() {
		// TODO to implement
		return false;
	}
}
