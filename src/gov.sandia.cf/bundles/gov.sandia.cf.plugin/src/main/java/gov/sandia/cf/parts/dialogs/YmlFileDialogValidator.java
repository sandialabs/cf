/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.dialogs;

import java.util.stream.Stream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;

import gov.sandia.cf.constants.CredibilityFrameworkConstants;
import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;

/**
 * The yml file validator
 * 
 * @author Didier Verstraete
 *
 */
public class YmlFileDialogValidator implements ISelectionStatusValidator {

	@Override
	public IStatus validate(Object[] selection) {
		if (selection != null) {
			boolean allMatch = Stream.of(selection).allMatch(element -> {
				if (element instanceof IFile) {
					String name = ((IFile) element).getName().toLowerCase();
					boolean valid = name.endsWith(FileTools.YML) || name.endsWith(FileTools.YAML);
					valid &= !name.startsWith(RscTools.DOT) //
							&& !name.startsWith(RscTools.ASTERISK.trim()) //
							&& !name.startsWith(RscTools.PLUS.trim()) //
							&& !name.startsWith(RscTools.COLON.trim()) //
							&& !name.startsWith(RscTools.SEMICOLON.trim()) //
							&& !name.startsWith(RscTools.UNDERSCORE) //	
							&& !name.startsWith(RscTools.COMMA.trim()) //
							&& !name.startsWith(RscTools.PERCENT) //
							&& !name.startsWith(RscTools.HYPHEN); //
					return valid;
				}
				return false;
			});
			if (allMatch) {
				return new Status(IStatus.OK, CredibilityFrameworkConstants.CF_PLUGIN_NAME, RscTools.empty());
			}
		}
		return new Status(IStatus.ERROR, CredibilityFrameworkConstants.CF_PLUGIN_NAME,
				RscTools.getString(RscConst.EX_YMLVALIDATOR_NOTVALID));

	}

}
