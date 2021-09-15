/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.decision.editors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.Decision;

/**
 * Provides the content of the Decision table
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionTreeContentProvider implements ITreeContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();
		if (inputElement instanceof List) {
			for (Decision decision : ((List<Decision>) inputElement).stream().filter(e -> e instanceof Decision)
					.sorted(Comparator.comparing(Decision::getGeneratedId)).collect(Collectors.toList())) {
				data.add(decision);
			}
		}
		return data.toArray();
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] tab = null;
		if (parentElement instanceof Decision) {
			tab = ((Decision) parentElement).getChildren().stream()
					.sorted(Comparator.comparing(Decision::getGeneratedId)).toArray();
		}
		return tab;
	}

	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof Decision) {
			parent = ((Decision) element).getParent();
		}
		return parent;
	}

	@Override
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		if (element instanceof Decision) {
			hasChildren = ((Decision) element).getChildren() != null && !((Decision) element).getChildren().isEmpty();
		}
		return hasChildren;
	}

}
