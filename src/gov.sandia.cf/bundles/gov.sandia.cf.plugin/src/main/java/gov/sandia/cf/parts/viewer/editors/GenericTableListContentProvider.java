/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.viewer.editors;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;

/**
 * Provides the content of a list of element to a table
 * 
 * @author Didier Verstraete
 *
 */
public class GenericTableListContentProvider implements IStructuredContentProvider {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		List<Object> data = new ArrayList<>();

		if (inputElement instanceof List) {
			for (Object qoi : (List<?>) inputElement) {
				data.add(qoi);
			}
		}
		return data.toArray();
	}

}
