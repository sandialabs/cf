/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs;

import java.util.stream.Stream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The container validator
 * 
 * @author Didier Verstraete
 *
 */
public class ContainerDialogValidator implements ISelectionStatusValidator {

	@Override
	public IStatus validate(Object[] selection) {
		if (selection != null) {
			boolean allMatch = Stream.of(selection).allMatch(IContainer.class::isInstance);
			if (allMatch) {
				return new Status(IStatus.OK, CredibilityFrameworkConstants.CF_PLUGIN_NAME, RscTools.empty());
			}
		}
		return new Status(IStatus.ERROR, CredibilityFrameworkConstants.CF_PLUGIN_NAME,
				RscTools.getString(RscConst.EX_CONTAINERVALIDATOR_NOTVALID));
	}

}
