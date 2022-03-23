/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.pirt;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pirt.IImportPIRTApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PIRTSpecification;

/**
 * Import Application manager for methods that are specific to the import of
 * PIRT.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportPIRTApp extends AApplication implements IImportPIRTApp {

	/**
	 * ImportPIRTApp constructor
	 */
	public ImportPIRTApp() {
		super();
	}

	/**
	 * ImportPIRTApp constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportPIRTApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePIRTConfiguration(
			Model model, PIRTSpecification currentSpecs, File pirtSchemaFile) throws CredibilityException, IOException {
		// TODO to implement
		return null;
	}

	@Override
	public void importPIRTSpecification(Model model, User user, File pirtSchemaFile)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importPIRTConfiguration(Model model, PIRTSpecification pirtSpecs) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPIRTColors(Model model, List<PIRTLevelDifferenceColor> colors) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPIRTColumns(Model model, List<PIRTAdequacyColumn> list) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPIRTHeaders(Model model, List<PIRTDescriptionHeader> list) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPIRTLevels(Model model, Map<String, PIRTLevelImportance> map) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPIRTLevels(Model model, List<PIRTLevelImportance> levels) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPIRTGuidelines(List<PIRTAdequacyColumnGuideline> columnGuidelines) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPIRTLevelGuidelines(List<PIRTAdequacyColumnLevelGuideline> levelGuidelines)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public <M extends IImportable<M>> void importPIRTChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {
		// TODO to implement

	}
}
