/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.IImportPCMMApp;
import gov.sandia.cf.application.IPCMMApplication;
import gov.sandia.cf.application.IPCMMPlanningApplication;
import gov.sandia.cf.application.configuration.pcmm.PCMMSpecification;
import gov.sandia.cf.application.configuration.pcmm.YmlReaderPCMMSchema;
import gov.sandia.cf.dao.IPCMMElementRepository;
import gov.sandia.cf.dao.IPCMMLevelColorRepository;
import gov.sandia.cf.dao.IPCMMLevelDescRepository;
import gov.sandia.cf.dao.IPCMMLevelRepository;
import gov.sandia.cf.dao.IPCMMOptionRepository;
import gov.sandia.cf.dao.IPCMMPlanningParamRepository;
import gov.sandia.cf.dao.IPCMMPlanningQuestionRepository;
import gov.sandia.cf.dao.IPCMMSubelementRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
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
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Application manager for methods that are specific to the import of
 * PCMM.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportPCMMApp extends AApplication implements IImportPCMMApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportPCMMApp.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> void importPCMMChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {

		if (toImport != null && !toImport.isEmpty()) {

			/*
			 * PCMM Phases
			 */
			List<PCMMOption> pcmmOptionsToAdd = getAppMgr().getService(IImportApplication.class).getChanges(toImport,
					PCMMOption.class, ImportActionType.TO_ADD);
			List<PCMMOption> pcmmOptionsToDelete = getAppMgr().getService(IImportApplication.class).getChanges(toImport,
					PCMMOption.class, ImportActionType.TO_DELETE);

			// import PCMM options
			importPCMMOptions(pcmmOptionsToAdd);

			// delete PCMM options
			getAppMgr().getService(IPCMMApplication.class).deleteAllPCMMOptions(pcmmOptionsToDelete);

			/*
			 * PCMM Planning import
			 */
			List<PCMMPlanningParam> planningFieldsToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PCMMPlanningParam.class, ImportActionType.TO_ADD);
			List<PCMMPlanningParam> planningFieldsToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PCMMPlanningParam.class, ImportActionType.TO_DELETE);
			List<PCMMPlanningQuestion> planningQuestionsToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PCMMPlanningQuestion.class, ImportActionType.TO_ADD);
			List<PCMMPlanningQuestion> planningQuestionsToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PCMMPlanningQuestion.class, ImportActionType.TO_DELETE);

			// import planning fields
			getAppMgr().getService(IPCMMPlanningApplication.class).addAllPCMMPlanningParam(model, planningFieldsToAdd);

			// import planning questions
			getAppMgr().getService(IPCMMPlanningApplication.class).addAllPCMMPlanningQuestion(model,
					planningQuestionsToAdd);

			// delete planning fields
			getAppMgr().getService(IPCMMPlanningApplication.class).deleteAllPlanningParameter(planningFieldsToDelete);

			// delete planning questions
			getAppMgr().getService(IPCMMPlanningApplication.class)
					.deleteAllPlanningQuestions(planningQuestionsToDelete);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePCMMConfiguration(
			Model model, PCMMSpecification currentSpecs, File pcmmSchemaFile) throws CredibilityException, IOException {
		// Check errors
		if (pcmmSchemaFile == null || !pcmmSchemaFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_CONF_SCHEMAFILE_NOTEXISTS,
					RscTools.getString(RscConst.MSG_PCMM)));
		}

		// Initialize
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = new HashMap<>();
		YmlReaderPCMMSchema pcmmReader = new YmlReaderPCMMSchema();

		// Get configuration
		PCMMSpecification newSpecs = pcmmReader.load(pcmmSchemaFile);

		// Analyze
		if (newSpecs != null) {
			// PCMM Phases
			List<PCMMOption> currentOptions = currentSpecs != null ? currentSpecs.getOptions() : null;
			analysis.put(PCMMOption.class, getAppMgr().getService(IImportApplication.class)
					.analyzeImport(newSpecs.getOptions(), currentOptions));

			// PCMM Planning
			analysis.putAll(analyzeUpdatePCMMPlanningConfiguration(model, newSpecs.getPlanningFields(),
					newSpecs.getPlanningQuestions()));
		}

		return analysis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePCMMPlanningConfiguration(
			Model model, List<PCMMPlanningParam> newPlanningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> mapNewPlanningQuestions) {

		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = new HashMap<>();

		/**
		 * Analyze planning fields
		 */
		// get existing planning fields
		List<PCMMPlanningParam> dbPlanningFields = getDaoManager().getRepository(IPCMMPlanningParamRepository.class)
				.findAll();
		if (dbPlanningFields != null) {
			dbPlanningFields = dbPlanningFields.stream().filter(param -> param.getParent() == null)
					.collect(Collectors.toList());

		}

		// get planning fields analysis
		analysis.put(PCMMPlanningParam.class,
				getAppMgr().getService(IImportApplication.class).analyzeImport(newPlanningFields, dbPlanningFields));

		/**
		 * Analyze planning questions
		 */
		// get new planning questions
		List<PCMMPlanningQuestion> newPlanningQuestions = new ArrayList<>();
		if (mapNewPlanningQuestions != null && !mapNewPlanningQuestions.isEmpty()) {
			newPlanningQuestions = mapNewPlanningQuestions.values().stream().flatMap(List::stream)
					.collect(Collectors.toList());
		}

		// get existing planning questions
		List<PCMMPlanningQuestion> dbPlanningQuestions = getDaoManager()
				.getRepository(IPCMMPlanningQuestionRepository.class).findAll();

		// get planning question analysis
		analysis.put(PCMMPlanningQuestion.class, getAppMgr().getService(IImportApplication.class)
				.analyzeImport(newPlanningQuestions, dbPlanningQuestions));

		return analysis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMSpecification(Model model, File pcmmSchemaFile) throws CredibilityException, IOException {

		if (pcmmSchemaFile != null && pcmmSchemaFile.exists()) {

			// load file
			PCMMSpecification pcmmSpecs = new YmlReaderPCMMSchema().load(pcmmSchemaFile);

			// import PCMM specs
			importPCMMConfiguration(model, pcmmSpecs);

			// add configuration file import history
			getAppMgr().getService(IGlobalApplication.class).addConfigurationFile(model, CFFeature.PCMM,
					pcmmSchemaFile.getPath());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMConfiguration(Model model, PCMMSpecification pcmmSpecs) throws CredibilityException {

		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (pcmmSpecs != null) {
			logger.info("Importing PCMM Specification into database..."); //$NON-NLS-1$

			// import PCMM options
			importPCMMOptionsWithPhases(pcmmSpecs.getPhases());

			// import PCMM roles
			importPCMMRoles(pcmmSpecs.getRoles());

			// import PCMM level colors
			if (pcmmSpecs.getLevelColors() != null) {
				importPCMMLevelColors(new ArrayList<>(pcmmSpecs.getLevelColors().values()));
			}

			// import PCMM elements
			importPCMMElements(model, pcmmSpecs.getElements());

			// import PCMM planning feature if present
			importPCMMPlanning(model, pcmmSpecs.getPlanningFields(), pcmmSpecs.getPlanningQuestions());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMRoles(List<Role> roles) throws CredibilityException {

		// import roles
		if (roles != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(roles,
					getAppMgr().getService(IPCMMApplication.class).getRoles());

			if (toAdd != null && !toAdd.isEmpty()) {

				logger.info("Importing PCMM Role into database..."); //$NON-NLS-1$

				for (Role role : toAdd.stream().map(Role.class::cast).collect(Collectors.toList())) {
					getAppMgr().getService(IPCMMApplication.class).addRole(role);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMLevelColors(List<PCMMLevelColor> levelColors) throws CredibilityException {

		// import roles
		if (levelColors != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(levelColors,
					getDaoManager().getRepository(IPCMMLevelColorRepository.class).findAll());

			if (toAdd != null && !toAdd.isEmpty()) {

				logger.info("Importing PCMM Level colors into database..."); //$NON-NLS-1$

				for (PCMMLevelColor levelColor : toAdd.stream().map(PCMMLevelColor.class::cast)
						.collect(Collectors.toList())) {
					getAppMgr().getService(IPCMMApplication.class).addLevelColor(levelColor);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMElements(Model model, List<PCMMElement> elements) throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		// import elements
		if (elements != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(elements,
					getAppMgr().getService(IPCMMApplication.class).getElementList(model));

			if (toAdd != null && !toAdd.isEmpty()) {

				logger.info("Importing PCMM elements into database..."); //$NON-NLS-1$

				for (PCMMElement element : toAdd.stream().map(PCMMElement.class::cast).collect(Collectors.toList())) {

					List<PCMMSubelement> subelementList = new ArrayList<>(element.getSubElementList());
					List<PCMMLevel> levelList = new ArrayList<>(element.getLevelList());

					// set element attributes
					element.setModel(model);
					element.setSubElementList(null);
					element.setLevelList(null);
					element.setEvidenceList(null);
					PCMMElement createdElement = getAppMgr().getService(IPCMMApplication.class).addElement(element);

					// create subelements
					importPCMMSubelements(subelementList, createdElement);

					// create levels (if PCMM Mode is simplified)
					importPCMMLevels(levelList, createdElement, null);

					getDaoManager().getRepository(IPCMMElementRepository.class).refresh(element);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMPlanning(Model model, List<PCMMPlanningParam> planningFields,
			Map<IAssessable, List<PCMMPlanningQuestion>> planningQuestions) throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL));
		}

		// import PCMM planning param
		importPCMMPlanningParam(model, planningFields);

		// import PCMM planning questions
		if (planningQuestions != null && !planningQuestions.isEmpty()) {
			importPCMMPlanningQuestions(model,
					planningQuestions.values().stream().flatMap(List::stream).collect(Collectors.toList()));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMPlanningParam(Model model, List<PCMMPlanningParam> planningFields)
			throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL));
		}

		// import elements
		if (planningFields != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(planningFields,
					getDaoManager().getRepository(IPCMMPlanningParamRepository.class).findAll());

			if (toAdd != null && !toAdd.isEmpty()) {

				logger.info("Importing PCMM Planning Fields into database..."); //$NON-NLS-1$

				getAppMgr().getService(IPCMMPlanningApplication.class).addAllPCMMPlanningParam(model,
						toAdd.stream().map(PCMMPlanningParam.class::cast).collect(Collectors.toList()));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMPlanningQuestions(Model model, List<PCMMPlanningQuestion> planningQuestions)
			throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_PCMMPLANNING_IMPORTCONF_MODELNULL));
		}

		// import elements
		if (planningQuestions != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(planningQuestions,
					getDaoManager().getRepository(IPCMMPlanningQuestionRepository.class).findAll());

			if (toAdd != null && !toAdd.isEmpty()) {

				logger.info("Importing PCMM Planning Fields into database..."); //$NON-NLS-1$

				getAppMgr().getService(IPCMMPlanningApplication.class).addAllPCMMPlanningQuestion(model,
						toAdd.stream().map(PCMMPlanningQuestion.class::cast).collect(Collectors.toList()));
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMOptionsWithPhases(List<PCMMPhase> phases) throws CredibilityException {

		// import PCMM Options
		if (phases != null) {

			// put phases in an option list
			List<PCMMOption> options = new ArrayList<>();
			for (PCMMPhase phase : phases) {
				PCMMOption option = new PCMMOption();
				option.setPhase(phase);
				options.add(option);
			}

			// import tbe options
			importPCMMOptions(options);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMOptions(List<PCMMOption> options) throws CredibilityException {

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(options,
				getDaoManager().getRepository(IPCMMOptionRepository.class).findAll());

		// import PCMM Options
		if (toAdd != null && !toAdd.isEmpty()) {

			logger.info("Importing PCMM options into database..."); //$NON-NLS-1$

			for (PCMMOption option : toAdd.stream().map(PCMMOption.class::cast).collect(Collectors.toList())) {
				getAppMgr().getService(IPCMMApplication.class).addPCMMOption(option);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMSubelements(List<PCMMSubelement> subelementList, PCMMElement createdElement)
			throws CredibilityException {

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(subelementList,
				getDaoManager().getRepository(IPCMMSubelementRepository.class).findAll());

		if (toAdd != null && !toAdd.isEmpty()) {

			logger.info("Importing PCMM subelements into database..."); //$NON-NLS-1$

			for (PCMMSubelement subelement : toAdd.stream().map(PCMMSubelement.class::cast)
					.collect(Collectors.toList())) {

				List<PCMMLevel> subelementlevelList = new ArrayList<>(subelement.getLevelList());

				// set element attributes
				subelement.setElement(createdElement);
				subelement.setEvidenceList(null);
				subelement.setLevelList(null);
				PCMMSubelement createdSubelement = getAppMgr().getService(IPCMMApplication.class)
						.addSubelement(subelement);

				// import levels
				importPCMMLevels(subelementlevelList, null, createdSubelement);

				getDaoManager().getRepository(IPCMMSubelementRepository.class).refresh(subelement);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMLevels(List<PCMMLevel> levelList, PCMMElement element, PCMMSubelement subelement)
			throws CredibilityException {

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(levelList,
				getDaoManager().getRepository(IPCMMLevelRepository.class).findAll());

		if (toAdd != null && !toAdd.isEmpty()) {

			logger.info("Importing PCMM levels into database..."); //$NON-NLS-1$

			for (PCMMLevel level : toAdd.stream().map(PCMMLevel.class::cast).collect(Collectors.toList())) {

				List<PCMMLevelDescriptor> levelDescList = new ArrayList<>(level.getLevelDescriptorList());

				// set level attributes
				level.setElement(element);
				level.setSubelement(subelement);
				level.setLevelDescriptorList(null);
				PCMMLevel createdLevel = getAppMgr().getService(IPCMMApplication.class).addLevel(level);

				// import level descriptors
				importPCMMLevelDescriptors(levelDescList, createdLevel);

				getDaoManager().getRepository(IPCMMLevelRepository.class).refresh(level);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importPCMMLevelDescriptors(List<PCMMLevelDescriptor> levelDescList, PCMMLevel level)
			throws CredibilityException {

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(levelDescList,
				getDaoManager().getRepository(IPCMMLevelDescRepository.class).findAll());

		if (toAdd != null && !toAdd.isEmpty()) {

			logger.info("Importing PCMM level descriptors into database..."); //$NON-NLS-1$

			for (PCMMLevelDescriptor levelDesc : toAdd.stream().map(PCMMLevelDescriptor.class::cast)
					.collect(Collectors.toList())) {

				// set level desc attributes
				levelDesc.setLevel(level);
				getAppMgr().getService(IPCMMApplication.class).addLevelDescriptor(levelDesc);
			}
		}
	}
}
