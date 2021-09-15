/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.tools;

import java.util.Map;

import org.eclipse.swt.custom.ControlEditor;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.custom.TreeEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 
 * The view tools class
 * 
 * @author Didier Verstraete
 *
 */
public class ViewTools {

	/**
	 * Private constructor to not allow instantiation.
	 */
	private ViewTools() {
	}

	/**
	 * Dispose all the children of the parameter composite but do not dispose the
	 * composite itself
	 * 
	 * @param composite the composite
	 */
	public static void disposeChildren(Composite composite) {
		if (composite != null && !composite.isDisposed()) {
			for (Control control : composite.getChildren()) {
				if (!control.isDisposed()) {
					if (control instanceof Composite) {
						disposeChildren((Composite) control);
						control.dispose();
					} else {
						control.dispose();
					}
				}
			}
		}
	}

	/**
	 * Dispose the control in parameter
	 * 
	 * @param control the control to dispose
	 */
	public static void disposeControl(Control control) {
		if (control != null && !control.isDisposed()) {
			if (control instanceof Composite) {
				disposeChildren((Composite) control);
			}
			control.dispose();
		}
	}

	/**
	 * Dispose the tree editor in parameter
	 * 
	 * @param editor the tree editor to dispose
	 */
	public static void disposeViewerEditor(ControlEditor editor) {
		if (editor != null) {
			if (editor.getEditor() != null && !editor.getEditor().isDisposed()) {
				editor.getEditor().dispose();
			}
			editor.dispose();
		}
	}

	/**
	 * Refresh Table Edit Editors
	 * 
	 * @param editors the editors to refresh
	 */
	public static void refreshTableEditors(Map<TreeItem, TreeEditor> editors) {
		if (editors != null) {
			editors.forEach((item, editor) -> {
				if (editor != null) {
					if (editor.getEditor() != null && !editor.getEditor().isDisposed()) {
						editor.getEditor().dispose();
					}
					editor.dispose();
				}
			});
			editors.clear();
		}
	}

	/**
	 * Refresh the tree editor in parameter
	 * 
	 * @param treeEditor the editor to refresh
	 */
	public static void refreshTreeEditor(TreeEditor treeEditor) {
		if (treeEditor != null) {
			treeEditor.layout();
		}
	}

	/**
	 * Refresh the table editor in parameter
	 * 
	 * @param tableEditor the editor to refresh
	 */
	public static void refreshTableEditor(TableEditor tableEditor) {
		if (tableEditor != null) {
			tableEditor.layout();
		}
	}
}
