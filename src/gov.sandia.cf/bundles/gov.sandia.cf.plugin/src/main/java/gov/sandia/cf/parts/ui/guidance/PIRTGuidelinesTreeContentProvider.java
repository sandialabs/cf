/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.PIRTAdequacyColumnGuideline;
import gov.sandia.cf.model.PIRTAdequacyColumnLevelGuideline;

/**
 * Provides the content of the PIRT Ranking Guidelines view in DEFAULT mode
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTGuidelinesTreeContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof PIRTAdequacyColumnGuideline) {
			List<PIRTAdequacyColumnLevelGuideline> children = ((PIRTAdequacyColumnGuideline) parentElement)
					.getLevelGuidelines();
			return children != null ? children.toArray() : null;
		}
		return new Object[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return inputElement != null ? ((List<PIRTAdequacyColumnLevelGuideline>) inputElement).toArray() : null;
	}

}
