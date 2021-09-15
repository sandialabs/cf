/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

/**
 * Provides the content of the PCMM Assessment table
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessTreeSimplifiedContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();

		if (inputElement != null) {
			for (Object element : (List<?>) inputElement) {
				data.add(element);
			}
		}
		return data.toArray();
	}

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
