/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.exports;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.exports.IExportApplication;
import gov.sandia.cf.constants.configuration.ExportOptions;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;

/**
 * Import Application manager for methods that are specific to the export.
 * 
 * @author Didier Verstraete
 * 
 */
public class ExportApplication extends AApplication implements IExportApplication {

	/**
	 * Constructor
	 */
	public ExportApplication() {
		super();
	}

	/**
	 * Constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ExportApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public void exportDecisionSchema(File schemaFile, DecisionSpecification specification)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportQoIPlanningSchema(File schemaFile, QoIPlanningSpecification specification)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportPIRTSchema(File schemaFile, PIRTSpecification specification)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportPCMMSchema(File schemaFile, PCMMSpecification specification)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportUncertaintySchema(File schemaFile, UncertaintySpecification specification)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportSysRequirementsSchema(File schemaFile, SystemRequirementSpecification specification)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exportData(File schemaFile, Map<ExportOptions, Object> exportOptions)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub
		
	}
}
