/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import org.eclipse.jface.resource.ResourceManager;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tools.DateTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The QoI creation date column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class QoICreationDateLabelProvider extends QoILabelProvider {

	/**
	 * @param rscMgr the resource manager
	 */
	public QoICreationDateLabelProvider(ResourceManager rscMgr) {
		super(rscMgr);
	}

	@Override
	public String getText(Object element) {
		if (element instanceof QuantityOfInterest) {
			QuantityOfInterest qoi = (QuantityOfInterest) element;

			// qoi
			if (qoi.getParent() == null) {
				return qoi.getCreationDate() != null
						? DateTools.formatDate(qoi.getCreationDate(), RscTools.getString(RscConst.DATETIME_FORMAT))
						: RscTools.empty();
			}

			// tag
			else {
				return qoi.getTagDate() != null
						? DateTools.formatDate(qoi.getTagDate(), RscTools.getString(RscConst.DATETIME_FORMAT))
						: RscTools.empty();
			}
		}
		return null;
	}
}
