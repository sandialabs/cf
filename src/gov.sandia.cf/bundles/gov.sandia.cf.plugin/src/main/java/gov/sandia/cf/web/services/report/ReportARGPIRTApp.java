/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.report;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.report.IReportARGPIRTApp;
import gov.sandia.cf.constants.configuration.ExportOptions;

/**
 * Manage ARG Report for PIRT methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGPIRTApp extends AApplication implements IReportARGPIRTApp {

	/**
	 * The constructor
	 */
	public ReportARGPIRTApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGPIRTApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public void generateStructurePIRT(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options) {
		// TODO to implement

	}

}
