/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pcmm.editors;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import gov.sandia.cf.model.Tag;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscTools;

/**
 * The PCMM Tag tag date column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class PCMMTagDateLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof Tag) {
			return ((Tag) element).getDateTag() != null
					? DateTools.formatDate(((Tag) element).getDateTag(), DateTools.getDateTimeFormat())
					: RscTools.empty();
		}
		return null;
	}

}
