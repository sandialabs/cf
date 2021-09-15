/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMSubelement;

/**
 * Provides the content of the PCMM Assessment table
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMAssessTreeContentProvider implements ITreeContentProvider {

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
		Object[] tab = new Object[] {};
		if (parentElement instanceof PCMMElement && ((PCMMElement) parentElement).getSubElementList() != null) {
			return ((PCMMElement) parentElement).getSubElementList().toArray();
		}
		return tab;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof PCMMSubelement) {
			return ((PCMMSubelement) element).getElement();
		}
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		if (element instanceof PCMMElement) {
			hasChildren = ((PCMMElement) element).getSubElementList() != null
					&& !((PCMMElement) element).getSubElementList().isEmpty();
		}
		return hasChildren;
	}

}
