/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs.importation;

import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.viewers.ColumnLabelProvider;

import gov.sandia.cf.model.IImportable;
import gov.sandia.cf.tools.RscTools;

/**
 * The import changes default label provider.
 * 
 * @author Didier Verstraete
 *
 */
public class ImportChangeTreeLabelProvider extends ColumnLabelProvider {

	@Override
	public String getText(Object element) {
		if (element instanceof IImportable<?>) {
			return ((IImportable<?>) element).getAbstract();
		} else if (element instanceof Map && !((Map<?, ?>) element).isEmpty()
				&& ((Map<?, ?>) element).keySet().iterator().hasNext()) {
			Object next = ((Map<?, ?>) element).keySet().iterator().next();
			return next instanceof String ? (String) next : RscTools.empty();
		} else if (element instanceof Entry) {
			return ((Entry<?, ?>) element).getKey() != null ? ((Entry<?, ?>) element).getKey().toString()
					: RscTools.empty();
		}
		return super.getText(element);
	}

}
