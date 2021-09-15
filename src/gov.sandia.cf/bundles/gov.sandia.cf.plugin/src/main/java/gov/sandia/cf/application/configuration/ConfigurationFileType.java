/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration;

/**
 * The configuration file type
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum ConfigurationFileType {

	PIRT, QOIPLANNING, PCMM, UNCERTAINTY, SYSTEM_REQUIREMENT, DECISION;

	/**
	 */
	private ConfigurationFileType() {
	}
}
