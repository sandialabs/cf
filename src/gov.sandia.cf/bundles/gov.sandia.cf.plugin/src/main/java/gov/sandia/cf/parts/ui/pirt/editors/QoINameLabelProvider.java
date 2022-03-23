/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.swt.graphics.Image;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;

/**
 * The QoI name column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class QoINameLabelProvider extends QoILabelProvider {

	/**
	 * Constructor
	 * 
	 * @param rscMgr the view manager
	 */
	public QoINameLabelProvider(ResourceManager rscMgr) {
		super(rscMgr);
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
				qoiIcon = IconTheme.getIconImage(getRscMgr(), IconTheme.ICON_NAME_TAG,
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN));
			} else {
				qoiIcon = IconTheme.getIconImage(getRscMgr(), IconTheme.ICON_NAME_EMPTY,
						ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
			}
			return qoiIcon;
		}
		return null;
	}
}
