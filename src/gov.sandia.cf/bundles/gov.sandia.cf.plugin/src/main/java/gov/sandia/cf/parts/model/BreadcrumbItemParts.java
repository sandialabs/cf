/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.model;

import gov.sandia.cf.parts.ui.IViewManager;

/**
 * Breadcrumb item part model
 * 
 * @author Didier Verstraete
 *
 */
public class BreadcrumbItemParts {

	private String name;

	private IViewManager listener;

	/**
	 * @return the name of the breadcrumb item
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the name of the breadcrumb item
	 * 
	 * @param name the breadcrumb name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the listener of the breadcrumb item
	 */
	public IViewManager getListener() {
		return listener;
	}

	/**
	 * Set the listener of the breadcrumb item
	 * 
	 * @param listener the view manager listener
	 */
	public void setListener(IViewManager listener) {
		this.listener = listener;
	}

}
