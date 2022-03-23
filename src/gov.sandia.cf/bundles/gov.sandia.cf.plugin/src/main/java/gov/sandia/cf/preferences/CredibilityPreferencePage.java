/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import gov.sandia.cf.model.OpenLinkBrowserOption;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 *
 * Credibility Framework Preference Page
 * 
 * @author Didier Verstraete
 */
public class CredibilityPreferencePage extends org.eclipse.jface.preference.FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/**
	 * The constructors
	 */
	public CredibilityPreferencePage() {
		super(GRID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void createFieldEditors() {

		// add display version number preference
		addField(new BooleanFieldEditor(PrefTools.GLOBAL_DISPLAY_VERSION_NUMBER_PATH_KEY,
				RscTools.getString(RscConst.PREFS_GLOBAL_DISPLAY_VERSION_NUMBER), getFieldEditorParent()));

		// add display version origin number preference
		addField(new BooleanFieldEditor(PrefTools.GLOBAL_DISPLAY_VERSION_ORIGIN_NUMBER_PATH_KEY,
				RscTools.getString(RscConst.PREFS_GLOBAL_DISPLAY_VERSION_ORIGIN_NUMBER), getFieldEditorParent()));

		// TODO issue #9 disable PIRT queries for now
		// add PIRT query file path preference
//		addField(new FileFieldEditor(PrefTools.PIRT_QUERY_FILE_PATH_KEY,
//				RscTools.getString(RscConst.PREFS_PIRT_QUERY_FILE), getFieldEditorParent()));

		// add ARG installation directory preference
		addField(new FileFieldEditor(PrefTools.GLOBAL_ARG_EXECUTABLE_PATH_KEY,
				RscTools.getString(RscConst.PREFS_GLOBAL_ARG_EXECUTABLE), getFieldEditorParent()));

		// add ARG script to set ARG environment
		addField(new FileFieldEditor(PrefTools.GLOBAL_ARG_SETENV_SCRIPT_PATH_KEY,
				RscTools.getString(RscConst.PREFS_GLOBAL_ARG_SETENV), getFieldEditorParent()));

		// add select open link browser option
		addField(new ComboFieldEditor(PrefTools.GLOBAL_OPEN_LINK_BROWSER_OPTION_KEY,
				RscTools.getString(RscConst.PREFS_GLOBAL_OPEN_LINK_BROWSER_OPTION),
				OpenLinkBrowserOption.getNameValueArray(OpenLinkBrowserOption.EXERTNAL_BROWSER,
						OpenLinkBrowserOption.INTERNAL_BROWSER, OpenLinkBrowserOption.ECLIPSE_PREFERENCE),
				getFieldEditorParent()));

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init(IWorkbench workbench) {
		// initialize preference store
		setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, PrefTools.CF_NODE));
	}

	/** {@inheritDoc} */
	@Override
	protected void performApply() {

		super.performApply();

		// reload the editors
		WorkspaceTools.getActiveCFEditors().forEach(e -> e.getViewMgr().reload());
	}

	/** {@inheritDoc} */
	@Override
	public boolean performOk() {

		boolean performOk = super.performOk();

		// reload the editors
		WorkspaceTools.getActiveCFEditors().forEach(e -> e.getViewMgr().reload());

		return performOk;
	}
}
