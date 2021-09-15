/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.swt.widgets.Composite;

/**
 * An extend of ComboBoxCellEditor to avoid strange behaviors on
 * ComboBoxCellEditor
 * 
 * @author Didier Verstraete
 *
 */
public class CFComboBoxCellEditor extends ComboBoxCellEditor {

	/**
	 * The constructor
	 * 
	 * @param parent the parent composite
	 * @param items  the items to add to the combo
	 */
	public CFComboBoxCellEditor(Composite parent, String[] items) {
		super(parent, items);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void doSetValue(Object value) {
		super.doSetValue(value == null ? -1 : value);
	}

}
