/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.report;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;
import gov.sandia.cf.model.dto.arg.ARGType;

/**
 * Interface to manage Report Application methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IReportARGExecutionApp extends IApplication {

	/**
	 * @return the arg parameters
	 */
	ARGParameters getARGParameters();

	/**
	 * @param cfProjectPath the cf project path
	 * @return the default ARG parameters
	 * @throws CredibilityException if an error occurs
	 */
	ARGParameters addDefaultARGParameters(final IPath cfProjectPath) throws CredibilityException;

	/**
	 * @param argParameters the arg parameters to update
	 * @return the arg parameters updated
	 * @throws CredibilityException if an error occured
	 */
	ARGParameters updateARGParameters(ARGParameters argParameters) throws CredibilityException;

	/**
	 * Gets the ARG types.
	 *
	 * @param argParameters   The ARG parameters
	 * @param errorLog        the error logger
	 * @param infoLog         the info logger
	 * @param progressMonitor the progress monitor
	 * @return the ARG type loaded from ARG
	 * @throws URISyntaxException   the URI syntax exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws CredibilityException the credibility exception
	 */
	ARGType getARGTypes(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor progressMonitor) throws URISyntaxException, IOException, CredibilityException;

	/**
	 * Gets the ARG version.
	 *
	 * @param argParameters   The ARG parameters
	 * @param errorLog        the error logger
	 * @param infoLog         the info logger
	 * @param progressMonitor the progress monitor
	 * @return the ARG version
	 * @throws URISyntaxException   the URI syntax exception
	 * @throws IOException          Signals that an I/O exception has occurred.
	 * @throws CredibilityException the credibility exception
	 */
	String getARGVersion(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor progressMonitor) throws URISyntaxException, IOException, CredibilityException;

	/**
	 * Launch generation ARG command.
	 *
	 * @param argParameters   The ARG parameters
	 * @param errorLog        the error logger
	 * @param infoLog         the info logger
	 * @param progressMonitor the progress monitor
	 * @throws CredibilityException if an error occurs during report generation
	 */
	void generateReportARG(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog,
			IProgressMonitor progressMonitor) throws CredibilityException;

	/**
	 * Checks if is enabled.
	 *
	 * @return true, if is enabled
	 */
	boolean isEnabled();
}
