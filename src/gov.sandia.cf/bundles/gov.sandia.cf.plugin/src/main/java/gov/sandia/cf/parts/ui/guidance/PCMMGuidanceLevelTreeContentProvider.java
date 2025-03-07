/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.guidance;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.PCMMElement;
import gov.sandia.cf.model.PCMMLevel;
import gov.sandia.cf.model.PCMMLevelDescriptor;
import gov.sandia.cf.model.PCMMSubelement;

/**
 * Provides the content of the PCMM Guidance Level view in DEFAULT mode
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMGuidanceLevelTreeContentProvider implements ITreeContentProvider {
	/**
	 * Flag by row or by column
	 */
	boolean isTreeWithColumn;

	/**
	 * Construct
	 * 
	 * @param isTreeWithColumn is it a tree with column
	 */
	public PCMMGuidanceLevelTreeContentProvider(boolean isTreeWithColumn) {
		this.isTreeWithColumn = isTreeWithColumn;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement != null) {
			if (parentElement instanceof PCMMElement) {
				List<PCMMSubelement> children = ((PCMMElement) parentElement).getSubElementList();
				return children != null ? children.toArray() : null;
			} else if (parentElement instanceof PCMMSubelement) {
				List<PCMMLevel> children = ((PCMMSubelement) parentElement).getLevelList();
				return children != null ? children.toArray() : null;
			} else if (!isTreeWithColumn && parentElement instanceof PCMMLevel) {
				List<PCMMLevelDescriptor> children = ((PCMMLevel) parentElement).getLevelDescriptorList();
				return children != null ? children.toArray() : null;
			}
		}
		return new Object[] {};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getParent(Object element) {
		Object parent = null;
		if (element != null) {
			if (element instanceof PCMMSubelement) {
				parent = ((PCMMSubelement) element).getElement();
			} else if (element instanceof PCMMLevel) {
				parent = ((PCMMLevel) element).getSubelement();
			} else if (!isTreeWithColumn && element instanceof PCMMLevelDescriptor) {
				parent = ((PCMMLevelDescriptor) element).getLevel();
			}
		}
		return parent;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren(Object element) {
		Object[] children = getChildren(element);
		return children != null && children.length > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Object[] getElements(Object inputElement) {
		return inputElement != null ? ((List<PCMMElement>) inputElement).toArray() : null;
	}

}
