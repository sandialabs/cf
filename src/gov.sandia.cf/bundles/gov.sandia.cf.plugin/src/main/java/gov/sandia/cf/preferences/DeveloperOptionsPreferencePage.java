/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.preferences;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.model.CFFeature;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 *
 * CF Developer Options Preference Page
 * 
 * @author Didier Verstraete
 */
public class DeveloperOptionsPreferencePage extends org.eclipse.jface.preference.FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	private static Logger logger = LoggerFactory.getLogger(DeveloperOptionsPreferencePage.class);

	/**
	 * The constructors
	 */
	public DeveloperOptionsPreferencePage() {
		super(GRID);
	}

	/** {@inheritDoc} */
	@Override
	protected void createFieldEditors() {

		// add report inline word document option
		addField(new BooleanFieldEditor(PrefTools.DEVOPTS_REPORT_INLINEWORD_KEY,
				RscTools.getString(RscConst.PREFS_DEVOPTS_REPORT_INLINEWORD_KEY), getFieldEditorParent()));

		// add concurrency support developer option
		addField(new BooleanFieldEditor(PrefTools.DEVOPTS_CONCURRENCY_SUPPORT_KEY,
				RscTools.getString(RscConst.PREFS_DEVOPTS_CONCURRENCY_SUPPORT_KEY), getFieldEditorParent()));

		// add log level developer option
		addField(new ComboFieldEditor(PrefTools.DEVOPTS_LOG_LEVEL_KEY,
				RscTools.getString(RscConst.PREFS_DEVOPTS_LOG_LEVEL_KEY),
				new String[][] { { Level.ERROR.levelStr, Level.ERROR.levelStr },
						{ Level.WARN.levelStr, Level.WARN.levelStr }, { Level.INFO.levelStr, Level.INFO.levelStr },
						{ Level.DEBUG.levelStr, Level.DEBUG.levelStr },
						{ Level.TRACE.levelStr, Level.TRACE.levelStr } },
				getFieldEditorParent()));
	}

	/** {@inheritDoc} */
	@Override
	public void init(IWorkbench workbench) {
		// initialize default preferences if not set
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, PrefTools.CF_NODE));

		setDescription(RscTools.getString(RscConst.PREFS_DEVOPTS_DESCRIPTION));
	}

	/** {@inheritDoc} */
	@Override
	protected void performApply() {

		super.performApply();

		// set configured logger level
		setLogLevel();

		// reload the editors
		WorkspaceTools.getActiveCFEditors().forEach(e -> e.getViewMgr().reloadView(CFFeature.GEN_REPORT));
	}

	/** {@inheritDoc} */
	@Override
	public boolean performOk() {

		boolean performOk = super.performOk();

		// set configured logger level
		setLogLevel();

		// reload the editors
		WorkspaceTools.getActiveCFEditors().forEach(e -> e.getViewMgr().reloadView(CFFeature.GEN_REPORT));

		return performOk;
	}

	/**
	 * Sets the log level.
	 */
	private void setLogLevel() {
		LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
		if (loggerContext == null) {
			logger.warn("Impossible to load the logger context"); //$NON-NLS-1$
			return;
		}

		ch.qos.logback.classic.Logger cfLogger = loggerContext.getLogger(CredibilityFrameworkConstants.CF_PACKAGE_ROOT);
		String logLevel = PrefTools.getPreference(PrefTools.DEVOPTS_LOG_LEVEL_KEY);
		if (cfLogger != null && !StringUtils.isBlank(logLevel)) {
			Level level = Level.toLevel(logLevel);
			if (level != null) {
				cfLogger.setLevel(level);
			}
		}
	}
}
