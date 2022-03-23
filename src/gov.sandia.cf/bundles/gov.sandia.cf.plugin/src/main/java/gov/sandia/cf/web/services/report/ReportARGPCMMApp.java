/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.report;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.report.IReportARGPCMMApp;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * Manage ARG Report for PCMM methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGPCMMApp extends AApplication implements IReportARGPCMMApp {

	/**
	 * The constructor
	 */
	public ReportARGPCMMApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGPCMMApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public void generateStructurePCMM(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options)
			throws CredibilityException {
		// TODO to implement

	}
}
