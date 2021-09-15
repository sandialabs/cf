/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.widgets;

import gov.sandia.cf.model.Tag;

/**
 * The interface to intercept the tag widget actions
 * 
 * @author Didier Verstraete
 *
 */
public interface ITagAction {
	/**
	 * Tag the current PCMM, and prompt the user to enter the tag parameters
	 */
	void tagCurrentPCMM();

	/**
	 * Opens the view to manage Tags
	 */
	void manageTags();

	/**
	 * Trigger the new tag selected
	 * 
	 * @param newTag the new tag
	 */
	void tagSelectionChanged(Tag newTag);
}
