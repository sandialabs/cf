/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.configuration.arg;

import java.util.Arrays;
import java.util.List;

/**
 * The ARG Report types.
 * 
 * @author Didier Verstraete
 *
 */
public enum ARGReportTypeDefault {

	@SuppressWarnings("javadoc")
	REPORT("Report"); //$NON-NLS-1$

	/**
	 * The report type
	 */
	private String type;

	/**
	 * @param type
	 */
	private ARGReportTypeDefault(String type) {
		this.type = type;
	}

	@SuppressWarnings("javadoc")
	public String getType() {
		return this.type;
	}

	/**
	 * @param reportType the report type as string
	 * @return the associated report type or null if not found
	 */
	public static ARGReportTypeDefault getReportEnum(String reportType) {
		return Arrays.asList(values()).stream().filter(v -> v.getType().equals(reportType)).findFirst().orElse(null);
	}

	/**
	 * @return the list of Report Types
	 */
	public static List<ARGReportTypeDefault> getValues() {
		return Arrays.asList(values());
	}
}
