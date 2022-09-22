/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.report;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.report.IReportARGExecutionApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.dto.arg.ARGType;

/**
 * Manage Report Execution Application methods
 * 
 * @author Didier Verstraete
 *
 */
public class ReportARGExecutionApp extends AApplication implements IReportARGExecutionApp {

	/**
	 * The constructor
	 */
	public ReportARGExecutionApp() {
		super();
	}

	/**
	 * The constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ReportARGExecutionApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public ARGParameters getARGParameters() {
		// TODO to implement
		return null;
	}

	@Override
	public ARGParameters addDefaultARGParameters(IPath cfProjectPath) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public ARGParameters updateARGParameters(ARGParameters argParameters) throws CredibilityException {
		// TODO to implement
		return null;
	}

	@Override
	public ARGType getARGTypes(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor progressMonitor) {
		// TODO to implement
		return null;
	}

	@Override
	public String getARGVersion(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor progressMonitor) {
		// TODO to implement
		return null;
	}

	@Override
	public void generateReportARG(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor monitor) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public boolean isEnabled() {
		// TODO implement
		return false;
	}
}