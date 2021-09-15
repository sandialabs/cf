/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import gov.sandia.cf.model.Tag;
import gov.sandia.cf.parts.theme.ConstantTheme;
import gov.sandia.cf.parts.theme.IconTheme;
import gov.sandia.cf.parts.ui.IViewManager;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM Tag name column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMTagNameLabelProvider extends ColumnLabelProvider {

	/**
	 * The view manager
	 */
	private IViewManager viewMgr;

	/**
	 * Constructor
	 * 
	 * @param viewMgr the view manager
	 */
	public PCMMTagNameLabelProvider(IViewManager viewMgr) {
		Assert.isNotNull(viewMgr);
		this.viewMgr = viewMgr;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof Tag) {
			return ((Tag) element).getName() != null ? ((Tag) element).getName() : RscTools.empty();
		}
		return null;
	}

	@Override
	public Image getImage(Object element) {
		if (element instanceof Tag && ((Tag) element).getDateTag() != null) {
			return IconTheme.getIconImage(viewMgr.getRscMgr(), IconTheme.ICON_NAME_TAG,
					ConstantTheme.getColor(ConstantTheme.COLOR_NAME_BROWN));
		}
		return null;
	}
}
