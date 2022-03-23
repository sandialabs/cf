/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.requirement;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.requirement.IImportSysRequirementApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirementConstraint;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;

/**
 * Import Application manager for methods that are specific to the import of
 * System Requirements.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportSysRequirementApp extends AApplication implements IImportSysRequirementApp {

	/**
	 * ImportSysRequirementApp constructor
	 */
	public ImportSysRequirementApp() {
		super();
	}

	/**
	 * ImportSysRequirementApp constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportSysRequirementApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateRequirementsConfiguration(
			Model model, SystemRequirementSpecification currentSpecs, File pcmmSchemaFile)
			throws CredibilityException, IOException {
		// TODO to implement
		return null;
	}

	@Override
	public void importSysRequirementConfiguration(Model model, SystemRequirementSpecification specs)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importSysRequirementSpecification(Model model, User user, File requirementSchemaFile)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importSysRequirementParam(Model model, List<SystemRequirementParam> requirementParamList)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importSysRequirementSelectValue(List<SystemRequirementSelectValue> requirementSelectValueList,
			SystemRequirementParam requirementParam) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importSysRequirementConstraint(List<SystemRequirementConstraint> constraintList,
			SystemRequirementParam param) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public <M extends IImportable<M>> void importSysRequirementChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {
		// TODO to implement

	}
}
