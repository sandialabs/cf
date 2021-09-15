/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs;

import org.eclipse.core.resources.IContainer;
import org.eclipse.swt.widgets.Shell;

/**
 * The CF container selector dialog
 * 
 * @author Didier Verstraete
 *
 */
public class ContainerPickerDialog extends org.eclipse.ui.dialogs.ContainerSelectionDialog {

	/**
	 * Creates a resource container selection dialog rooted at the given resource.
	 * All selections are considered valid.
	 *
	 * @param parentShell           the parent shell
	 * @param initialRoot           the initial selection in the tree
	 * @param allowNewContainerName <code>true</code> to enable the user to type in
	 *                              a new container name, and <code>false</code> to
	 *                              restrict the user to just selecting from
	 *                              existing ones
	 * @param message               the message to be displayed at the top of this
	 *                              dialog, or <code>null</code> to display a
	 *                              default message
	 */
	public ContainerPickerDialog(Shell parentShell, IContainer initialRoot, boolean allowNewContainerName,
			String message) {
		super(parentShell, initialRoot, allowNewContainerName, message);
	}

	/**
	 * @return the first result of the selection
	 */
	public Object getFirstResult() {
		Object[] result = getResult();
		if (result == null || result.length == 0) {
			return null;
		}
		return result[0];
	}
}
