/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.report;

import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.IApplication;
import gov.sandia.cf.application.Service;
import gov.sandia.cf.constants.configuration.ExportOptions;

/**
 * Interface to manage ARG Report for PIRT methods
 * 
 * @author Didier Verstraete
 *
 */
@Service
public interface IReportARGPIRTApp extends IApplication {

	/**
	 * Generate PIRT sections
	 * 
	 * @param chapters the report content to extend
	 * @param options  the generation options
	 */
	void generateStructurePIRT(List<Map<String, Object>> chapters, Map<ExportOptions, Object> options);

}
