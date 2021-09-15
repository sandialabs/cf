/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.List;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.theme.ConstantTheme;

/**
 * The QoI column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class QoILabelProvider extends ColumnLabelProvider {

	@Override
	public Color getBackground(Object element) {
		if (element instanceof QuantityOfInterest) {
			QuantityOfInterest qoi = (QuantityOfInterest) element;
			if (null != qoi.getParent()) {
				List<QuantityOfInterest> children = qoi.getParent().getChildren();
				return ConstantTheme.getScaledColor(children.indexOf(qoi));
			}
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK);
	}
}
