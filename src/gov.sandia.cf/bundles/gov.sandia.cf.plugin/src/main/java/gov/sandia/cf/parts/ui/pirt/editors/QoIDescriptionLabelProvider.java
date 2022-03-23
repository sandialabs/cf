/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import org.eclipse.jface.resource.ResourceManager;

import gov.sandia.cf.model.QuantityOfInterest;
import gov.sandia.cf.tools.StringTools;

/**
 * The QoI description column label provider
 * 
 * @author Didier Verstraete
 *
 */
public class QoIDescriptionLabelProvider extends QoILabelProvider {
	/**
	 * @param rscMgr the resource manager
	 */
	public QoIDescriptionLabelProvider(ResourceManager rscMgr) {
		super(rscMgr);
	}

	@Override
	public String getText(Object element) {
		return (element instanceof QuantityOfInterest)
				? StringTools.clearHtml(((QuantityOfInterest) element).getDescription(), true)
				: null;
	}
}
