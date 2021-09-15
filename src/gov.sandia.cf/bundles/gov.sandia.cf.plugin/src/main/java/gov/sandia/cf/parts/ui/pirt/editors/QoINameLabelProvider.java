/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.graphics.Image;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.ui.IViewManager;

/**
 * The QoI name column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class QoINameLabelProvider extends QoILabelProvider {

	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/**
	 * Constructor
	 * 
	 * @param viewMgr the view manager
	 */
	public QoINameLabelProvider(IViewManager viewMgr) {
		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;
	}

	@Override
	public String getText(Object element) {
		return (element instanceof QuantityOfInterest) ? ((QuantityOfInterest) element).getSymbol() : null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof QuantityOfInterest) {
			QuantityOfInterest qoi = (QuantityOfInterest) element;
			Image qoiIcon = null;
			if (qoi.getTagDate() != null) {
				qoiIcon = IconTheme.getIconImage(viewMgr.getRscMgr(), IconTheme.ICON_NAME_TAG,
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN));
			} else {
				qoiIcon = IconTheme.getIconImage(viewMgr.getRscMgr(), IconTheme.ICON_NAME_EMPTY,
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
			}
			return qoiIcon;
		}
		return null;
	}
}
