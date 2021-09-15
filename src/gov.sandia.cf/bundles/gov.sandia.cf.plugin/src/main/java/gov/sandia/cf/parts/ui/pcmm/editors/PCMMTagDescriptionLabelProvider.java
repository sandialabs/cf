/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import gov.sandia.cf.model.Tag;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.StringTools;

/**
 * The PCMM Tag description column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMTagDescriptionLabelProvider extends ColumnLabelProvider {
	@Override
	public String getText(Object element) {
		if (element instanceof Tag) {
			return ((Tag) element).getDescription() != null
					? StringTools.clearHtml(((Tag) element).getDescription(), true)
					: RscTools.empty();
		}
		return null;
	}
}
