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
import gov.sandia.cf.application.IImportPIRTApp;
import gov.sandia.cf.application.IPIRTApplication;
import gov.sandia.cf.application.configuration.pirt.PIRTSpecification;
import gov.sandia.cf.application.configuration.pirt.YmlReaderPIRTSchema;
import gov.sandia.cf.dao.IPIRTAdequacyColumnGuidelineRepository;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.Model;
import gov.sandia.cf.model.PIRTAdequacyColumn;
import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;
import gov.sandia.cf.model.PIRTDescriptionHeader;
import gov.sandia.cf.model.PIRTLevelDifferenceColor;
import gov.sandia.cf.model.PIRTLevelImportance;
import gov.sandia.cf.model.query.EntityFilter;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Application manager for methods that are specific to the import of
 * PIRT.
 * 
 * @author Didier Verstraete
 * 
 */
public class ImportPIRTApp extends AApplication implements IImportPIRTApp {
	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportPIRTApp.class);

	/**
	 * ImportPIRTApp constructor
	 */
	public ImportPIRTApp() {
		super();
	}

	/**
	 * ImportPIRTApp constructor
	 * 
	 * @param appMgr the application manager
	 */
	public ImportPIRTApp(ApplicationManager appMgr) {
		super(appMgr);
	}

	/** {@inheritDoc} */
	@Override
	public <M extends IImportable<M>> void importPIRTChanges(Model model,
			Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toImport) throws CredibilityException {

		if (toImport != null && !toImport.isEmpty()) {

			/*
			 * PIRT Adequacy Column
			 */
			List<PIRTAdequacyColumn> adequacyColumnsToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTAdequacyColumn.class, ImportActionType.TO_ADD);
			List<PIRTAdequacyColumn> adequacyColumnsToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTAdequacyColumn.class, ImportActionType.TO_DELETE);

			// import PIRT Adequacy Columns
			importPIRTColumns(model, adequacyColumnsToAdd);

			// delete PIRT Adequacy Columns
			getAppMgr().getService(IPIRTApplication.class).deleteAllPIRTAdequacyColumn(adequacyColumnsToDelete);

			/*
			 * PIRT Description Header
			 */
			List<PIRTDescriptionHeader> headersToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTDescriptionHeader.class, ImportActionType.TO_ADD);
			List<PIRTDescriptionHeader> headersToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTDescriptionHeader.class, ImportActionType.TO_DELETE);

			// import PIRT Description Header
			importPIRTHeaders(model, headersToAdd);

			// delete PIRT Description Header
			getAppMgr().getService(IPIRTApplication.class).deleteAllPIRTDescriptionHeader(headersToDelete);

			/*
			 * PIRT Level Difference Color
			 */
			List<PIRTLevelDifferenceColor> colorsToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTLevelDifferenceColor.class, ImportActionType.TO_ADD);
			List<PIRTLevelDifferenceColor> colorsToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTLevelDifferenceColor.class, ImportActionType.TO_DELETE);

			// import PIRT Level Difference Color
			importPIRTColors(model, colorsToAdd);

			// delete PIRT Level Difference Color
			getAppMgr().getService(IPIRTApplication.class).deleteAllPIRTLevelDifferenceColor(colorsToDelete);

			/*
			 * PIRT Level Importance
			 */
			List<PIRTLevelImportance> levelsToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTLevelImportance.class, ImportActionType.TO_ADD);
			List<PIRTLevelImportance> levelsToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTLevelImportance.class, ImportActionType.TO_DELETE);

			// import PIRT Level Importance
			importPIRTLevels(model, levelsToAdd);

			// delete PIRT Level Importance
			getAppMgr().getService(IPIRTApplication.class).deleteAllPIRTLevelImportance(levelsToDelete);

			/*
			 * PIRT Adequacy Column Guideline
			 */
			List<PIRTAdequacyColumnGuideline> guidelinesToAdd = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTAdequacyColumnGuideline.class, ImportActionType.TO_ADD);
			List<PIRTAdequacyColumnGuideline> guidelinesToDelete = getAppMgr().getService(IImportApplication.class)
					.getChanges(toImport, PIRTAdequacyColumnGuideline.class, ImportActionType.TO_DELETE);

			// import PIRT Adequacy Column Guideline
			importPIRTGuidelines(guidelinesToAdd);

			// delete PIRT Adequacy Column Guideline
			getAppMgr().getService(IPIRTApplication.class).deleteAllPIRTAdequacyColumnGuideline(guidelinesToDelete);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends IImportable<M>> Map<Class<?>, Map<ImportActionType, List<?>>> analyzeUpdatePIRTConfiguration(
			Model model, PIRTSpecification currentSpecs, File pirtSchemaFile) throws CredibilityException, IOException {
		// Check errors
		if (pirtSchemaFile == null || !pirtSchemaFile.exists()) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_CONF_SCHEMAFILE_NOTEXISTS,
					RscTools.getString(RscConst.MSG_PIRT)));
		}

		// Initialize
		Map<Class<?>, Map<ImportActionType, List<?>>> analysis = new HashMap<>();
		YmlReaderPIRTSchema pcmmReader = new YmlReaderPIRTSchema();

		// Get configuration
		PIRTSpecification newSpecs = pcmmReader.load(pirtSchemaFile);

		// PIRT Adequacy columns
		List<PIRTAdequacyColumn> currentColumns = currentSpecs != null ? currentSpecs.getColumns() : null;
		analysis.put(PIRTAdequacyColumn.class,
				getAppMgr().getService(IImportApplication.class).analyzeImport(newSpecs.getColumns(), currentColumns));

		// PIRT Headers
		List<PIRTDescriptionHeader> currentHeaders = currentSpecs != null ? currentSpecs.getHeaders() : null;
		analysis.put(PIRTDescriptionHeader.class,
				getAppMgr().getService(IImportApplication.class).analyzeImport(newSpecs.getHeaders(), currentHeaders));

		// PIRT Level difference colors
		List<PIRTLevelDifferenceColor> currentColors = currentSpecs != null ? currentSpecs.getColors() : null;
		analysis.put(PIRTLevelDifferenceColor.class,
				getAppMgr().getService(IImportApplication.class).analyzeImport(newSpecs.getColors(), currentColors));

		// PIRT importance levels
		List<PIRTLevelImportance> currentLevels = currentSpecs != null && currentSpecs.getLevels() != null
				? new ArrayList<>(currentSpecs.getLevels().values())
				: null;
		analysis.put(PIRTLevelImportance.class, getAppMgr().getService(IImportApplication.class)
				.analyzeImport(new ArrayList<PIRTLevelImportance>(newSpecs.getLevels().values()), currentLevels));

		// PIRT column guidelines
		List<PIRTAdequacyColumnGuideline> currentGuidelines = currentSpecs != null
				? currentSpecs.getPirtAdequacyGuidelines()
				: null;
		analysis.put(PIRTAdequacyColumnGuideline.class, getAppMgr().getService(IImportApplication.class)
				.analyzeImport(newSpecs.getPirtAdequacyGuidelines(), currentGuidelines));

		return analysis;
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTSpecification(Model model, File pirtSchemaFile) throws CredibilityException, IOException {

		if (pirtSchemaFile != null && pirtSchemaFile.exists()) {
			PIRTSpecification pirtSpecs = new YmlReaderPIRTSchema().load(pirtSchemaFile);

			// import PIRTconfiguration
			importPIRTConfiguration(model, pirtSpecs);

			// add configuration file import history
			getAppMgr().getService(IGlobalApplication.class).addConfigurationFile(model, CFFeature.PIRT,
					pirtSchemaFile.getPath());
		}
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTConfiguration(Model model, PIRTSpecification pirtSpecs) throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (pirtSpecs != null) {
			logger.info("Importing PIRT Specification into database..."); //$NON-NLS-1$

			// import PIRT colors
			if (pirtSpecs.getColors() != null) {
				importPIRTColors(model, pirtSpecs.getColors());
			}

			// import PIRT columns
			if (pirtSpecs.getColumns() != null) {
				importPIRTColumns(model, pirtSpecs.getColumns());
			}

			// import PIRT headers
			if (pirtSpecs.getHeaders() != null) {
				importPIRTHeaders(model, pirtSpecs.getHeaders());
			}

			// import PIRT levels
			if (pirtSpecs.getLevels() != null) {
				importPIRTLevels(model, pirtSpecs.getLevels());
			}

			// import PIRT guidelines
			if (pirtSpecs.getPirtAdequacyGuidelines() != null) {
				importPIRTGuidelines(pirtSpecs.getPirtAdequacyGuidelines());
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTColors(Model model, List<PIRTLevelDifferenceColor> colors) throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (colors != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(colors,
					getAppMgr().getService(IPIRTApplication.class).getPIRTLevelDifferenceColor());

			if (toAdd != null && !toAdd.isEmpty()) {
				logger.info("Importing PIRT Colors into database..."); //$NON-NLS-1$

				for (PIRTLevelDifferenceColor color : toAdd.stream().map(PIRTLevelDifferenceColor.class::cast)
						.collect(Collectors.toList())) {
					getAppMgr().getService(IPIRTApplication.class).addPIRTLevelDifferenceColor(color);
				}
			}

		}
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTColumns(Model model, List<PIRTAdequacyColumn> columns) throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (columns != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(columns,
					getAppMgr().getService(IPIRTApplication.class).getPIRTAdequacyColumn());

			if (toAdd != null && !toAdd.isEmpty()) {

				logger.info("Importing PIRT Columns into database..."); //$NON-NLS-1$

				for (PIRTAdequacyColumn column : toAdd.stream().map(PIRTAdequacyColumn.class::cast)
						.collect(Collectors.toList())) {
					getAppMgr().getService(IPIRTApplication.class).addPIRTAdequacyColumn(column);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTHeaders(Model model, List<PIRTDescriptionHeader> headers) throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (headers != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(headers,
					getAppMgr().getService(IPIRTApplication.class).getPIRTDescriptionHeader());

			if (toAdd != null && !toAdd.isEmpty()) {

				logger.info("Importing PIRT Headers into database..."); //$NON-NLS-1$

				for (PIRTDescriptionHeader header : toAdd.stream().map(PIRTDescriptionHeader.class::cast)
						.collect(Collectors.toList())) {
					getAppMgr().getService(IPIRTApplication.class).addPIRTDescriptionHeader(header);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTLevels(Model model, Map<String, PIRTLevelImportance> levels) throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (levels != null) {

			// get list to add
			importPIRTLevels(model, new ArrayList<>(levels.values()));
		}
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTLevels(Model model, List<PIRTLevelImportance> levels) throws CredibilityException {
		if (model == null) {
			throw new CredibilityException(RscTools.getString(RscConst.EX_IMPORTAPP_MODELNULL));
		}

		if (levels != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(levels,
					getAppMgr().getService(IPIRTApplication.class).getPIRTLevelImportance());

			if (toAdd != null && !toAdd.isEmpty()) {

				logger.info("Importing PIRT Levels into database..."); //$NON-NLS-1$

				for (PIRTLevelImportance value : toAdd.stream().map(PIRTLevelImportance.class::cast)
						.collect(Collectors.toList())) {
					getAppMgr().getService(IPIRTApplication.class).addPIRTLevelImportance(value);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTGuidelines(List<PIRTAdequacyColumnGuideline> columnGuidelines) throws CredibilityException {

		if (columnGuidelines != null) {

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(columnGuidelines,
					getAppMgr().getService(IPIRTApplication.class).getPIRTAdequacyColumnGuideline());

			logger.info("Importing PIRT Guidelines into database..."); //$NON-NLS-1$

			for (PIRTAdequacyColumnGuideline value : columnGuidelines) {

				// get level guidelines
				List<PIRTAdequacyColumnLevelGuideline> levelGuidelines = value.getLevelGuidelines();

				// add column guideline
				if (toAdd != null && toAdd.contains(value)) {
					value.setLevelGuidelines(null);
					getAppMgr().getService(IPIRTApplication.class).addPIRTAdequacyColumnGuideline(value);
				}

				if (levelGuidelines != null) {
					importPIRTLevelGuidelines(levelGuidelines);
				}
			}

		}
	}

	/** {@inheritDoc} */
	@Override
	public void importPIRTLevelGuidelines(List<PIRTAdequacyColumnLevelGuideline> levelGuidelines)
			throws CredibilityException {

		if (levelGuidelines != null) {

			logger.info("Importing PIRT Level Guidelines into database..."); //$NON-NLS-1$

			// get list to add
			List<?> toAdd = getAppMgr().getService(IImportApplication.class).getChangesToAdd(levelGuidelines,
					getAppMgr().getService(IPIRTApplication.class).getPIRTAdequacyColumnLevelGuideline());

			if (toAdd != null && !toAdd.isEmpty()) {
				for (PIRTAdequacyColumnLevelGuideline levelGuideline : toAdd.stream()
						.map(PIRTAdequacyColumnLevelGuideline.class::cast).collect(Collectors.toList())) {

					// get column guideline
					PIRTAdequacyColumnGuideline pirtGuideline = findPIRTGuidelines(
							levelGuideline.getAdequacyColumnGuideline());

					if (pirtGuideline != null) {
						// set column guideline
						levelGuideline.setAdequacyColumnGuideline(pirtGuideline);

						// add level guideline
						getAppMgr().getService(IPIRTApplication.class)
								.addPIRTAdequacyColumnLevelGuideline(levelGuideline);

					} else {
						logger.warn(
								"PIRT Level Guideline {} can not be imported, PIRT column guideline associated not found", //$NON-NLS-1$
								levelGuideline.getAbstract());
					}
				}
			}
		}
	}

	/**
	 * @param guidelineToFind the pirt guideline column to find
	 * @return the pirt guideline column to find if found, otherwise null
	 */
	private PIRTAdequacyColumnGuideline findPIRTGuidelines(PIRTAdequacyColumnGuideline guidelineToFind) {

		PIRTAdequacyColumnGuideline pirtGuideline = null;

		if (guidelineToFind != null) {
			Map<EntityFilter, Object> filters = new HashMap<>();
			filters.put(PIRTAdequacyColumnGuideline.Filter.NAME, guidelineToFind.getName());
			filters.put(PIRTAdequacyColumnGuideline.Filter.DESCRIPTION, guidelineToFind.getDescription());
			List<PIRTAdequacyColumnGuideline> findBy = getDaoManager()
					.getRepository(IPIRTAdequacyColumnGuidelineRepository.class).findBy(filters);
			pirtGuideline = findBy != null && !findBy.isEmpty() ? findBy.get(0) : null;
		}

		return pirtGuideline;
	}
}
