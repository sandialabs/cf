/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.QuantityOfInterest;

/**
 * Provides the content of the Quantity Of Interest tree
 * 
 * @author Maxime N.
 *
 */
public class QoITreeContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();

		if (inputElement != null) {
			for (QuantityOfInterest qoi : (List<QuantityOfInterest>) inputElement) {
				data.add(qoi);
			}
		}
		return data.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		List<Object> data = new ArrayList<>();
		// TODO: check is tagged
		if (parentElement instanceof QuantityOfInterest) {
			QuantityOfInterest parentQoi = (QuantityOfInterest) parentElement;
			if (null != parentQoi.getChildren() && !parentQoi.getChildren().isEmpty()) {
				parentQoi.getChildren().forEach(data::add);
			}
		}
		return data.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof QuantityOfInterest) {
			QuantityOfInterest qoi = (QuantityOfInterest) element;
			return qoi.getParent();
		}
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		// TODO: check is tagged
		if (element instanceof QuantityOfInterest) {
			QuantityOfInterest qoi = (QuantityOfInterest) element;
			return null != qoi.getChildren() && !qoi.getChildren().isEmpty();
		}
		return hasChildren;
	}
}
