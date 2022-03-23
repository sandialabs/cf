/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ResourceManager;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Color;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.tools.ColorTools;

/**
 * The QoI column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class QoILabelProvider extends ColumnLabelProvider {

	private ResourceManager rscMgr;

	/**
	 * @param rscMgr the resource manager
	 */
	public QoILabelProvider(ResourceManager rscMgr) {
		super();

		Assert.isNotNull(rscMgr);
		this.rscMgr = rscMgr;
	}

	@Override
	public Color getBackground(Object element) {
		if (element instanceof QuantityOfInterest) {
			QuantityOfInterest qoi = (QuantityOfInterest) element;
			if (null != qoi.getParent()) {
				List<QuantityOfInterest> children = qoi.getParent().getChildren();
				return ColorTools.toColor(rscMgr, ConstantTheme.getScaledColor(children.indexOf(qoi)));
			}
		}
		return null;
	}

	@Override
	public Color getForeground(Object element) {
		return ColorTools.toColor(rscMgr, ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BLACK));
	}

	protected ResourceManager getRscMgr() {
		return this.rscMgr;
	}
}
