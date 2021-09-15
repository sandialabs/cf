/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Provides the content of the PIRT Level view in DEFAULT mode
 * 
 * @author Didier Verstraete
 *
 */
public class GenericTreeListContentProvider extends GenericTableListContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
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
		return false;
	}

}
