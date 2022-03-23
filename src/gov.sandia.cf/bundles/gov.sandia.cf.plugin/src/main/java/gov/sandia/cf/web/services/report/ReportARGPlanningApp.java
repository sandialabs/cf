/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.report;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.report.IReportARGPlanningApp;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * Manage ARG Report for Planning methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGPlanningApp extends AApplication implements IReportARGPlanningApp {

	/**
	 * The constructor
	 */
	public ReportARGPlanningApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGPlanningApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public void generateStructurePlanning(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options)
			throws CredibilityException {
		// TODO to implement

	}

}
