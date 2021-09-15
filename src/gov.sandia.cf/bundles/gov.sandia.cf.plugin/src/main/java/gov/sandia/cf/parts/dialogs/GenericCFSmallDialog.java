/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import gov.sandia.cf.parts.ui.IViewManager;

/**
 * Generic Credibility Dialog (resizable)
 * 
 * @author Didier Verstraete
 *
 * @param <V> the view manager type
 */
public class GenericCFSmallDialog<V extends IViewManager> extends GenericCFDialog<V> {

	/**
	 * The dialog mode create, update
	 */
	protected DialogMode mode;

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parentShell the parent shell
	 */
	public GenericCFSmallDialog(V viewManager, Shell parentShell) {
		super(viewManager, parentShell);
		Assert.isNotNull(viewManager);
		Assert.isNotNull(parentShell);
	}

	@Override
	protected Point getInitialSize() {
		final Point size = super.getInitialSize();

		// fix dialog size error
		size.y += 50;

		return size;
	}

	/**
	 * Set the Ok Button enabled
	 * 
	 * @param enabled is enabled?
	 */
	protected void setEnableOkButton(boolean enabled) {
		Button button = super.getButton(Window.OK);
		if (button != null) {
			button.setEnabled(enabled);
		}
	}

}
