/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.launcher;

/**
 * The Class CFClientSetup.
 * 
 * @author Didier Verstraete
 */
public class CFClientSetup {

	/** The backend connection type. */
	private CFBackendConnectionType backendConnectionType;

	/** The web server URL. */
	private String webServerURL;

	/** The model id. */
	private Integer modelId;

	/**
	 * Gets the backend connection type.
	 *
	 * @return the backend connection type
	 */
	public CFBackendConnectionType getBackendConnectionType() {
		return backendConnectionType;
	}

	/**
	 * Sets the backend connection type.
	 *
	 * @param backendConnectionType the new backend connection type
	 */
	public void setBackendConnectionType(CFBackendConnectionType backendConnectionType) {
		this.backendConnectionType = backendConnectionType;
	}

	/**
	 * Gets the web server URL.
	 *
	 * @return the web server URL
	 */
	public String getWebServerURL() {
		return webServerURL;
	}

	/**
	 * Sets the web server URL.
	 *
	 * @param webServerURL the new web server URL
	 */
	public void setWebServerURL(String webServerURL) {
		this.webServerURL = webServerURL;
	}

	/**
	 * Gets the model id.
	 *
	 * @return the model id
	 */
	public Integer getModelId() {
		return modelId;
	}

	/**
	 * Sets the model id.
	 *
	 * @param modelId the new model id
	 */
	public void setModelId(Integer modelId) {
		this.modelId = modelId;
	}

}
