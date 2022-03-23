/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.application.decision.IImportDecisionApp;
import gov.sandia.cf.application.global.IGlobalApplication;
import gov.sandia.cf.application.imports.IImportApplication;
import gov.sandia.cf.application.pcmm.IImportPCMMApp;
import gov.sandia.cf.application.pirt.IImportPIRTApp;
import gov.sandia.cf.application.qoiplanning.IImportQoIPlanningApp;
import gov.sandia.cf.application.requirement.IImportSysRequirementApp;
import gov.sandia.cf.application.uncertainty.IImportUncertaintyApp;
import gov.sandia.cf.exceptions.CredibilityException;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.model.ImportActionType;
import gov.sandia.cf.model.ImportSchema;
import gov.sandia.cf.parts.dialogs.importation.ImportDialog;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Configuration view controller: Used to control the Import
 * Configuration view
 * 
 * @author Didier Verstraete
 *
 */
public class ImportConfigurationViewController {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportConfigurationViewController.class);

	/**
	 * The view
	 */
	private ImportConfigurationView view;

	ImportConfigurationViewController(ImportConfigurationView view) {
		Assert.isNotNull(view);
		this.view = view;
	}

	/**
	 * Ask the user for the changes to apply and import the approved changes for the
	 * new schema.
	 * 
	 * @throws CredibilityException
	 */
	boolean importSchema(Map<Class<?>, Map<ImportActionType, List<?>>> analysis, ImportSchema importSchema)
			throws CredibilityException {

		boolean changed = false;

		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = null;

		// open import dialog
		ImportDialog importDlg = new ImportDialog(view.getViewManager(), view.getShell(), analysis, importSchema);
		toChange = importDlg.openDialog();

		if (toChange != null) {

			if (toChange.isEmpty()) {

				// Inform nothing to change
				MessageDialog.openWarning(view.getShell(), RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
						RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_NOTHING));

			} else {

				// import approved changes in the database
				view.getViewManager().getAppManager().getService(IImportApplication.class).importChanges(
						view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser(),
						toChange);

				// reload configuration
				view.getViewManager().getCredibilityEditor().reloadConfiguration();

				// save changes
				view.getViewManager().viewChanged();

				view.getViewManager().reload();

				// Inform success
				MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
						RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_SUCCESS));

				changed = true;
			}
		}

		return changed;
	}

	/**
	 * Ask the user for the changes to apply and import the approved changes for the
	 * new QoI Planning schema.
	 */
	void importQoIPlanningSchema() {

		// check save need before import
		if (!continueIfSaveNeeded()) {
			return;
		}

		String schemaPath = view.getTextQoIPlanningSchemaPath();
		File schemaFile = view.getTextQoIPlanningSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = view.getViewManager().getAppManager()
					.getService(IImportQoIPlanningApp.class).analyzeUpdateQoIPlanningConfiguration(schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.QOI_PLANNING);

			// add configuration file import history
			if (changed)
				view.getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser(),
						CFFeature.QOI_PLANNER, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_IMPORTVIEW_IMPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Ask the user for the changes to apply and import the approved changes for the
	 * new PIRT schema.
	 */
	void importPIRTSchema() {

		// check save need before import
		if (!continueIfSaveNeeded()) {
			return;
		}

		String schemaPath = view.getTextPIRTSchemaPath();
		File schemaFile = view.getTextPIRTSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = view.getViewManager().getAppManager()
					.getService(IImportPIRTApp.class)
					.analyzeUpdatePIRTConfiguration(view.getViewManager().getCache().getModel(),
							view.getViewManager().getPIRTConfiguration(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.PIRT);

			// add configuration file import history
			if (changed)
				view.getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser(),
						CFFeature.PIRT, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_IMPORTVIEW_IMPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Ask the user for the changes to apply and import the approved changes for the
	 * new PCMM schema.
	 */
	void importPCMMSchema() {

		// check save need before import
		if (!continueIfSaveNeeded()) {
			return;
		}

		String schemaPath = view.getTextPCMMSchemaPath();
		File schemaFile = view.getTextPCMMSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = view.getViewManager().getAppManager()
					.getService(IImportPCMMApp.class)
					.analyzeUpdatePCMMConfiguration(view.getViewManager().getCache().getModel(),
							view.getViewManager().getPCMMConfiguration(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.PCMM);

			// add configuration file import history
			if (changed)
				view.getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser(),
						CFFeature.PCMM, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_IMPORTVIEW_IMPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Ask the user for the changes to apply and import the approved changes for the
	 * new Communicate schema.
	 */
	void importUncertaintySchema() {

		// check save need before import
		if (!continueIfSaveNeeded()) {
			return;
		}

		String schemaPath = view.getTextUncertaintySchemaPath();
		File schemaFile = view.getTextUncertaintySchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = view.getViewManager().getAppManager()
					.getService(IImportUncertaintyApp.class)
					.analyzeUpdateUncertaintyConfiguration(view.getViewManager().getCache().getModel(),
							view.getViewManager().getUncertaintyConfiguration(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.UNCERTAINTY);

			// add configuration file import history
			if (changed)
				view.getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser(),
						CFFeature.UNCERTAINTY, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_IMPORTVIEW_IMPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Ask the user for the changes to apply and import the approved changes for the
	 * new System Requirements schema.
	 */
	void importRequirementSchema() {

		// check save need before import
		if (!continueIfSaveNeeded()) {
			return;
		}

		String schemaPath = view.getTextRequirementsSchemaPath();
		File schemaFile = view.getTextRequirementsSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = view.getViewManager().getAppManager()
					.getService(IImportSysRequirementApp.class)
					.analyzeUpdateRequirementsConfiguration(view.getViewManager().getCache().getModel(),
							view.getViewManager().getSystemRequirementConfiguration(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.SYSTEM_REQUIREMENTS);

			// add configuration file import history
			if (changed)
				view.getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser(),
						CFFeature.SYSTEM_REQUIREMENTS, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_IMPORTVIEW_IMPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Ask the user for the changes to apply and import the approved changes for the
	 * new Decision schema.
	 */
	void importDecisionSchema() {

		// check save need before import
		if (!continueIfSaveNeeded()) {
			return;
		}

		String schemaPath = view.getTextDecisionSchemaPath();
		File schemaFile = view.getTextDecisionSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = view.getViewManager().getAppManager()
					.getService(IImportDecisionApp.class)
					.analyzeUpdateDecisionConfiguration(view.getViewManager().getCache().getModel(),
							view.getViewManager().getCache().getDecisionSpecification(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.DECISION);

			// add configuration file import history
			if (changed)
				view.getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						view.getViewManager().getCache().getModel(), view.getViewManager().getCache().getUser(),
						CFFeature.DECISION, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(view.getShell(), RscTools.getString(RscConst.ERROR_TITLE),
					RscTools.getString(RscConst.ERR_CONF_IMPORTVIEW_IMPORT_ERROR_OCCURED)
							+ RscTools.getString(RscConst.CARRIAGE_RETURN) + e.getMessage());
		}
	}

	/**
	 * Check save need
	 * 
	 * @return true if the user wants to save and continue, otherwise false.
	 */
	private boolean continueIfSaveNeeded() {
		try {
			// check save need before import
			checkSaveNeed();
			return true;
		} catch (Exception e) {
			// user cancelled
			return false;
		}
	}

	/**
	 * Check save need
	 */
	private void checkSaveNeed() throws CredibilityException {

		// check save need
		if (view.getViewManager().isDirty()) {
			boolean openConfirm = MessageDialog.openConfirm(view.getShell(),
					RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_NEEDSAVE));
			if (openConfirm) {
				view.getViewManager().doSave();
			} else {
				MessageDialog.openInformation(view.getShell(), RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
						RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_CANCELLED));
				throw new CredibilityException(RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_CANCELLED));
			}
		}
	}

}
