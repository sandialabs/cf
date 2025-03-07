/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMEvidence;
import gov.sandia.cf.model.comparator.StringWithNumberAndNullableComparator;
import gov.sandia.cf.parts.ui.pcmm.PCMMViewManager;

/**
 * Provides the content of the PCMM Evidence table
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMEvidenceTreeSimplifiedContentProvider implements ITreeContentProvider {

	/**
	 * The PCMM view manager
	 */
	private PCMMViewManager viewManager;

	/**
	 * Constructor
	 * 
	 * @param viewManager the PCMM view manager
	 */
	public PCMMEvidenceTreeSimplifiedContentProvider(PCMMViewManager viewManager) {
		this.viewManager = viewManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();

		if (inputElement != null) {
			for (PCMMElement element : (List<PCMMElement>) inputElement) {
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
		Object[] tab = null;
		if (parentElement instanceof PCMMElement && ((PCMMElement) parentElement).getEvidenceList() != null) {

			List<PCMMEvidence> evidenceToDisplay = new ArrayList<>();

			// add evidence for tag null
			if (viewManager.getSelectedTag() == null || viewManager.getSelectedTag().getId() == null) {
				evidenceToDisplay.addAll(((PCMMElement) parentElement).getEvidenceList().stream()
						.filter(evidence -> (evidence.getTag() == null)).collect(Collectors.toList()));
			}
			// add evidence for tag not null
			else {
				evidenceToDisplay.addAll(((PCMMElement) parentElement).getEvidenceList().stream()
						.filter(evidence -> (evidence.getTag() != null
								&& viewManager.getSelectedTag().getId().equals(evidence.getTag().getId())))
						.collect(Collectors.toList()));
			}

			// sort by generated id (user choice)
			evidenceToDisplay.sort(
					Comparator.comparing(PCMMEvidence::getGeneratedId, new StringWithNumberAndNullableComparator()));

			tab = evidenceToDisplay.toArray();
		}
		return tab;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element instanceof PCMMEvidence) {
			return ((PCMMEvidence) element).getElement();
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
			return getChildren(element).length > 0;
		}
		return hasChildren;
	}

}
