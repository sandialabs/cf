/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.preferences;

import java.io.IOException;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.tools.RscTools;

/**
 * Eclipse OSGI Preferences class to manage cf plugin preferences
 * 
 * @author Didier Verstraete
 *
 */
public class PrefTools {

	/**
	 * the logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(PrefTools.class);

	/**
	 * Pref Pages id
	 */
	/** CF preferences ROOT Page ID */
	public static final String CF_PREF_ROOT_PAGE_ID = "gov.sandia.cf.plugin.rootPreferencePage"; //$NON-NLS-1$
	/** CF preferences DEVELOPER OPTIONS Page ID */
	public static final String CF_PREF_DEVOPTS_PAGE_ID = "gov.sandia.cf.plugin.developerOptionsPreferencePage"; //$NON-NLS-1$

	/**
	 * CF_NODE constant
	 */
	public static final String CF_NODE = CredibilityFrameworkConstants.CF_PLUGIN_NAME;

	/**
	 * Global CF Preferences
	 */
	/** GLOBAL CONFIGURATION FOLDER LAST PATH preference key */
	public static final String CONF_SCHEMA_FOLDER_LAST_PATH_KEY = "conf_schema_folder_last_path"; //$NON-NLS-1$
	/** DECISION SCHEMA FILE LAST PATH preference key */
	public static final String DECISION_SCHEMA_FILE_LAST_PATH_KEY = "decision_schema_file_last_path"; //$NON-NLS-1$
	/** PIRT SCHEMA FILE LAST PATH preference key */
	public static final String PIRT_SCHEMA_FILE_LAST_PATH_KEY = "pirt_schema_file_last_path"; //$NON-NLS-1$
	/** QOI PLANNING SCHEMA FILE LAST PATH preference key */
	public static final String QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY = "qoiplanning_schema_file_last_path"; //$NON-NLS-1$
	/** PCMM SCHEMA FILE LAST PATH preference key */
	public static final String PCMM_SCHEMA_FILE_LAST_PATH_KEY = "pcmm_schema_file_last_path"; //$NON-NLS-1$
	/** UNCERTAINTY SCHEMA FILE LAST PATH preference key */
	public static final String UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY = "com_uncertainty_schema_file_last_path"; //$NON-NLS-1$
	/** SYSTEM REQUIREMENT SCHEMA FILE LAST PATH preference key */
	public static final String SYSTEM_REQUIREMENT_SCHEMA_FILE_LAST_PATH_KEY = "com_requirement_schema_file_last_path"; //$NON-NLS-1$
	/** PIRT QUERY FILE LAST PATH preference key */
	public static final String PIRT_QUERY_FILE_PATH_KEY = "pirt_query_file_path"; //$NON-NLS-1$
	/** DISPLAY VERSION NUMBER preference key */
	public static final String GLOBAL_DISPLAY_VERSION_NUMBER_PATH_KEY = "global_display_version_number_path"; //$NON-NLS-1$
	/** DISPLAY CREATION VERSION NUMBER preference key */
	public static final String GLOBAL_DISPLAY_VERSION_ORIGIN_NUMBER_PATH_KEY = "global_display_version_origin_number_path"; //$NON-NLS-1$
	/** ARG EXECUTABLE PATH preference key */
	public static final String GLOBAL_ARG_EXECUTABLE_PATH_KEY = "global_arg_executable_path_key"; //$NON-NLS-1$
	/** ARG SETENV SCRIPT PATH preference key */
	public static final String GLOBAL_ARG_SETENV_SCRIPT_PATH_KEY = "global_arg_setenv_path"; //$NON-NLS-1$
	/** DEFAULT BROWSER FOR LINKS preference key */
	public static final String GLOBAL_OPEN_LINK_BROWSER_OPTION_KEY = "global_open_link_browser_option_key"; //$NON-NLS-1$
	/** The Constant WEB_SERVER_URL. */
	public static final String WEB_SERVER_URL = "web_server_url_key"; //$NON-NLS-1$

	/**
	 * PCMM Preferences
	 */
	/** The Constant PCMM_EVIDENCE_FILE_LAST_PATH_KEY. */
	public static final String PCMM_EVIDENCE_FILE_LAST_PATH_KEY = "pcmm_evidence_file_last_path"; //$NON-NLS-1$

	/**
	 * CF Developer Options Preferences
	 */
	/** The Constant DEVOPTS_REPORT_INLINEWORD_KEY. */
	public static final String DEVOPTS_REPORT_INLINEWORD_KEY = "devopts_report_inlineword_key"; //$NON-NLS-1$
	/** The Constant DEVOPTS_CONCURRENCY_SUPPORT_KEY. */
	public static final String DEVOPTS_CONCURRENCY_SUPPORT_KEY = "devopts_concurrency_support_key"; //$NON-NLS-1$

	
	
