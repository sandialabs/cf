/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.uncertainty.editors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.Uncertainty;
import gov.sandia.cf.model.UncertaintyGroup;

/**
 * Provides the content of the Uncertainty table
 * 
 * @author Maxime N.
 *
 */
public class UncertaintyTreeContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();
		if (inputElement instanceof List) {
			for (UncertaintyGroup group : ((List<UncertaintyGroup>) inputElement).stream()
					.filter(UncertaintyGroup.class::isInstance).sorted(Comparator.comparing(UncertaintyGroup::getId))
					.collect(Collectors.toList())) {
				data.add(group);
			}
		}
		return data.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] tab = null;
		if (parentElement instanceof UncertaintyGroup) {
			tab = ((UncertaintyGroup) parentElement).getUncertainties().stream()
					.sorted(Comparator.comparing(Uncertainty::getId)).toArray();
		}
		return tab;
	}

	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof Uncertainty) {
			parent = ((Uncertainty) element).getGroup();
		}
		return parent;
	}

	@Override
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		if (element instanceof UncertaintyGroup) {
			hasChildren = ((UncertaintyGroup) element).getUncertainties() != null
					&& !((UncertaintyGroup) element).getUncertainties().isEmpty();
		}
		return hasChildren;
	}

}
