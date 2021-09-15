/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model;

/**
 * CF Import Schema
 * 
 * @author Didier Verstraete
 *
 */
@SuppressWarnings("javadoc")
public enum ImportSchema {
	DECISION("Decision"), //$NON-NLS-1$
	QOI_PLANNING("QoI Planning"), //$NON-NLS-1$
	SYSTEM_REQUIREMENTS("System Requirements"), //$NON-NLS-1$
	PIRT("PIRT"), //$NON-NLS-1$
	PCMM("PCMM"), //$NON-NLS-1$
	UNCERTAINTY("Uncertainty"); //$NON-NLS-1$

	/**
	 * The schema name
	 */
	private String name;

	ImportSchema(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
