/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.uncertainty;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.uncertainty.IImportUncertaintyApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.UncertaintyValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.UncertaintySpecification;

/**
 * Import Application manager for methods that are specific to the import of
 * Uncertainties.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportUncertaintyApp extends AApplication implements IImportUncertaintyApp {

	/**
	 * ImportApplication constructor
	 */
	public ImportUncertaintyApp() {
		super();
	}

	/**
	 * ImportApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportUncertaintyApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateUncertaintyConfiguration(
			Model model, UncertaintySpecification currentSpecs, File pcmmSchemaFile)
			throws CredibilityException, IOException {
		// TODO to implement
		return null;
	}

	@Override
	public void importUncertaintyConfiguration(Model model, UncertaintySpecification specs)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importUncertaintySpecification(Model model, User user, File schemaFile)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importUncertaintyParam(Model model, List<UncertaintyParam> uncertaintyParamList)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importUncertaintySelectValue(List<UncertaintySelectValue> uncertaintySelectValueList,
			UncertaintyParam uncertaintyParam) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importUncertaintyConstraint(List<UncertaintyConstraint> constraintList, UncertaintyParam param)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public <M extends IImportable<M>> void importUncertaintyChanges(Model model, User user,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importUncertaintyData(Model model, User user, File uncertaintyFile)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importUncertainties(Model model, User user, Uncertainty parent, List<Uncertainty> uncertaintyList)
			throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importUncertaintyValues(Uncertainty uncertainty, User user, List<UncertaintyValue> values)
			throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importUpdateUncertaintyParam(Model model, List<UncertaintyParam> uncertaintyParamList)
			throws CredibilityException {
		// TODO Auto-generated method stub
		
	}
}