	/**
	 * Configuration import constants
	 */
	/** IMPORT DECISION SCHEMA FILE LAST PATH preference key */
	public static final String CONF_IMPORT_DECISION_SCHEMA_FILE_LAST_PATH_KEY = "conf_import_decision_schema_file_last_path"; //$NON-NLS-1$
	/** IMPORT QOI PLANNING SCHEMA FILE LAST PATH preference key */
	public static final String CONF_IMPORT_QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY = "conf_import_qoiplanning_schema_file_last_path"; //$NON-NLS-1$
	/** IMPORT PIRT SCHEMA FILE LAST PATH preference key */
	public static final String CONF_IMPORT_PIRT_SCHEMA_FILE_LAST_PATH_KEY = "conf_import_pirt_schema_file_last_path"; //$NON-NLS-1$
	/** IMPORT PCMM SCHEMA FILE LAST PATH preference key */
	public static final String CONF_IMPORT_PCMM_SCHEMA_FILE_LAST_PATH_KEY = "conf_import_pcmm_schema_file_last_path"; //$NON-NLS-1$
	/** IMPORT UNCERTAINTY SCHEMA FILE LAST PATH preference key */
	public static final String CONF_IMPORT_UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY = "conf_import_uncertainty_schema_file_last_path"; //$NON-NLS-1$
	/** IMPORT SYSTEM REQUIREMENTS SCHEMA FILE LAST PATH preference key */
	public static final String CONF_IMPORT_REQUIREMENTS_SCHEMA_FILE_LAST_PATH_KEY = "conf_import_requirements_schema_file_last_path"; //$NON-NLS-1$
	/**
	 * Configuration export constants
	 */
	/** EXPORT DECISION SCHEMA FILE LAST PATH preference key */
	public static final String CONF_EXPORT_DECISION_SCHEMA_FILE_LAST_PATH_KEY = "conf_export_decision_schema_file_last_path"; //$NON-NLS-1$
	/** EXPORT QOI PLANNING SCHEMA FILE LAST PATH preference key */
	public static final String CONF_EXPORT_QOIPLANNING_SCHEMA_FILE_LAST_PATH_KEY = "conf_export_qoiplanning_schema_file_last_path"; //$NON-NLS-1$
	/** EXPORT PIRT SCHEMA FILE LAST PATH preference key */
	public static final String CONF_EXPORT_PIRT_SCHEMA_FILE_LAST_PATH_KEY = "conf_export_pirt_schema_file_last_path"; //$NON-NLS-1$
	/** EXPORT PCMM SCHEMA FILE LAST PATH preference key */
	public static final String CONF_EXPORT_PCMM_SCHEMA_FILE_LAST_PATH_KEY = "conf_export_pcmm_schema_file_last_path"; //$NON-NLS-1$
	/** EXPORT UNCERTAINTY SCHEMA FILE LAST PATH preference key */
	public static final String CONF_EXPORT_UNCERTAINTY_SCHEMA_FILE_LAST_PATH_KEY = "conf_export_uncertainty_schema_file_last_path"; //$NON-NLS-1$
	/** EXPORT SYSTEM REQUIREMENTS SCHEMA FILE LAST PATH preference key */
	public static final String CONF_EXPORT_REQUIREMENTS_SCHEMA_FILE_LAST_PATH_KEY = "conf_export_requirements_schema_file_last_path"; //$NON-NLS-1$
	/** EXPORT DATA FILE LAST PATH preference key */
	public static final String CONF_EXPORT_DATA_FILE_LAST_PATH_KEY = "conf_export_data_file_last_path"; //$NON-NLS-1$

	/**
	 * Default Global Python Executable Path default value
	 */
	public static final String GLOBAL_PYTHON_EXECUTABLE_PATH_DEFAULTVALUE = "python"; //$NON-NLS-1$

	/**
	 * Private constructor to not allow instantiation.
	 */
	private PrefTools() {
	}

	/**
	 * Initializes default preferences
	 */
	public static void initializePreferencesToDefault() {

		// display version number
		setPreferenceDefaultBoolean(GLOBAL_DISPLAY_VERSION_NUMBER_PATH_KEY, true);

		// display version origin number
		setPreferenceDefaultBoolean(GLOBAL_DISPLAY_VERSION_ORIGIN_NUMBER_PATH_KEY, true);

		// pirt query path
		setPreferenceDefault(PIRT_QUERY_FILE_PATH_KEY, RscTools.empty());

		// arg executable path
		setPreferenceDefault(GLOBAL_ARG_EXECUTABLE_PATH_KEY, RscTools.empty());

		// arg setenv path
		setPreferenceDefault(GLOBAL_ARG_SETENV_SCRIPT_PATH_KEY, RscTools.empty());

		// Open Link Browser Option
		setPreferenceDefault(GLOBAL_OPEN_LINK_BROWSER_OPTION_KEY, OpenLinkBrowserOption.EXERTNAL_BROWSER.name());

		// Developer options
		// inline word documents
		setPreferenceDefaultBoolean(DEVOPTS_REPORT_INLINEWORD_KEY, false);

	}

