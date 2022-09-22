/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.requirement;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.dao.ISystemRequirementConstraintRepository;
import gov.sandia.cf.dao.ISystemRequirementParamRepository;
import gov.sandia.cf.dao.ISystemRequirementSelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.SystemRequirementConstraint;
import gov.sandia.cf.model.SystemRequirementParam;
import gov.sandia.cf.model.SystemRequirementSelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.SystemRequirementSpecification;
import gov.sandia.cf.model.query.EntityFilterBuilder;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Application manager for methods that are specific to the import of
 * System Requirements.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportSysRequirementApp extends AApplication implements IImportSysRequirementApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportSysRequirementApp.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> void importSysRequirementChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {

		if (toImport != null && !toImport.isEmpty()) {

			/*
			 * System Requirement Parameters
			 */
			List<SystemRequirementParam> requirementsParamToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, SystemRequirementParam.class, ImportActionType.TO_ADD);
			List<SystemRequirementParam> requirementsParamToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, SystemRequirementParam.class, ImportActionType.TO_DELETE);

			// import System Requirement parameters
			importSysRequirementParam(model, requirementsParamToAdd);

			// delete System Requirement parameters
			getAppMgr().getService(ISystemRequirementApplication.class)
					.deleteAllRequirementParam(requirementsParamToDelete);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateRequirementsConfiguration(
			Model model, SystemRequirementSpecification currentSpecs, File schemaFile)
			throws CredibilityException, IOException {
		// Check errors
		if (schemaFile == null || !schemaFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_CONF_SCHEMAFILE_NOTEXISTS,
					RscTools.getString(RscConst.MSG_SYSREQUIREMENT)));
		}

		// Initialize
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = new HashMap<>();
		YmlReaderSystemRequirementSchema reader = new YmlReaderSystemRequirementSchema();

		// Get configuration
		SystemRequirementSpecification newSpecs = reader.load(schemaFile);

		// Analyze
		if (newSpecs != null) {
			// System Requirements parameters
			List<SystemRequirementParam> parameters = currentSpecs != null ? currentSpecs.getParameters() : null;
			analysis.put(SystemRequirementParam.class, getAppMgr().getService(IImportApplication.class)
					.analyzeImport(newSpecs.getParameters(), parameters));
		}

		return analysis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importSysRequirementSpecification(Model model, User user, File requirementSchemaFile)
			throws CredibilityException, IOException {

		if (requirementSchemaFile != null && requirementSchemaFile.exists()) {

			SystemRequirementSpecification specs = new YmlReaderSystemRequirementSchema().load(requirementSchemaFile);

			// import System Requirement configuration
			importSysRequirementConfiguration(model, specs);

			// add configuration file import history
			getAppMgr().getService(IGlobalApplication.class).addConfigurationFile(model, user,
					CFFeature.SYSTEM_REQUIREMENTS, requirementSchemaFile.getPath());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importSysRequirementConfiguration(Model model, SystemRequirementSpecification specs)
			throws CredibilityException {

		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (specs != null) {
			logger.info("Importing System Requirement specifications into database..."); //$NON-NLS-1$

			// import Requirement parameters
			importSysRequirementParam(model, specs.getParameters());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importSysRequirementParam(Model model, List<SystemRequirementParam> requirementParamList)
			throws CredibilityException {

		logger.info("Importing Requirement Parameters into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(requirementParamList,
				getDaoManager().getRepository(ISystemRequirementParamRepository.class).findAll());

		// For each parameters
		for (SystemRequirementParam parameter : toAdd.stream().map(SystemRequirementParam.class::cast)
				.collect(Collectors.toList())) {
			SystemRequirementParam p = parameter.copy();
			p.setModel(model);
			SystemRequirementParam parameterCreated = getAppMgr().getDaoManager()
					.getRepository(ISystemRequirementParamRepository.class).create(p);

			// For each parameter values
			if (parameter.getParameterValueList() != null) {
				List<SystemRequirementSelectValue> selectValues = parameter.getParameterValueList().stream()
						.filter(SystemRequirementSelectValue.class::isInstance)
						.map(SystemRequirementSelectValue.class::cast).collect(Collectors.toList());
				importSysRequirementSelectValue(selectValues, parameterCreated);
			}

			// Import constraints
			if (parameter.getConstraintList() != null) {
				List<SystemRequirementConstraint> constraints = parameter.getConstraintList().stream()
						.filter(SystemRequirementConstraint.class::isInstance)
						.map(SystemRequirementConstraint.class::cast).collect(Collectors.toList());
				importSysRequirementConstraint(constraints, parameterCreated);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importSysRequirementSelectValue(List<SystemRequirementSelectValue> requirementSelectValueList,
			SystemRequirementParam requirementParam) throws CredibilityException {

		if (requirementParam == null) {
			logger.warn("Impossible to import System Requirement Select Value for a System Requirement Parameter null"); //$NON-NLS-1$
			return;
		}

		logger.info("Importing Uncertainty Select values into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(requirementSelectValueList,
				getDaoManager().getRepository(ISystemRequirementSelectValueRepository.class).findBy(
						EntityFilterBuilder.get(GenericParameterSelectValue.Filter.PARAMETER, requirementParam)));

		// For each parameter values
		for (SystemRequirementSelectValue parameterValue : toAdd.stream().map(SystemRequirementSelectValue.class::cast)
				.collect(Collectors.toList())) {
			SystemRequirementSelectValue pv = parameterValue.copy();
			pv.setParameter(requirementParam);
			getAppMgr().getDaoManager().getRepository(ISystemRequirementSelectValueRepository.class).create(pv);
		}

		// refresh the parameter
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(requirementParam);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importSysRequirementConstraint(List<SystemRequirementConstraint> constraintList,
			SystemRequirementParam param) throws CredibilityException {

		if (param == null) {
			logger.warn("Impossible to import System Requirement Constraint for a System Requirement Parameter null"); //$NON-NLS-1$
			return;
		}

		logger.info("Importing System Requirement Constraints into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(constraintList,
				getDaoManager().getRepository(ISystemRequirementConstraintRepository.class)
						.findBy(EntityFilterBuilder.get(GenericParameterConstraint.Filter.PARAMETER, param)));

		// For each parameter values
		for (SystemRequirementConstraint parameterValue : toAdd.stream().map(SystemRequirementConstraint.class::cast)
				.collect(Collectors.toList())) {
			SystemRequirementConstraint pv = parameterValue.copy();
			pv.setParameter(param);
			getAppMgr().getDaoManager().getRepository(ISystemRequirementConstraintRepository.class).create(pv);
		}

		// refresh the parameter
		getDaoManager().getRepository(ISystemRequirementParamRepository.class).refresh(param);
	}
}
