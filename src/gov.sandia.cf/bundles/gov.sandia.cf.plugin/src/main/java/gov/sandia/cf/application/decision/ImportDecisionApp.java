/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.decision;

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
import gov.sandia.cf.dao.IDecisionConstraintRepository;
import gov.sandia.cf.dao.IDecisionParamRepository;
import gov.sandia.cf.dao.IDecisionSelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.DecisionConstraint;
import gov.sandia.cf.model.DecisionParam;
import gov.sandia.cf.model.DecisionSelectValue;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.DecisionSpecification;
import gov.sandia.cf.model.query.EntityFilterBuilder;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Application manager for methods that are specific to the import of
 * Decision.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportDecisionApp extends AApplication implements IImportDecisionApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportDecisionApp.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> void importDecisionChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {

		if (toImport != null && !toImport.isEmpty()) {

			/*
			 * Decision Parameters
			 */
			List<DecisionParam> requirementsParamToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, DecisionParam.class, ImportActionType.TO_ADD);
			List<DecisionParam> requirementsParamToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, DecisionParam.class, ImportActionType.TO_DELETE);

			// import Decision parameters
			importDecisionParam(model, requirementsParamToAdd);

			// delete Decision parameters
			getAppMgr().getService(IDecisionApplication.class).deleteAllDecisionParam(requirementsParamToDelete);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateDecisionConfiguration(
			Model model, DecisionSpecification currentSpecs, File schemaFile) throws CredibilityException, IOException {
		// Check errors
		if (schemaFile == null || !schemaFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_CONF_SCHEMAFILE_NOTEXISTS,
					RscTools.getString(RscConst.MSG_DECISION)));
		}

		// Initialize
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = new HashMap<>();
		YmlReaderDecisionSchema reader = new YmlReaderDecisionSchema();

		// Get configuration
		DecisionSpecification newSpecs = reader.load(schemaFile);

		// Analyze
		if (newSpecs != null) {
			// Decision parameters
			List<DecisionParam> parameters = currentSpecs != null ? currentSpecs.getParameters() : null;
			analysis.put(DecisionParam.class, getAppMgr().getService(IImportApplication.class)
					.analyzeImport(newSpecs.getParameters(), parameters));
		}

		return analysis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importDecisionSpecification(Model model, User user, File schemaFile)
			throws CredibilityException, IOException {

		if (schemaFile != null && schemaFile.exists()) {
			DecisionSpecification pirtSpecs = new YmlReaderDecisionSchema().load(schemaFile);

			// import Decision configuration
			importDecisionConfiguration(model, pirtSpecs);

			// add configuration file import history
			getAppMgr().getService(IGlobalApplication.class).addConfigurationFile(model, user, CFFeature.DECISION,
					schemaFile.getPath());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importDecisionConfiguration(Model model, DecisionSpecification specs) throws CredibilityException {

		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (specs != null) {
			logger.info("Importing Decision specifications into database..."); //$NON-NLS-1$

			// import Decision parameters
			importDecisionParam(model, specs.getParameters());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importDecisionParam(Model model, List<DecisionParam> paramList) throws CredibilityException {

		logger.info("Importing Decision Parameters into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(paramList,
				getDaoManager().getRepository(IDecisionParamRepository.class).findAll());

		// For each parameters
		for (DecisionParam parameter : toAdd.stream().map(DecisionParam.class::cast).collect(Collectors.toList())) {
			DecisionParam p = parameter.copy();
			p.setModel(model);
			DecisionParam parameterCreated = getAppMgr().getDaoManager().getRepository(IDecisionParamRepository.class)
					.create(p);

			// Import select values
			if (parameter.getParameterValueList() != null) {
				List<DecisionSelectValue> selectValues = parameter.getParameterValueList().stream()
						.filter(DecisionSelectValue.class::isInstance).map(DecisionSelectValue.class::cast)
						.collect(Collectors.toList());
				importDecisionSelectValue(selectValues, parameterCreated);
			}

			// Import constraints
			if (parameter.getConstraintList() != null) {
				List<DecisionConstraint> constraints = parameter.getConstraintList().stream()
						.filter(DecisionConstraint.class::isInstance).map(DecisionConstraint.class::cast)
						.collect(Collectors.toList());
				importDecisionConstraint(constraints, parameterCreated);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importDecisionSelectValue(List<DecisionSelectValue> selectValueList, DecisionParam param)
			throws CredibilityException {

		if (param == null) {
			logger.warn("Impossible to import Decision Select Value for a Decision Parameter null"); //$NON-NLS-1$
			return;
		}
		logger.info("Importing Decision Select values into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(selectValueList,
				getDaoManager().getRepository(IDecisionSelectValueRepository.class)
						.findBy(EntityFilterBuilder.get(GenericParameterSelectValue.Filter.PARAMETER, param)));

		// For each parameter values
		for (DecisionSelectValue parameterValue : toAdd.stream().map(DecisionSelectValue.class::cast)
				.collect(Collectors.toList())) {
			DecisionSelectValue pv = parameterValue.copy();
			pv.setParameter(param);
			getAppMgr().getDaoManager().getRepository(IDecisionSelectValueRepository.class).create(pv);
		}

		// refresh the parameter
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(param);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importDecisionConstraint(List<DecisionConstraint> constraintList, DecisionParam param)
			throws CredibilityException {

		if (param == null) {
			logger.warn("Impossible to import Decision Constraint for a Decision Parameter null"); //$NON-NLS-1$
			return;
		}

		logger.info("Importing Decision Constraints into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(constraintList,
				getDaoManager().getRepository(IDecisionConstraintRepository.class)
						.findBy(EntityFilterBuilder.get(GenericParameterConstraint.Filter.PARAMETER, param)));

		// For each parameter values
		for (DecisionConstraint parameterValue : toAdd.stream().map(DecisionConstraint.class::cast)
				.collect(Collectors.toList())) {
			DecisionConstraint pv = parameterValue.copy();
			pv.setParameter(param);
			getAppMgr().getDaoManager().getRepository(IDecisionConstraintRepository.class).create(pv);
		}

		// refresh the parameter
		getDaoManager().getRepository(IDecisionParamRepository.class).refresh(param);
	}
}
