/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * 
 * The list of CF features
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum CFFeature {

	HOME, // Home view
	CONFIGURATION, // Configuration view
	// Planning
	INTENDED_PURPOSE, // Intended Purpose view
	SYSTEM_REQUIREMENTS, // System Requirements view
	QOI_PLANNER, // QoI Planner view
	UNCERTAINTY, // Uncertainty view
	PCMM_PLANNING, // PCMM Planning view
	DECISION, // Decision view
	// PIRT
	PIRT, // PIRT view
	// PCMM
	PCMM, // PCMM view
	// Communicate
	GEN_REPORT, // Report view
	// not used yet
	CRED_EVIDENCE_PACKAGE_OVERVIEW, CRED_EVIDENCE_PACKAGE, MARGIN_BOUNDS, PEER_REVIEWS;

	/**
	 */
	private CFFeature() {
	}

}
