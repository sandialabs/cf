/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.requirement.editors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.SystemRequirement;

/**
 * Provides the content of the Requirement table
 * 
 * @author Maxime N.
 *
 */
public class SystemRequirementTreeContentProvider implements ITreeContentProvider {

	/** {@inheritDoc} */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();
		if (inputElement instanceof List) {
			for (SystemRequirement parent : ((List<SystemRequirement>) inputElement).stream()
					.filter(SystemRequirement.class::isInstance).sorted(Comparator.comparing(SystemRequirement::getGeneratedId))
					.collect(Collectors.toList())) {
				data.add(parent);
			}
		}
		return data.toArray();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] tab = null;
		if (parentElement instanceof SystemRequirement) {
			tab = ((SystemRequirement) parentElement).getChildren().stream()
					.sorted(Comparator.comparing(SystemRequirement::getGeneratedId)).toArray();
		}
		return tab;
	}

	/** {@inheritDoc} */
	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof SystemRequirement) {
			parent = ((SystemRequirement) element).getParent();
		}
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		if (element instanceof SystemRequirement) {
			hasChildren = ((SystemRequirement) element).getChildren() != null
					&& !((SystemRequirement) element).getChildren().isEmpty();
		}
		return hasChildren;
	}

}
