/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.report;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;

/**
 * Interface to manage ARG Report for Planning methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IReportARGPlanningApp extends IApplication {

	/**
	 * Generate Planning chapters
	 * 
	 * @param chapters the Planning chapters
	 * @param options  the export options
	 * @throws CredibilityException if an error occurs during CF variable parsing
	 */
	void generateStructurePlanning(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options)
			throws CredibilityException;

}
