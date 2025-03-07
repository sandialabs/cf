/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.migration;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;

/**
 * Interface to manage Migration Application methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IMigrationApplication extends IApplication {

	/**
	 * Clear multiple assessments for the same user, role and tag (see gitlab issue
	 * #199).
	 * 
	 * @param pcmmSpecification the pcmm specification to get the PCMM mode
	 * @return true if the database needed and has been updated, otherwise false.
	 */
	boolean clearMultipleAssessment(PCMMSpecification pcmmSpecification);

	/**
	 * Clear the evidence path and replace "\\" by "/" to be correctly interpreted
	 * (see issue #262).
	 * 
	 * @return true if the database needed and has been updated, otherwise false.
	 */
	boolean clearEvidencePath();

}
