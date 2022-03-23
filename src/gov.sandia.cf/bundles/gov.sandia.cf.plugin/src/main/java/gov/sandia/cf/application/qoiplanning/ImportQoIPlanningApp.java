/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.qoiplanning;

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
import gov.sandia.cf.dao.IQoIPlanningConstraintRepository;
import gov.sandia.cf.dao.IQoIPlanningParamRepository;
import gov.sandia.cf.dao.IQoIPlanningSelectValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.QoIPlanningConstraint;
import gov.sandia.cf.model.QoIPlanningParam;
import gov.sandia.cf.model.QoIPlanningSelectValue;
import gov.sandia.cf.model.User;
import gov.sandia.cf.model.dto.configuration.QoIPlanningSpecification;
import gov.sandia.cf.model.query.EntityFilterBuilder;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Application manager for methods that are specific to the import of QoI
 * Planning.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportQoIPlanningApp extends AApplication implements IImportQoIPlanningApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportQoIPlanningApp.class);

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> void importQoIPlanningChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {

		if (toImport != null && !toImport.isEmpty()) {

			/*
			 * QoI Planning Parameters
			 */
			List<QoIPlanningParam> requirementsParamToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, QoIPlanningParam.class, ImportActionType.TO_ADD);
			List<QoIPlanningParam> requirementsParamToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, QoIPlanningParam.class, ImportActionType.TO_DELETE);

			// import QoI Planning parameters
			importQoIPlanningParam(model, requirementsParamToAdd);

			// delete QoI Planning parameters
			getAppMgr().getService(IQoIPlanningApplication.class).deleteAllQoIPlanningParam(requirementsParamToDelete);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdateQoIPlanningConfiguration(
			File schemaFile) throws CredibilityException, IOException {
		// Check errors
		if (schemaFile == null || !schemaFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_CONF_SCHEMAFILE_NOTEXISTS,
					RscTools.getString(RscConst.MSG_QOIPLANNING)));
		}

		// Initialize
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = new HashMap<>();
		YmlReaderQoIPlanningSchema reader = new YmlReaderQoIPlanningSchema();

		// Get configuration
		QoIPlanningSpecification newSpecs = reader.load(schemaFile);

		// Analyze
		if (newSpecs != null) {
			// QoI Planning parameters
			List<QoIPlanningParam> uncertaintyParameters = getDaoManager()
					.getRepository(IQoIPlanningParamRepository.class).findAll();
			analysis.put(QoIPlanningParam.class, getAppMgr().getService(IImportApplication.class)
					.analyzeImport(newSpecs.getParameters(), uncertaintyParameters));
		}

		return analysis;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importQoIPlanningSpecification(Model model, User user, File schemaFile)
			throws CredibilityException, IOException {

		if (schemaFile != null && schemaFile.exists()) {
			QoIPlanningSpecification pirtSpecs = new YmlReaderQoIPlanningSchema().load(schemaFile);

			// import QoIPlanning configuration
			importQoIPlanningConfiguration(model, pirtSpecs);

			// add configuration file import history
			getAppMgr().getService(IGlobalApplication.class).addConfigurationFile(model, user, CFFeature.QOI_PLANNER,
					schemaFile.getPath());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importQoIPlanningConfiguration(Model model, QoIPlanningSpecification specs)
			throws CredibilityException {

		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (specs != null) {
			logger.info("Importing QoI Planning specifications into database..."); //$NON-NLS-1$

			// import QoIPlanning parameters
			importQoIPlanningParam(model, specs.getParameters());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importQoIPlanningParam(Model model, List<QoIPlanningParam> paramList) throws CredibilityException {

		logger.info("Importing QoIPlanning Parameters into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(paramList,
				getDaoManager().getRepository(IQoIPlanningParamRepository.class).findAll());

		// For each parameters
		for (QoIPlanningParam parameter : toAdd.stream().map(QoIPlanningParam.class::cast)
				.collect(Collectors.toList())) {
			QoIPlanningParam p = parameter.copy();
			p.setModel(model);
			QoIPlanningParam parameterCreated = getAppMgr().getDaoManager()
					.getRepository(IQoIPlanningParamRepository.class).create(p);

			// Import select values
			if (parameter.getParameterValueList() != null) {
				List<QoIPlanningSelectValue> selectValues = parameter.getParameterValueList().stream()
						.filter(QoIPlanningSelectValue.class::isInstance).map(QoIPlanningSelectValue.class::cast)
						.collect(Collectors.toList());
				importQoIPlanningSelectValue(selectValues, parameterCreated);
			}

			// Import constraints
			if (parameter.getConstraintList() != null) {
				List<QoIPlanningConstraint> constraints = parameter.getConstraintList().stream()
						.filter(QoIPlanningConstraint.class::isInstance).map(QoIPlanningConstraint.class::cast)
						.collect(Collectors.toList());
				importQoIPlanningConstraint(constraints, parameterCreated);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importQoIPlanningSelectValue(List<QoIPlanningSelectValue> selectValueList, QoIPlanningParam param)
			throws CredibilityException {

		if (param == null) {
			logger.warn("Impossible to import QoI Planning Select Value for a QoI Planning Param null"); //$NON-NLS-1$
			return;
		}

		logger.info("Importing QoI Planning Select values into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(selectValueList,
				getDaoManager().getRepository(IQoIPlanningSelectValueRepository.class)
						.findBy(EntityFilterBuilder.get(GenericParameterSelectValue.Filter.PARAMETER, param)));

		// For each parameter values
		for (QoIPlanningSelectValue parameterValue : toAdd.stream().map(QoIPlanningSelectValue.class::cast)
				.collect(Collectors.toList())) {
			QoIPlanningSelectValue pv = parameterValue.copy();
			pv.setParameter(param);
			getAppMgr().getDaoManager().getRepository(IQoIPlanningSelectValueRepository.class).create(pv);
		}

		// refresh the parameter
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(param);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void importQoIPlanningConstraint(List<QoIPlanningConstraint> constraintList, QoIPlanningParam param)
			throws CredibilityException {

		if (param == null) {
			logger.warn("Impossible to import QoI Planning Constraint for a QoI Planning Param null"); //$NON-NLS-1$
			return;
		}

		logger.info("Importing QoI Planning Constraints into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(constraintList,
				getDaoManager().getRepository(IQoIPlanningConstraintRepository.class)
						.findBy(EntityFilterBuilder.get(GenericParameterConstraint.Filter.PARAMETER, param)));

		// For each parameter values
		for (QoIPlanningConstraint parameterValue : toAdd.stream().map(QoIPlanningConstraint.class::cast)
				.collect(Collectors.toList())) {
			QoIPlanningConstraint pv = parameterValue.copy();
			pv.setParameter(param);
			getAppMgr().getDaoManager().getRepository(IQoIPlanningConstraintRepository.class).create(pv);
		}

		// refresh the parameter
		getDaoManager().getRepository(IQoIPlanningParamRepository.class).refresh(param);
	}
}
