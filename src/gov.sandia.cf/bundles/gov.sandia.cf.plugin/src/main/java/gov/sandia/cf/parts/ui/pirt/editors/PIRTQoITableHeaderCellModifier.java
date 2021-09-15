/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.parts.model.HeaderParts;
import gov.sandia.cf.parts.ui.pirt.PIRTQoIViewController;
import gov.sandia.cf.parts.viewer.TableHeader;

/**
 * Defines the PIRT table cell modifier and all the constants of the table
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTQoITableHeaderCellModifier implements ICellModifier {
	/**
	 * View controller
	 */
	private PIRTQoIViewController viewCtrl;

	/**
	 * @param viewCtrl the view controller
	 */
	public PIRTQoITableHeaderCellModifier(PIRTQoIViewController viewCtrl) {
		Assert.isNotNull(viewCtrl);
		this.viewCtrl = viewCtrl;
	}

	/** {@inheritDoc} */
	@Override
	public void modify(Object element, String property, Object value) {

		if (element instanceof Item) {
			Item item = (Item) element;

			if (item.getData() instanceof HeaderParts && ((HeaderParts<?>) item.getData()).getData() instanceof Model) {
				Model model = (Model) ((HeaderParts<?>) item.getData()).getData();

				boolean toUpdate = false;
				if (PIRTQoITableHeaderDescriptor.getApplicationLabel()
						.equals(((HeaderParts<?>) item.getData()).getName())) {
					model.setApplication((String) value);
					toUpdate = true;
				} else if (PIRTQoITableHeaderDescriptor.getContactLabel()
						.equals(((HeaderParts<?>) item.getData()).getName())) {
					model.setContact((String) value);
					toUpdate = true;
				}

				// update header
				if (toUpdate) {
					viewCtrl.updateModelHeaders(model, property);
				}
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public Object getValue(Object element, String property) {
		if (property.equals(TableHeader.COLUMN_VALUE_PROPERTY) && element instanceof HeaderParts) {
			return ((HeaderParts<?>) element).getValue();
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public boolean canModify(Object element, String property) {

		// if the column to modify is the value one
		if (property.equals(TableHeader.COLUMN_VALUE_PROPERTY)) {
			return element instanceof HeaderParts;
		}

		return false;
	}
}
