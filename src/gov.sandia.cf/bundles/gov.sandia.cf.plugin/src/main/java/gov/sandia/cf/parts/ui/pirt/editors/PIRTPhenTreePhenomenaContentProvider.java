/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;

/**
 * Provides the content of the PIRT table
 * 
 * @author Maxime N.
 *
 */
public class PIRTPhenTreePhenomenaContentProvider implements ITreeContentProvider {

	/** {@inheritDoc} */
	@Override
	public Object[] getElements(Object inputElement) {
		List<PhenomenonGroup> data = new ArrayList<>();
		if (inputElement instanceof List) {
			for (Object elt : (List<?>) inputElement) {
				if (elt instanceof PhenomenonGroup) {
					data.add((PhenomenonGroup) elt);
				}
			}
		}

		// sort by id label
		data.sort(Comparator.comparing(PhenomenonGroup::getIdLabel));

		return data.toArray();
	}

	/** {@inheritDoc} */
	@Override
	public Object[] getChildren(Object parentElement) {

		Object[] tab = null;

		if (parentElement instanceof PhenomenonGroup) {

			List<Phenomenon> data = ((PhenomenonGroup) parentElement).getPhenomenonList();

			// sort by id label
			if (data != null) {
				data.sort(Comparator.comparing(Phenomenon::getIdLabel));
				return data.toArray();
			}
		}
		return tab;
	}

	/** {@inheritDoc} */
	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof Phenomenon) {
			return ((Phenomenon) element).getPhenomenonGroup();
		}
		return parent;
	}

	/** {@inheritDoc} */
	@Override
	public boolean hasChildren(Object element) {
		boolean hasChildren = false;
		if (element instanceof PhenomenonGroup) {
			return ((PhenomenonGroup) element).getPhenomenonList() != null
					&& !((PhenomenonGroup) element).getPhenomenonList().isEmpty();
		}
		return hasChildren;
	}

}
