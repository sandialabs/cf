/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui;

import org.eclipse.swt.widgets.Composite;

/**
 * Qn abstract class to force the credibility views to implement methods needed
 * for the view managers
 * 
 * @author Didier Verstraete
 *
 * @param <V> the view manager
 */
public abstract class ACredibilitySubView<V extends Composite & IViewManager> extends ACredibilityView<V> {

	/**
	 * The constructor
	 * 
	 * @param viewManager the view manager
	 * @param parent      the parent composite
	 * @param style       the style
	 */
	public ACredibilitySubView(V viewManager, Composite parent, int style) {
		super(viewManager, parent, style);

		// breadcrumb
		super.createBreadcrumb(this);
	}
}
