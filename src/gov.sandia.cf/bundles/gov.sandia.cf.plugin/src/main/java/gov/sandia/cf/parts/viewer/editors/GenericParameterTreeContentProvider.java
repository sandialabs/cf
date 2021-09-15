/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Provides the content of the Generic table
 * 
 * @author Didier Verstraete
 *
 */
public class GenericParameterTreeContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();
		if (inputElement instanceof List) {
			for (Object element : (List<?>) inputElement) {
				data.add(element);
			}
		}
		return data.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return false;
	}

}
