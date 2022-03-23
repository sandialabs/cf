/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

/**
 * The Class CFClientSetup.
 * 
 * @author Didier Verstraete
 */
public class CFClientSetupFactory {

	private CFClientSetupFactory() {
		// Do not instantiate
	}

	/**
	 * Gets the default CF client setup.
	 *
	 * @return the CF client setup populated.
	 */
	public static CFClientSetup get() {
		return get(CFBackendConnectionType.FILE);
	}

	/**
	 * Gets the default CF client setup.
	 *
	 * @param backendConnection the backend connection
	 * @return the CF client setup populated.
	 */
	public static CFClientSetup get(final CFBackendConnectionType backendConnection) {
		CFClientSetup cfClientSetupTmp = new CFClientSetup();
		if (backendConnection == null) {
			cfClientSetupTmp.setBackendConnectionType(CFBackendConnectionType.FILE);
		} else {
			cfClientSetupTmp.setBackendConnectionType(backendConnection);
		}
		return cfClientSetupTmp;
	}
}
