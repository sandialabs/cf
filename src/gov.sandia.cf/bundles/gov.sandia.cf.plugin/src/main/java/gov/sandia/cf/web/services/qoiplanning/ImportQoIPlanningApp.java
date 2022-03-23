/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.qoiplanning;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.qoiplanning.IImportQoIPlanningApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;

/**
 * Import Application manager for methods that are specific to the import of QoI
 * Planning.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportQoIPlanningApp extends AApplication implements IImportQoIPlanningApp {

	/**
	 * ImportQoIPlanningApp constructor
	 */
	public ImportQoIPlanningApp() {
		super();
	}

	/**
	 * ImportQoIPlanningApp constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportQoIPlanningApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateQoIPlanningConfiguration(
			File schemaFile) throws CredibilityException, IOException {
		// TODO to implement
		return null;
	}

	@Override
	public void importQoIPlanningSpecification(Model model, User user, File schemaFile)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importQoIPlanningConfiguration(Model model, QoIPlanningSpecification specs)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importQoIPlanningParam(Model model, List<QoIPlanningParam> paramList) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importQoIPlanningSelectValue(List<QoIPlanningSelectValue> selectValueList, QoIPlanningParam param)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importQoIPlanningConstraint(List<QoIPlanningConstraint> constraintList, QoIPlanningParam param)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public <M extends IImportable<M>> void importQoIPlanningChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {
		// TODO to implement

	}
}
