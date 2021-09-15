/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt;

import org.eclipse.swt.custom.CTabFolder;

/**
 * Quantity of interest view: it is the QoI home page to select, open and delete
 * a QoI
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTTabFolder extends CTabFolder {

	/**
	 * The parent view
	 */
	private PIRTViewManager viewManager;

	/**
	 * The constructor
	 * 
	 * @param viewManager the parent view
	 * @param style       the view style
	 */
	public PIRTTabFolder(PIRTViewManager viewManager, int style) {
		super(viewManager, style);
		this.viewManager = viewManager;
	}

	@SuppressWarnings("javadoc")
	public PIRTViewManager getViewManager() {
		return viewManager;
	}

}
