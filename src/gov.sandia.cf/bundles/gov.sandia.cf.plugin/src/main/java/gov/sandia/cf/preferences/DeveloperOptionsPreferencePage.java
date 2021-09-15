/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.preferences;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

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

		// reload the editors
		WorkspaceTools.getActiveCFEditors().forEach(e -> e.getViewMgr().reloadView(CFFeature.GEN_REPORT));
	}

	/** {@inheritDoc} */
	@Override
	public boolean performOk() {

		boolean performOk = super.performOk();

		// reload the editors
		WorkspaceTools.getActiveCFEditors().forEach(e -> e.getViewMgr().reloadView(CFFeature.GEN_REPORT));

		return performOk;
	}
}
