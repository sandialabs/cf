/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.configuration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
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
import gov.sandia.cf.parts.ui.AViewController;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * Import Configuration view controller: Used to control the Import
 * Configuration view
 * 
 * @author Didier Verstraete
 *
 */
public class ImportConfigurationViewController
		extends AViewController<ConfigurationViewManager, ImportConfigurationView> {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ImportConfigurationViewController.class);

	/**
	 * Instantiates a new import configuration view controller.
	 *
	 * @param viewManager the view manager
	 * @param parent      the parent
	 */
	ImportConfigurationViewController(ConfigurationViewManager viewManager, Composite parent) {
		super(viewManager);
		super.setView(new ImportConfigurationView(this, parent, SWT.NONE));
	}

	/**
	 * Ask the user for the changes to apply and import the approved changes for the
	 * new schema.
	 *
	 * @param analysis     the analysis
	 * @param importSchema the import schema
	 * @return true, if successful
	 * @throws CredibilityException the credibility exception
	 */
	boolean importSchema(Map<Class<?>, Map<ImportActionType, List<?>>> analysis, ImportSchema importSchema)
			throws CredibilityException {

		boolean changed = false;

		Map<Class<?>, Map<ImportActionType, List<IImportable<?>>>> toChange = null;

		// open import dialog
		ImportDialog importDlg = new ImportDialog(getViewManager(), getView().getShell(), analysis, importSchema);
		toChange = importDlg.openDialog();

		if (toChange != null) {

			if (toChange.isEmpty()) {

				// Inform nothing to change
				MessageDialog.openWarning(getView().getShell(), RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
						RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_NOTHING));

			} else {

				// import approved changes in the database
				getViewManager().getAppManager().getService(IImportApplication.class).importChanges(
						getViewManager().getCache().getModel(), getViewManager().getCache().getUser(), toChange);

				// reload configuration
				getViewManager().getCredibilityEditor().reloadConfiguration();

				// save changes
				getViewManager().viewChanged();

				getViewManager().reload();

				// Inform success
				MessageDialog.openInformation(getView().getShell(),
						RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
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

		String schemaPath = getView().getTextQoIPlanningSchemaPath();
		File schemaFile = getView().getTextQoIPlanningSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getViewManager().getAppManager()
					.getService(IImportQoIPlanningApp.class).analyzeUpdateQoIPlanningConfiguration(schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.QOI_PLANNING);

			// add configuration file import history
			if (changed)
				getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						getViewManager().getCache().getModel(), getViewManager().getCache().getUser(),
						CFFeature.QOI_PLANNER, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		String schemaPath = getView().getTextPIRTSchemaPath();
		File schemaFile = getView().getTextPIRTSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getViewManager().getAppManager()
					.getService(IImportPIRTApp.class)
					.analyzeUpdatePIRTConfiguration(getViewManager().getCache().getModel(),
							getViewManager().getPIRTConfiguration(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.PIRT);

			// add configuration file import history
			if (changed)
				getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						getViewManager().getCache().getModel(), getViewManager().getCache().getUser(), CFFeature.PIRT,
						schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		String schemaPath = getView().getTextPCMMSchemaPath();
		File schemaFile = getView().getTextPCMMSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getViewManager().getAppManager()
					.getService(IImportPCMMApp.class)
					.analyzeUpdatePCMMConfiguration(getViewManager().getCache().getModel(),
							getViewManager().getPCMMConfiguration(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.PCMM);

			// add configuration file import history
			if (changed)
				getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						getViewManager().getCache().getModel(), getViewManager().getCache().getUser(), CFFeature.PCMM,
						schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		String schemaPath = getView().getTextUncertaintySchemaPath();
		File schemaFile = getView().getTextUncertaintySchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getViewManager().getAppManager()
					.getService(IImportUncertaintyApp.class)
					.analyzeUpdateUncertaintyConfiguration(getViewManager().getCache().getModel(),
							getViewManager().getUncertaintyConfiguration(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.UNCERTAINTY);

			// add configuration file import history
			if (changed)
				getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						getViewManager().getCache().getModel(), getViewManager().getCache().getUser(),
						CFFeature.UNCERTAINTY, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		String schemaPath = getView().getTextRequirementsSchemaPath();
		File schemaFile = getView().getTextRequirementsSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getViewManager().getAppManager()
					.getService(IImportSysRequirementApp.class)
					.analyzeUpdateRequirementsConfiguration(getViewManager().getCache().getModel(),
							getViewManager().getSystemRequirementConfiguration(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.SYSTEM_REQUIREMENTS);

			// add configuration file import history
			if (changed)
				getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						getViewManager().getCache().getModel(), getViewManager().getCache().getUser(),
						CFFeature.SYSTEM_REQUIREMENTS, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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

		String schemaPath = getView().getTextDecisionSchemaPath();
		File schemaFile = getView().getTextDecisionSchemaFile();

		try {

			// get analysis
			Map<Class<?>, Map<ImportActionType, List<?>>> analysis = getViewManager().getAppManager()
					.getService(IImportDecisionApp.class)
					.analyzeUpdateDecisionConfiguration(getViewManager().getCache().getModel(),
							getViewManager().getCache().getDecisionSpecification(), schemaFile);

			// import schema
			boolean changed = importSchema(analysis, ImportSchema.DECISION);

			// add configuration file import history
			if (changed)
				getViewManager().getAppManager().getService(IGlobalApplication.class).addConfigurationFile(
						getViewManager().getCache().getModel(), getViewManager().getCache().getUser(),
						CFFeature.DECISION, schemaPath);

		} catch (CredibilityException | IOException e) {
			logger.error(e.getMessage(), e);
			MessageDialog.openError(getView().getShell(), RscTools.getString(RscConst.ERROR_TITLE),
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
	 * Check save need.
	 *
	 * @throws CredibilityException the credibility exception
	 */
	private void checkSaveNeed() throws CredibilityException {

		// check save need
		if (getViewManager().isDirty()) {
			boolean openConfirm = MessageDialog.openConfirm(getView().getShell(),
					RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
					RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_NEEDSAVE));
			if (openConfirm) {
				getViewManager().doSave();
			} else {
				MessageDialog.openInformation(getView().getShell(),
						RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_TITLE),
						RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_CANCELLED));
				throw new CredibilityException(RscTools.getString(RscConst.MSG_CONF_IMPORTVIEW_IMPORT_CANCELLED));
			}
		}
	}

}
