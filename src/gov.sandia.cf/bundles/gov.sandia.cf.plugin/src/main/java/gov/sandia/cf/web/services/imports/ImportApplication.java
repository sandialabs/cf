/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.imports;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.ConfigurationSchema;

/**
 * Import Application manager for methods that are specific to the import.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportApplication extends AApplication implements IImportApplication {

	/**
	 * ImportApplication constructor
	 */
	public ImportApplication() {
		super();
	}

	/**
	 * ImportApplication constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportApplication(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public <M extends IImportable<M>> Map<ImportActionType, List<?>> analyzeImport(List<M> newImportableList,
			List<M> existingImportableList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <M extends IImportable<M>> List<?> getChangesToAdd(List<M> newImportableList,
			List<M> existingImportableList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <M extends IImportable<M>> void importChanges(Model model, User user,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importConfiguration(Model model, User user, ConfigurationSchema confSchema)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public String getImportableName(Class<?> importClass) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <M extends IImportable<M>> boolean sameListContent(List<M> list1, List<M> list2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <M extends IImportable<M>> List<M> getChanges(
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport, Class<M> importClass,
			ImportActionType importAction) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> getListOfImportableFromAnalysis(
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDatabaseValueForGeneric(GenericParameter<?> parameter, GenericValue<?, ?> value) {
		// TODO Auto-generated method stub
		return null;
	}
}