	/**
	 * @return the global display version number option
	 */
	public static Boolean getGlobalDisplayVersionNumber() {
		return getPreferenceBoolean(GLOBAL_DISPLAY_VERSION_NUMBER_PATH_KEY);
	}

	/**
	 * @return the global display version origin number option
	 */
	public static Boolean getGlobalDisplayVersionOriginNumber() {
		return getPreferenceBoolean(GLOBAL_DISPLAY_VERSION_ORIGIN_NUMBER_PATH_KEY);
	}

	/**
	 * @return the global ARG installation directory path
	 */
	public static String getARGExecutablePath() {
		return getPreference(GLOBAL_ARG_EXECUTABLE_PATH_KEY);
	}

	/**
	 * @return the global ARG set env directory path
	 */
	public static String getARGSetEnvScriptPath() {
		return getPreference(GLOBAL_ARG_SETENV_SCRIPT_PATH_KEY);
	}

	/**
	 * @param prefKey the preference key
	 * @return the preference associated to the key as a string
	 */
	public static String getPreference(String prefKey) {
		ScopedPreferenceStore prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, CF_NODE);
		return prefStore.getString(prefKey) != null ? prefStore.getString(prefKey) : RscTools.empty();
	}

	/**
	 * @param prefKey the preference key
	 * @return the preference associated to the key as a boolean. Returns the
	 *         default value (false) if there is no preference with the given name,
	 *         or if the current value cannot be treated as a boolean.
	 */
	public static Boolean getPreferenceBoolean(String prefKey) {
		ScopedPreferenceStore prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, CF_NODE);
		return prefStore.getString(prefKey) != null ? Boolean.valueOf(prefStore.getBoolean(prefKey)) : Boolean.FALSE;
	}

	/**
	 * Sets the value in parameter to the associated key
	 * 
	 * @param prefKey the preference key
	 * @param value   the value to set
	 */
	public static void setPreference(String prefKey, String value) {

		ScopedPreferenceStore prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, CF_NODE);
		prefStore.setValue(prefKey, value);
		try {
			// forces the application to save the preferences
			prefStore.save();
			logger.info("New preference has been set in instance scope: { {},{} }", prefKey, value);//$NON-NLS-1$
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Sets the value in parameter to the associated key
	 * 
	 * @param prefKey the preference key
	 * @param value   the value to set
	 */
	public static void setPreferenceBoolean(String prefKey, Boolean value) {

		ScopedPreferenceStore prefStore = new ScopedPreferenceStore(InstanceScope.INSTANCE, CF_NODE);
		prefStore.setValue(prefKey, value);
		try {
			// forces the application to save the preferences
			prefStore.save();
			logger.info("New preference has been set in instance scope: { {},{} }", prefKey, value);//$NON-NLS-1$
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}

	/**
	 * Sets the value in parameter to the associated key
	 * 
	 * @param prefKey the preference key
	 * @param value   the value to set
	 */
	public static void setPreferenceDefault(String prefKey, String value) {

		// set into default scope
		ScopedPreferenceStore prefStore = new ScopedPreferenceStore(DefaultScope.INSTANCE, CF_NODE);
		prefStore.setValue(prefKey, value);
		try {
			// forces the application to save the preferences
			prefStore.save();
			logger.info("New preference has been set in instance scope: { {},{} }", prefKey, value);//$NON-NLS-1$
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}

	/**
	 * Sets the value in parameter to the associated key
	 * 
	 * @param prefKey the preference key
	 * @param value   the value to set
	 */
	public static void setPreferenceDefaultBoolean(String prefKey, Boolean value) {

		ScopedPreferenceStore prefStore = new ScopedPreferenceStore(DefaultScope.INSTANCE, CF_NODE);
		prefStore.setValue(prefKey, value);
		try {
			// forces the application to save the preferences
			prefStore.save();
			logger.info("New preference has been set in instance scope: { {},{} }", prefKey, value);//$NON-NLS-1$
		} catch (IOException e) {
			logger.warn(e.getMessage());
		}
	}

	/**
	 * @param shell the active shell
	 * @return the credibility framework preferences dialog
	 */
	public static PreferenceDialog getCFPrefDialog(Shell shell) {
		Shell activeShell = shell;
		if (activeShell == null) {
			activeShell = Display.getCurrent().getActiveShell();
		}
		return PreferencesUtil.createPreferenceDialogOn(activeShell, CF_PREF_ROOT_PAGE_ID, null, null);
	}

	/**
	 * @param shell the active shell
	 * @return the return code of the preferences dialog
	 */
	public static int openPrefDialog(Shell shell) {
		PreferenceDialog pref = getCFPrefDialog(shell);
		int returnCode = -1;
		if (pref != null) {
			returnCode = pref.open();
		}
		return returnCode;
	}
}
