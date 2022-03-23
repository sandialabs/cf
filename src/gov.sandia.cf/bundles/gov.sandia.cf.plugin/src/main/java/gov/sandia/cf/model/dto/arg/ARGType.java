/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.model.dto.arg;

import java.util.List;

/**
 * Contains the arg types from the arg installation
 * 
 * @author Didier Verstraete
 *
 */
public class ARGType {

	/**
	 * The arg backend
	 */
	private List<String> backendTypes;

	/**
	 * The arg report types
	 */
	private List<String> reportTypes;

	@SuppressWarnings("javadoc")
	public List<String> getBackendTypes() {
		return backendTypes;
	}

	@SuppressWarnings("javadoc")
	public void setBackendTypes(List<String> backendTypes) {
		this.backendTypes = backendTypes;
	}

	@SuppressWarnings("javadoc")
	public List<String> getReportTypes() {
		return reportTypes;
	}

	@SuppressWarnings("javadoc")
	public void setReportTypes(List<String> reportTypes) {
		this.reportTypes = reportTypes;
	}

}
