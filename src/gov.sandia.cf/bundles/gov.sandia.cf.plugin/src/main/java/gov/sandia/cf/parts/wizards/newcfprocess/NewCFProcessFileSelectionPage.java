/*************************************************************************************************************
See LICENSE file at <a href="https://gitlab.com/CredibilityFramework/cf/-/blob/master/LICENSE">CF LICENSE</a>}
*************************************************************************************************************/
package gov.sandia.cf.parts.wizards.newcfprocess;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

import gov.sandia.cf.tools.FileTools;
import gov.sandia.cf.tools.RscConst;
import gov.sandia.cf.tools.RscTools;
import gov.sandia.cf.tools.WorkspaceTools;

/**
 * A newWizard page to create a new credibility process extending the eclipse
 * WizardNewFileCreationPage class
 * 
 * @author Didier Verstraete
 *
 */
public class NewCFProcessFileSelectionPage extends WizardNewFileCreationPage implements INewCFProcessFileSelectionPage {

	/**
	 * @param selection the selection to work with
	 */
	public NewCFProcessFileSelectionPage(IStructuredSelection selection) {
		super(RscTools.getString(RscConst.MSG_NEWCFPROCESS_FILESELECTION_PAGE_PAGENAME), selection);
		setTitle(RscTools.getString(RscConst.MSG_NEWCFPROCESS_FILESELECTION_PAGE_TITLE));
		setDescription(RscTools.getString(RscConst.MSG_NEWCFPROCESS_FILESELECTION_PAGE_DESCRIPTION));
	}

	/**
	 * @return the selected file name with credibility extension
	 */
	public String getSelectedFilename() {
		return getFileName() + FileTools.DOT + FileTools.CREDIBILITY_FILE_EXTENSION;
	}

	@Override
	public boolean canFlipToNextPage() {
		// can flip to next page if the cf file already exists
		File cfFile = null;
		if (getContainerFullPath() != null && getSelectedFilename() != null) {
			cfFile = new File(WorkspaceTools.toOsPath(getContainerFullPath().append(getSelectedFilename())));
		}
		boolean cfFileAlreadyExists = (cfFile != null && cfFile.exists());

		if (cfFileAlreadyExists) {
			setErrorMessage(
					RscTools.getString(RscConst.ERR_NEWCFPROCESS_FILESELECTION_PAGE_CFFILEALREADYEXISTS, getSelectedFilename()));
		} else {
			setErrorMessage(null);
		}

		return super.canFlipToNextPage() && !cfFileAlreadyExists;
	}

	@Override
	protected String getNewFileLabel() {
		return RscTools.getString(RscConst.MSG_NEWCFPROCESS_FILESELECTION_PAGE_FILENAME);
	}

	@Override
	public boolean isPageComplete() {
		return super.isPageComplete() && isPageValid();
	}

	/**
	 * @return true if the page is valid (cf file does not exist), otherwise false.
	 */
	private boolean isPageValid() {
		File cfFile = new File(WorkspaceTools.toOsPath(getContainerFullPath().append(getSelectedFilename())));
		return !cfFile.exists();
	}

}
