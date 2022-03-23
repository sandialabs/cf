/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.web.services.pcmm;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.pcmm.IImportPCMMApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.IAssessable;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelColor;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMOption;
import gov.sandia.cf.model.PCMMPhase;
import gov.sandia.cf.model.PCMMPlanningParam;
import gov.sandia.cf.model.PCMMPlanningQuestion;
import gov.sandia.cf.model.PCMMSubelement;
import gov.sandia.cf.model.Role;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.PCMMSpecification;

/**
 * Import Application manager for methods that are specific to the import of
 * PCMM.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportPCMMApp extends AApplication implements IImportPCMMApp {

	/**
	 * ImportPCMMApp constructor
	 */
	public ImportPCMMApp() {
		super();
	}

	/**
	 * ImportPCMMApp constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportPCMMApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePCMMConfiguration(
			Model model, PCMMSpecification currentSpecs, File pcmmSchemaFile) throws CredibilityException, IOException {
		// TODO to implement
		return null;
	}

	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePCMMPlanningConfiguration(
			Model model, List<PCMMPlanningParam> planningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions) {
		// TODO to implement
		return null;
	}

	@Override
	public void importPCMMSpecification(Model model, User user, File pcmmSchemaFile)
			throws CredibilityException, IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void importPCMMConfiguration(Model model, PCMMSpecification pcmmSpecs) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMOptionsWithPhases(List<PCMMPhase> phases) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMOptions(List<PCMMOption> options) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMLevelColors(List<PCMMLevelColor> levelColors) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMElements(Model model, List<PCMMElement> elements) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMRoles(List<Role> roles) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMSubelements(List<PCMMSubelement> subelementList, PCMMElement createdElement)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMLevels(List<PCMMLevel> levelList, PCMMElement element, PCMMSubelement subelement)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMLevelDescriptors(List<PCMMLevelDescriptor> levelDescList, PCMMLevel level)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMPlanning(Model model, List<PCMMPlanningParam> planningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions) throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMPlanningParam(Model model, List<PCMMPlanningParam> planningFields)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public void importPCMMPlanningQuestions(Model model, List<PCMMPlanningQuestion> planningQuestions)
			throws CredibilityException {
		// TODO to implement

	}

	@Override
	public <M extends IImportable<M>> void importPCMMChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {
		// TODO to implement

	}
}
