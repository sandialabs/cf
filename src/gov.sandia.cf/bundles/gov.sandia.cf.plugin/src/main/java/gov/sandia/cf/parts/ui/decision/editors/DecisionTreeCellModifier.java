/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.decision.editors;

import org.eclipse.jface.viewers.ICellModifier;

/**
 * Defines the Decision table cell modifier and all the constants of the table
 * 
 * @author Didier Verstraete
 *
 */
public class DecisionTreeCellModifier implements ICellModifier {

	/**
	 * The DecisionTreeCellModifier constructor
	 */
	public DecisionTreeCellModifier() {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify(Object element, String property, Object value) {
		// Do nothing
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(Object element, String property) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canModify(Object element, String property) {
		return false;
	}

}
