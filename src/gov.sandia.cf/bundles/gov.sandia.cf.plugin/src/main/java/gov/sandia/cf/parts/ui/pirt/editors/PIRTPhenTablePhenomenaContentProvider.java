/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import gov.sandia.cf.model.Phenomenon;
import gov.sandia.cf.model.PhenomenonGroup;

/**
 * Provides the content of the PIRT table
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTPhenTablePhenomenaContentProvider implements IStructuredContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();
		if (inputElement instanceof List) {
			for (Object object : (List<?>) inputElement) {
				if (object instanceof PhenomenonGroup) {
					PhenomenonGroup group = (PhenomenonGroup) object;
					data.add(group);
					if (group.getPhenomenonList() != null) {
						for (Phenomenon phen : group.getPhenomenonList()) {
							data.add(phen);
						}
					}
				}
			}
		}
		return data.toArray();
	}

}
