/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.ui.pirt.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

import gov.sandia.cf.model.Model;
import gov.sandia.cf.parts.model.HeaderParts;
import gov.sandia.cf.tools.RscTools;

/**
 * Provides the content of the PIRT table
 * 
 * @author Didier Verstraete
 *
 */
public class PIRTQoITableHeaderContentProvider implements IStructuredContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {

		List<HeaderParts<Model>> data = new ArrayList<>();

		if (inputElement instanceof Model) {

			Model model = (Model) inputElement;

			// Application
			HeaderParts<Model> nameHeader = new HeaderParts<>();
			nameHeader.setName(PIRTQoITableHeaderDescriptor.getApplicationLabel());
			nameHeader.setValue(model.getApplication() != null ? model.getApplication() : RscTools.empty());
			nameHeader.setData(model);
			data.add(nameHeader);

			// Contact
			HeaderParts<Model> creationDateHeader = new HeaderParts<>();
			creationDateHeader.setName(PIRTQoITableHeaderDescriptor.getContactLabel());
			creationDateHeader.setValue(model.getContact() != null ? model.getContact() : RscTools.empty());
			creationDateHeader.setData(model);
			data.add(creationDateHeader);
		}

		return data.toArray();
	}

}
