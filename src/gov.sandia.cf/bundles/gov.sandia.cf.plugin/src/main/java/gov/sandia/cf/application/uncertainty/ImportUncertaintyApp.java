/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.application.uncertainty;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.AApplication;
import gov.sandia.cf.application.ApplicationManager;
import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.dao.IUncertaintyConstraintRepository;
import gov.sandia.cf.dao.IUncertaintyParamRepository;
import gov.sandia.cf.dao.IUncertaintyRepository;
import gov.sandia.cf.dao.IUncertaintySelectValueRepository;
import gov.sandia.cf.dao.IUncertaintyValueRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.GenericParameter;
import gov.sandia.cf.model.GenericParameterConstraint;
import gov.sandia.cf.model.GenericParameterSelectValue;
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
import gov.sandia.cf.model.dto.yml.YmlUncertaintyDataDto;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.model.query.EntityFilterBuilder;
import gov.sandia.cf.tools.DateTools;
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

	/** {@inheritDoc} */
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

		// Analyze specifications
		if (newSpecs != null) {
			// Uncertainty parameters
			List<UncertaintyParam> parameters = currentSpecs != null ? currentSpecs.getParameters() : null;
			analysis.put(UncertaintyParam.class, getAppMgr().getService(IImportApplication.class)
					.analyzeImport(newSpecs.getParameters(), parameters));
		}

		// Analyze data
		YmlUncertaintyDataDto uncertaintyData = new YmlReaderUncertaintyData().load(schemaFile);
		if (uncertaintyData != null && uncertaintyData.getUncertaintyGroups() != null
				&& !uncertaintyData.getUncertaintyGroups().isEmpty()) {
			// do not compare always add data
			analysis.put(Uncertainty.class, getAppMgr().getService(IImportApplication.class)
					.analyzeImport(uncertaintyData.getUncertaintyGroups(), null));
		}

		return analysis;
	}

	/** {@inheritDoc} */
	@Override
	public <M extends IImportable<M>> void importUncertaintyChanges(Model model, User user,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {

		if (toImport != null && !toImport.isEmpty()) {

			/*
			 * Uncertainty Parameters
			 */
			List<UncertaintyParam> uncertaintyParamToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, UncertaintyParam.class, ImportActionType.TO_ADD);
			List<UncertaintyParam> uncertaintyParamToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, UncertaintyParam.class, ImportActionType.TO_DELETE);
			List<UncertaintyParam> uncertaintyParamToUpdate = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, UncertaintyParam.class, ImportActionType.TO_UPDATE);

			// import Uncertainty parameters
			importUncertaintyParam(model, uncertaintyParamToAdd);
			importUpdateUncertaintyParam(model, uncertaintyParamToUpdate);

			// delete Uncertainty parameters
			getAppMgr().getService(IUncertaintyApplication.class).deleteAllUncertaintyParam(uncertaintyParamToDelete);

			/*
			 * Uncertainties
			 */
			List<Uncertainty> uncertaintiesToAdd = getAppMgr().getService(IImportApplication.class).getChanges(toImport,
					Uncertainty.class, ImportActionType.TO_ADD);
			List<Uncertainty> uncertaintiesToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, Uncertainty.class, ImportActionType.TO_DELETE);

			// import Uncertainties
			importUncertainties(model, user, null, uncertaintiesToAdd);

			// delete Uncertainties
			getAppMgr().getService(IUncertaintyApplication.class).deleteAllUncertainties(uncertaintiesToDelete, user);

		}
	}

	/** {@inheritDoc} */
	@Override
	public void importUncertaintySpecification(Model model, User user, File uncertaintySchemaFile)
			throws CredibilityException, IOException {

		if (uncertaintySchemaFile != null && uncertaintySchemaFile.exists()) {

			UncertaintySpecification specs = new YmlReaderUncertaintySchema().load(uncertaintySchemaFile);

			// import Uncertainty configuration
			importUncertaintyConfiguration(model, specs);

			// add configuration file import history
			getAppMgr().getService(IGlobalApplication.class).addConfigurationFile(model, user, CFFeature.UNCERTAINTY,
					uncertaintySchemaFile.getPath());
		}
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
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
						.filter(UncertaintySelectValue.class::isInstance).map(UncertaintySelectValue.class::cast)
						.collect(Collectors.toList());
				importUncertaintySelectValue(selectValues, parameterCreated);
			}

			// Import constraints
			if (parameter.getConstraintList() != null) {
				List<UncertaintyConstraint> constraints = parameter.getConstraintList().stream()
						.filter(UncertaintyConstraint.class::isInstance).map(UncertaintyConstraint.class::cast)
						.collect(Collectors.toList());
				importUncertaintyConstraint(constraints, parameterCreated);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void importUpdateUncertaintyParam(Model model, List<UncertaintyParam> uncertaintyParamList)
			throws CredibilityException {

		logger.info("Importing Uncertainty Parameters to update into database..."); //$NON-NLS-1$

		// For each parameters
		for (UncertaintyParam toImport : uncertaintyParamList) {

			// find
			UncertaintyParam toUpdate = findUncertaintyParamByName(toImport);

			if (toUpdate == null) {
				logger.debug("Uncertainty parameter with name {} not found. Imposssible to update.", //$NON-NLS-1$
						toImport != null ? toImport.getName() : RscTools.empty());
				continue;
			}

			// update fields
			toUpdate.setLevel(toImport.getLevel());
			toUpdate.setRequired(toImport.getRequired());
			toUpdate.setType(toImport.getType());
			toUpdate.setDefaultValue(toImport.getDefaultValue());

			getDaoManager().getRepository(IUncertaintyParamRepository.class).update(toUpdate);

			// For each parameter values
			if (toImport.getParameterValueList() != null) {

				// find select values
				List<UncertaintySelectValue> newSelectValues = toImport.getParameterValueList().stream()
						.filter(UncertaintySelectValue.class::isInstance).map(UncertaintySelectValue.class::cast)
						.collect(Collectors.toList());

				// recreate select values
				importUncertaintySelectValue(newSelectValues, toUpdate);
			}

			// Import constraints
			if (toImport.getConstraintList() != null) {

				// find constraints
				List<UncertaintyConstraint> newConstraints = toImport.getConstraintList().stream()
						.filter(UncertaintyConstraint.class::isInstance).map(UncertaintyConstraint.class::cast)
						.collect(Collectors.toList());

				// recreate constraints
				importUncertaintyConstraint(newConstraints, toUpdate);
			}
		}
	}

	/**
	 * Find uncertainty param by name.
	 *
	 * @param parameter the parameter
	 * @return the uncertainty param
	 */
	private UncertaintyParam findUncertaintyParamByName(UncertaintyParam parameter) {

		if (parameter == null || StringUtils.isBlank(parameter.getName())) {
			return null;
		}

		Map<EntityFilter, Object> filters = new HashMap<>();
		filters.put(GenericParameter.Filter.NAME, parameter.getName());
		List<UncertaintyParam> found = getDaoManager().getRepository(IUncertaintyParamRepository.class).findBy(filters);

		return found != null && !found.isEmpty() ? found.get(0) : null;
	}

	/** {@inheritDoc} */
	@Override
	public void importUncertaintySelectValue(List<UncertaintySelectValue> uncertaintySelectValueList,
			UncertaintyParam uncertaintyParam) throws CredibilityException {

		if (uncertaintyParam == null) {
			logger.warn("Impossible to import Uncertainty Select Values for an Uncertainty Parameter null"); //$NON-NLS-1$
			return;
		}

		if (uncertaintySelectValueList != null && !uncertaintySelectValueList.isEmpty()) {

			logger.info("Importing Uncertainty Select values into database..."); //$NON-NLS-1$

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(uncertaintySelectValueList,
					getDaoManager().getRepository(IUncertaintySelectValueRepository.class).findBy(
							EntityFilterBuilder.get(GenericParameterSelectValue.Filter.PARAMETER, uncertaintyParam)));

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

	/** {@inheritDoc} */
	@Override
	public void importUncertaintyConstraint(List<UncertaintyConstraint> constraintList, UncertaintyParam param)
			throws CredibilityException {

		if (param == null) {
			logger.warn("Impossible to import Uncertainty Constraint for an Uncertainty Parameter null"); //$NON-NLS-1$
			return;
		}

		logger.info("Importing Uncertainty Constraints into database..."); //$NON-NLS-1$

		// get list to add
		List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(constraintList,
				getDaoManager().getRepository(IUncertaintyConstraintRepository.class)
						.findBy(EntityFilterBuilder.get(GenericParameterConstraint.Filter.PARAMETER, param)));

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

	/** {@inheritDoc} */
	@Override
	public void importUncertaintyData(Model model, User user, File uncertaintyFile)
			throws CredibilityException, IOException {

		if (uncertaintyFile != null && uncertaintyFile.exists()) {

			YmlUncertaintyDataDto data = new YmlReaderUncertaintyData().load(uncertaintyFile);

			if (data != null && data.getUncertaintyGroups() != null) {

				// import Uncertainties
				Uncertainty parent = null;
				importUncertainties(model, user, parent, data.getUncertaintyGroups());

				// add configuration file import history
				getAppMgr().getService(IGlobalApplication.class).addConfigurationFile(model, user,
						CFFeature.UNCERTAINTY, uncertaintyFile.getPath());
			}

		}
	}

	/** {@inheritDoc} */
	@Override
	public void importUncertainties(Model model, User user, Uncertainty parent, List<Uncertainty> uncertaintyList)
			throws CredibilityException {

		logger.info("Importing Uncertainties into database..."); //$NON-NLS-1$

		if (user == null) {
			throw new CredibilityException(RscTools.getString(RscConst.ERR_IMPORT_UNCERTAINTY_APP_USER_NULL));
		}

		// For each value
		for (Uncertainty uncertainty : uncertaintyList) {

			// set values
			Uncertainty toCreate = new Uncertainty();
			toCreate.setModel(model);
			toCreate.setCreationDate(DateTools.getCurrentDate());
			toCreate.setUserCreation(user);
			toCreate.setName(uncertainty.getName());
			toCreate.setParent(parent);
			toCreate.setValues(null);

			Uncertainty created = getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class)
					.create(toCreate);

			// Import values
			if (uncertainty.getValues() != null) {
				importUncertaintyValues(created, user, uncertainty.getValues());
			}

			// import children
			if (uncertainty.getChildren() != null && !uncertainty.getChildren().isEmpty()) {
				importUncertainties(model, user, created, uncertainty.getChildren());
			}

			getAppMgr().getDaoManager().getRepository(IUncertaintyRepository.class).refresh(parent);
		}
	}

	/** {@inheritDoc} */
	@Override
	public void importUncertaintyValues(Uncertainty uncertainty, User user, List<UncertaintyValue> values)
			throws CredibilityException {

		if (uncertainty == null) {
			logger.warn("Impossible to import Uncertainty Values for an Uncertainty null"); //$NON-NLS-1$
			return;
		}

		if (uncertainty.getValues() != null && !values.isEmpty()) {

			logger.info("Importing Uncertainty values into database..."); //$NON-NLS-1$

			// For each value
			for (UncertaintyValue value : values) {

				// create value
				UncertaintyValue toCreate = new UncertaintyValue();

				// search parameter
				UncertaintyParam parameter = null;
				if (value.getParameter() != null && !StringUtils.isBlank(value.getParameter().getName())) {
					parameter = getAppMgr().getDaoManager().getRepository(IUncertaintyParamRepository.class)
							.findFirstByName(value.getParameter().getName());
				}

				if (parameter == null) {
					logger.warn("Impossible to import uncertainty value {}. Value parameter can not be found.", //$NON-NLS-1$
							value.getValue());
					continue;
				}

				// set values
				toCreate.setDateCreation(DateTools.getCurrentDate());
				toCreate.setUserCreation(user);
				toCreate.setParameter(parameter);
				toCreate.setUncertainty(uncertainty);

				// set value depending of the type
				String stringValue = getAppMgr().getService(IImportApplication.class)
						.getDatabaseValueForGeneric(parameter, value);

				toCreate.setValue(stringValue);

				// create
				getAppMgr().getDaoManager().getRepository(IUncertaintyValueRepository.class).create(toCreate);
			}

			// refresh the parameter
			getDaoManager().getRepository(IUncertaintyRepository.class).refresh(uncertainty);
		}
	}
}
