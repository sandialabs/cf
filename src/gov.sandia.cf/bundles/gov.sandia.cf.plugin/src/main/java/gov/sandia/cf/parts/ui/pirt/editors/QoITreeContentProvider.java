/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;

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
			for (QuantityOfInterest qoi : ((List<QuantityOfInterest>) inputElement).stream()
					.filter(QuantityOfInterest.class::isInstance).sorted(Comparator
							.comparing(QuantityOfInterest::getGeneratedId, new StringWithNumberAndNullableComparator()))
					.collect(Collectors.toList())) {
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
		Object[] tab = null;
		if (parentElement instanceof QuantityOfInterest) {
			tab = ((QuantityOfInterest) parentElement).getChildren().stream().sorted(Comparator
					.comparing(QuantityOfInterest::getGeneratedId, new StringWithNumberAndNullableComparator()))
					.toArray();
		}
		return tab;
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
		if (element instanceof QuantityOfInterest) {
			QuantityOfInterest qoi = (QuantityOfInterest) element;
			return null != qoi.getChildren() && !qoi.getChildren().isEmpty();
		}
		return hasChildren;
	}
}
