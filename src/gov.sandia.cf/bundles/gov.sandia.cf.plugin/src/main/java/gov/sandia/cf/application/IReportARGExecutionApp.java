/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application;

import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IPath;

import gov.sandia.cf.application.configuration.arg.ARGType;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.ARGParameters;

/**
 * Interface to manage Report Application methods
 * 
 * @author Didier Verstraete
 *
 */
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
	 * @param argParameters The ARG parameters
	 * @param errorLog      the error logger
	 * @param infoLog       the info logger
	 * @return the ARG type loaded from ARG
	 * @throws IOException          if a read/write error occurs
	 * @throws URISyntaxException   if an URI syntax error occurs
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	ARGType getARGTypes(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog)
			throws URISyntaxException, IOException, CredibilityException;

	/**
	 * @param argParameters The ARG parameters
	 * @param errorLog      the error logger
	 * @param infoLog       the info logger
	 * @return the ARG version
	 * @throws IOException          if a read/write error occurs
	 * @throws URISyntaxException   if an URI syntax error occurs
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	String getARGVersion(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog)
			throws URISyntaxException, IOException, CredibilityException;

	/**
	 * Launch generation ARG command
	 * 
	 * @param argParameters The ARG parameters
	 * @param errorLog      the error logger
	 * @param infoLog       the info logger
	 * @throws CredibilityException if an error occurs during report generation
	 */
	void generateReportARG(ARGParameters argParameters, StringBuilder errorLog, StringBuilder infoLog)
			throws CredibilityException;
}
