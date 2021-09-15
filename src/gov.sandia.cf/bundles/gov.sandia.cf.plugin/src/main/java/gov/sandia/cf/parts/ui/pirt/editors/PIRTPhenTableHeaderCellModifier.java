/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.Item;

import gov.sandia.cf.parts.model.QoIHeaderParts;
import gov.sandia.cf.parts.ui.pirt.PIRTPhenomenaViewController;
import gov.sandia.cf.parts.viewer.TableHeader;

/**
 * Defines the PIRT table cell modifier and all the constants of the table
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenTableHeaderCellModifier implements ICellModifier {

	/**
	 * parent view
	 */
	private PIRTPhenomenaViewController viewCtrl;

	/**
	 * @param viewCtrl the view controller
	 */
	public PIRTPhenTableHeaderCellModifier(PIRTPhenomenaViewController viewCtrl) {
		this.viewCtrl = viewCtrl;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify(Object element, String property, Object value) {

		// update qoi headers
		if (element instanceof Item && ((Item) element).getData() instanceof QoIHeaderParts) {
			viewCtrl.updateQoIHeaders((QoIHeaderParts) ((Item) element).getData(), property, (String) value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getValue(Object element, String property) {
		if (property.equals(TableHeader.COLUMN_VALUE_PROPERTY) && element != null
				&& element instanceof QoIHeaderParts) {
			QoIHeaderParts qoiHeaderParts = (QoIHeaderParts) element;
			if (!qoiHeaderParts.isFixed()
					|| PIRTPhenTableHeaderDescriptor.getRowNameLabel().equals(qoiHeaderParts.getName())
					|| PIRTPhenTableHeaderDescriptor.getRowDescriptionLabel().equals(qoiHeaderParts.getName())
					|| PIRTPhenTableHeaderDescriptor.getRowTagDescriptionLabel().equals(qoiHeaderParts.getName())) {
				return qoiHeaderParts.getValue();
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canModify(Object element, String property) {

		// if column to modify is value
		if (property.equals(TableHeader.COLUMN_VALUE_PROPERTY) && element instanceof QoIHeaderParts) {
			QoIHeaderParts qoiHeaderParts = (QoIHeaderParts) element;
			// can only modify the name of the qoi if the aoi is not tagged or the tag
			// description if the qoi is tagged
			if (qoiHeaderParts.getQoi() == null) {
				return false;
			}

			boolean taggedConditions = qoiHeaderParts.getQoi().getTagDate() != null
					&& PIRTPhenTableHeaderDescriptor.getRowTagDescriptionLabel().equals(qoiHeaderParts.getName());
			boolean nonTaggedConditions = qoiHeaderParts.getQoi().getTagDate() == null && (!qoiHeaderParts.isFixed()
					|| PIRTPhenTableHeaderDescriptor.getRowNameLabel().equals(qoiHeaderParts.getName())
					|| PIRTPhenTableHeaderDescriptor.getRowDescriptionLabel().equals(qoiHeaderParts.getName()));
			return taggedConditions || nonTaggedConditions;
		}

		return false;
	}
}
