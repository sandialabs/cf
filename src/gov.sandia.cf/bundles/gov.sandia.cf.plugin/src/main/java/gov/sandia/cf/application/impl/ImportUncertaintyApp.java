/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.impl;

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
import gov.sandia.cf.application.IGlobalApplication;
import gov.sandia.cf.application.IImportApplication;
import gov.sandia.cf.application.IImportUncertaintyApp;
import gov.sandia.cf.application.IUncertaintyApplication;
import gov.sandia.cf.application.configuration.uncertainty.UncertaintySpecification;
import gov.sandia.cf.application.configuration.uncertainty.YmlReaderUncertaintySchema;
import gov.sandia.cf.dao.IUncertaintyConstraintRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.UncertaintyConstraint;
import gov.sandia.cf.model.UncertaintyParam;
import gov.sandia.cf.model.UncertaintySelectValue;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Application manager for methods that are specific to the import of
 * Uncertainties.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportUncertaintyApp extends AApplication implements IImportUncertaintyApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportUncertaintyApp.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateUncertaintyConfiguration(
			Model model, UncertaintySpecification currentSpecs, File schemaFile)
			throws CredibilityException, IOException {
		// Check errors
		if (schemaFile == null || !schemaFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_CONF_SCHEMAFILE_NOTEXISTS,
					RscTools.getString(RscConst.MSG_UNCERTAINTY)));
		}

		// Initialize
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = new HashMap<>();
		YmlReaderUncertaintySchema reader = new YmlReaderUncertaintySchema();

		// Get configuration
		UncertaintySpecification newSpecs = null;
		newSpecs = reader.load(schemaFile);

		// Analyze
		if (newSpecs != null) {
			// Uncertainty parameters
			List<UncertaintyParam> uncertaintyParameters = getDaoManager()
					.getRepository(IUncertaintyParamRepository.class).findAll();
			analysis.put(UncertaintyParam.class, getAppMgr().getService(IImportApplication.class)
					.analyzeImport(newSpecs.getParameters(), uncertaintyParameters));
		}

		return analysis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> void importUncertaintyChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {

		if (toImport != null && !toImport.isEmpty()) {

			/*
			 * Uncertainty Parameters
			 */
			List<UncertaintyParam> uncertaintyParamToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, UncertaintyParam.class, ImportActionType.TO_ADD);
			List<UncertaintyParam> uncertaintyParamToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, UncertaintyParam.class, ImportActionType.TO_DELETE);

			// import Uncertainty parameters
			importUncertaintyParam(model, uncertaintyParamToAdd);

			// delete Uncertainty parameters
			getAppMgr().getService(IUncertaintyApplication.class).deleteAllUncertaintyParam(uncertaintyParamToDelete);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importUncertaintySpecification(Model model, File uncertaintySchemaFile)
			throws CredibilityException, IOException {

		if (uncertaintySchemaFile != null && uncertaintySchemaFile.exists()) {

			UncertaintySpecification specs = new YmlReaderUncertaintySchema().load(uncertaintySchemaFile);

			// import Uncertainty configuration
			importUncertaintyConfiguration(model, specs);

			// add configuration file import history
			getAppMgr().getService(IGlobalApplication.class).addConfigurationFile(model, CFFeature.UNCERTAINTY,
					uncertaintySchemaFile.getPath());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importUncertaintyConfiguration(Model model, UncertaintySpecification specs)
			throws CredibilityException {

		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (specs != null) {
			logger.info("Importing Uncertainty specifications into database..."); //$NON-NLS-1$

			// import Uncertainty parameters
			importUncertaintyParam(model, specs.getParameters());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importUncertaintyParam(Model model, List<UncertaintyParam> uncertaintyParamList)
			throws CredibilityException {

		logger.info("Importing Uncertainty Parameters into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(uncertaintyParamList,
				getDaoManager().getRepository(IUncertaintyParamRepository.class).findAll());

		// For each parameters
		for (UncertaintyParam parameter : toAdd.stream().map(UncertaintyParam.class::cast)
				.collect(Collectors.toList())) {
			UncertaintyParam p = parameter.copy();
			p.setModel(model);
			UncertaintyParam parameterCreated = getAppMgr().getDaoManager()
					.getRepository(IUncertaintyParamRepository.class).create(p);

			// For each parameter values
			if (parameter.getParameterValueList() != null) {
				List<UncertaintySelectValue> selectValues = parameter.getParameterValueList().stream()
						.filter(param -> param instanceof UncertaintySelectValue)
						.map(UncertaintySelectValue.class::cast).collect(Collectors.toList());
				importUncertaintySelectValue(selectValues, parameterCreated);
			}

			// Import constraints
			if (parameter.getConstraintList() != null) {
				List<UncertaintyConstraint> constraints = parameter.getConstraintList().stream()
						.filter(constraint -> constraint instanceof UncertaintyConstraint)
						.map(UncertaintyConstraint.class::cast).collect(Collectors.toList());
				importUncertaintyConstraint(constraints, parameterCreated);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importUncertaintySelectValue(List<UncertaintySelectValue> uncertaintySelectValueList,
			UncertaintyParam uncertaintyParam) throws CredibilityException {

		if (uncertaintyParam != null && uncertaintySelectValueList != null && !uncertaintySelectValueList.isEmpty()) {

			logger.info("Importing Uncertainty Select values into database..."); //$NON-NLS-1$

			// get list to add
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericParameterSelectValue.Filter.PARAMETER, uncertaintyParam);
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(uncertaintySelectValueList,
					getDaoManager().getRepository(IUncertaintySelectValueRepository.class).findBy(filters));

			// For each parameter values
			for (UncertaintySelectValue parameterValue : toAdd.stream().map(UncertaintySelectValue.class::cast)
					.collect(Collectors.toList())) {
				UncertaintySelectValue pv = parameterValue.copy();
				pv.setParameter(uncertaintyParam);
				getAppMgr().getDaoManager().getRepository(IUncertaintySelectValueRepository.class).create(pv);
			}

			// refresh the parameter
			getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(uncertaintyParam);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importUncertaintyConstraint(List<UncertaintyConstraint> constraintList, UncertaintyParam param)
			throws CredibilityException {

		if (param != null) {

			logger.info("Importing Uncertainty Constraints into database..."); //$NON-NLS-1$

			// get list to add
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(GenericParameterConstraint.Filter.PARAMETER, param);
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(constraintList,
					getDaoManager().getRepository(IUncertaintyConstraintRepository.class).findBy(filters));

			// For each parameter values
			for (UncertaintyConstraint parameterValue : toAdd.stream().map(UncertaintyConstraint.class::cast)
					.collect(Collectors.toList())) {
				UncertaintyConstraint pv = parameterValue.copy();
				pv.setParameter(param);
				getAppMgr().getDaoManager().getRepository(IUncertaintyConstraintRepository.class).create(pv);
			}

			// refresh the parameter
			getDaoManager().getRepository(IUncertaintyParamRepository.class).refresh(param);
		}
	}
}
