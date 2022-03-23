/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.decision;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.decision.IImportDecisionApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;

/**
 * Import Application manager for methods that are specific to the import of
 * Decision.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportDecisionApp extends AApplication implements IImportDecisionApp {

	/**
	 * ImportDecisionApp constructor
	 */
	public ImportDecisionApp() {
		super();
	}

	/**
	 * ImportDecisionApp constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportDecisionApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateDecisionConfiguration(
			Model model, DecisionSpecification currentSpecs, File schemaFile) throws CredibilityException, IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void importDecisionSpecification(Model model, User user, File schemaFile)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importDecisionConfiguration(Model model, DecisionSpecification specs) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importDecisionParam(Model model, List<DecisionParam> paramList) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importDecisionSelectValue(List<DecisionSelectValue> selectValueList, DecisionParam param)
			throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importDecisionConstraint(List<DecisionConstraint> constraintList, DecisionParam param)
			throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public <M extends IImportable<M>> void importDecisionChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {
		// TODO Auto-generated method stub

	}
}
