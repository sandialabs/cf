/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs.importation;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ITreeContentProvider;

import gov.sandia.cf.model.IImportable;

/**
 * The import changes tree content provider.
 * 
 * @author Didier Verstraete
 *
 */
public class ImportChangeTreeContentProvider implements ITreeContentProvider {

	protected static final String ALL = "All"; //$NON-NLS-1$

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof Map;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof IImportable<?>) {
			return ALL;
		}
		return null;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement instanceof List<?>) {
			Map<String, List<?>> elements = new HashMap<>();
			elements.put(ALL, ((List<?>) inputElement));
			return new Object[] { elements };
		}
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Map) {
			Object values = ((Map<?, ?>) parentElement).get(ALL);
			if (values instanceof List) {
				return ((List<?>) values).toArray();
			}
		}
		return new Object[0];
	}

}
